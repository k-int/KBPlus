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
      doCoreMigration()
    } as java.util.concurrent.Callable)
    log.debug("initiateCoreMigration returning");
  }

  def doCoreMigration() {
    def ie_ids = IssueEntitlement.executeQuery('select ie.id from issueEntitlement');
    ie_ids.each { ieid ->
      def ie = IssueEntitlement.get(ieid);
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
        tiinp.extendCoreExtent(ie.coreStatusStart, ie.coreStatusEnd);
      }
    }
  }
}
