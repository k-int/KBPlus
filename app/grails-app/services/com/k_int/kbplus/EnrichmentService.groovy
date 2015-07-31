package com.k_int.kbplus

import com.k_int.kbplus.*
import org.hibernate.ScrollMode
import java.nio.charset.Charset
import java.util.GregorianCalendar
import org.gokb.GOKbTextUtils
import groovy.text.SimpleTemplateEngine

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import groovy.text.Template 
import groovy.text.SimpleTemplateEngine


class EnrichmentService implements ApplicationContextAware {

  ApplicationContext applicationContext

  def executorService
  def grailsApplication
  def mailService
  def sessionFactory
  def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP

  def initiateHousekeeping() {
    log.debug("initiateHousekeeping");
    def future = executorService.submit({
      doHousekeeping()
    } as java.util.concurrent.Callable)
    log.debug("initiateHousekeeping returning");
  }

  def doHousekeeping() {
    try {
      def result = [:]
      result.possibleDuplicates = []
      result.packagesInLastWeek = []
      doDuplicateTitleDetection(result)
      addPackagesAddedInLastWeek(result)
      sendEmail(result)
    }
    catch ( Exception e ) {
      log.error("Problem in housekeeping",e);
    }
  }

  def doDuplicateTitleDetection(result) {
    log.debug("Duplicate Title Detection");
    def initial_title_list = TitleInstance.executeQuery("select title.id, title.normTitle from TitleInstance as title order by title.id asc");
    initial_title_list.each { title ->
      // Compare this title against every other title
      def inner_title_list = TitleInstance.executeQuery("select title.id, title.normTitle from TitleInstance as title where title.id > ? order by title.id asc", title[0]);
      inner_title_list.each { inner_title ->
        def similarity = GOKbTextUtils.cosineSimilarity(title[1], inner_title[1])
        if ( similarity > ( ( grailsApplication.config.cosine?.good_threshold ) ?: 0.925 ) ) {
          log.debug("Possible Duplicate:  ${title[1]} and ${inner_title[1]} : ${similarity}");
          result.possibleDuplicates.add([title[0], title[1], inner_title[0], inner_title[1],similarity]);
        }
      }
    }
  }

  def addPackagesAddedInLastWeek(result) {
    def last_week = new Date(System.currentTimeMillis() - (1000*60*60*24*7))
    def packages_in_last_week = Package.executeQuery("select p from Package as p where p.dateCreated > ? order by p.dateCreated",[last_week])
    packages_in_last_week.each {
      result.packagesInLastWeek.add(it);
    }
  }

  def sendEmail(result) {

    log.debug("sendEmail....");
    def emailTemplateFile = applicationContext.getResource("WEB-INF/mail-templates/housekeeping.gsp").file
    def engine = new SimpleTemplateEngine()
    def tmpl = engine.createTemplate(emailTemplateFile).make(result)
    def content = tmpl.toString()

    mailService.sendMail {
      to 'ian.ibbotson@k-int.com'
      from 'ian.ibbotson@k-int.com'
      subject 'KBPlus Housekeeping Results'
      html content
    }
  }

  def initiateCoreMigration() {
    log.debug("initiateCoreMigration");
    def future = executorService.submit({
      log.debug("Submit job....");
      doCoreMigration()
    } as java.util.concurrent.Callable)
    log.debug("initiateCoreMigration returning");
  }

  def doCoreMigration() {
    log.debug("Running core migration....");
    try {
      def ie_ids_count = IssueEntitlement.executeQuery('select count(ie.id) from IssueEntitlement as ie')[0];
      def ie_ids = IssueEntitlement.executeQuery('select ie.id from IssueEntitlement as ie');
      def start_time = System.currentTimeMillis();
      int counter=0

      ie_ids.each { ieid ->

        IssueEntitlement.withNewTransaction {

          log.debug("Get ie ${ieid}");

          def ie = IssueEntitlement.get(ieid);

          if ( ( ie != null ) && ( ie.subscription != null ) && ( ie.tipp != null ) ) {

            def elapsed = System.currentTimeMillis() - start_time
            def avg = counter > 0 ? ( elapsed / counter ) : 0
            log.debug("Processing ie_id ${ieid} ${counter++}/${ie_ids_count} - ${elapsed}ms elapsed avg=${avg}");
            def inst = ie.subscription.getSubscriber()
            def title = ie.tipp.title
            def provider = ie.tipp.pkg.getContentProvider()
    
            if ( inst && title && provider ) {
              def tiinp = TitleInstitutionProvider.findByTitleAndInstitutionAndprovider(title, inst, provider)
              if ( tiinp == null ) {
                log.debug("Creating new TitleInstitutionProvider");
                tiinp = new TitleInstitutionProvider(title:title, institution:inst, provider:provider).save(flush:true, failOnError:true)
              }
        
              log.debug("Got tiinp:: ${tiinp}");
              if ( ie.coreStatusStart != null ) {
                tiinp.extendCoreExtent(ie.coreStatusStart, ie.coreStatusEnd );
              }
              else {
                log.debug("No core start date - skip");
              }
            }
            else {
              log.error("Missing title(${title}), provider(${provider}) or institution(${inst})");
            }
          }
          else {
            log.error("IE ${ieid} is null, has no subscription or tipp.");
          }
        }

        if ( counter % 5000 == 0 ) {
          log.debug("Clean up gorm");
          cleanUpGorm();
        }

      }
    }
    catch ( Exception e ) {
      log.error("Problem",e);
    }
  }

  def cleanUpGorm() {
    log.debug("Clean up GORM");
    def session = sessionFactory.currentSession
    session.flush()
    session.clear()
    propertyInstanceMap.get().clear()
  }

}
