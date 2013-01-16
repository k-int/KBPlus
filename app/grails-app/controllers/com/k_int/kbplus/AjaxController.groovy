package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured
import grails.converters.*

class AjaxController {

  def refdata_config = [
    'ContentProvider' : [
      domain:'Org',
      countQry:'select count(o) from Org as o where lower(o.name) like ?',
      rowQry:'select o from Org as o where lower(o.name) like ? order by o.name asc',
      qryParams:[
                  [
                    param:'sSearch',
                    clos:{ value ->
                      def result = '%'
                      if ( value && ( value.length() > 0 ) )
                        result = "%${value.trim().toLowerCase()}%"
                      result
                    }
                  ]
                ],
      cols:['name'],
      format:'map'
    ],
    'PackageType' : [
      domain:'RefdataValue',
      countQry:"select count(rdv) from RefdataValue as rdv where rdv.owner.desc='Package Type'",
      rowQry:"select rdv from RefdataValue as rdv where rdv.owner.desc='Package Type'",
      qryParams:[],
      cols:['value'],
      format:'simple'
    ],
    'CoreStatus' : [
      domain:'RefdataValue',
      countQry:"select count(rdv) from RefdataValue as rdv where rdv.owner.desc='CoreStatus'",
      rowQry:"select rdv from RefdataValue as rdv where rdv.owner.desc='CoreStatus'",
      qryParams:[],
      cols:['value'],
      format:'simple'
    ]
  ]


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def setValue() {
    // [id:1, value:JISC_Collections_NESLi2_Lic_IOP_Institute_of_Physics_NESLi2_2011-2012_01012011-31122012.., type:License, action:inPlaceSave, controller:ajax
    // def clazz=grailsApplication.domainClasses.findByFullName(params.type)
    log.debug("setValue ${params}");
    def domain_class=grailsApplication.getArtefact('Domain',"com.k_int.kbplus.${params.type}")
    if ( domain_class ) {
      def instance = domain_class.getClazz().get(params.id) 
      if ( instance ) {
        log.debug("Got instance ${instance}");
        def binding_properties = [ "${params.elementid}":params.value ]
        log.debug("Merge: ${binding_properties}");
        // see http://grails.org/doc/latest/ref/Controllers/bindData.html
        if ( binding_properties[params.elementid] == '__NULL__' ) {
          binding_properties[params.elementid] = null;
        }
        bindData(instance, binding_properties)
        instance.save(flush:true);
      }
      else {
        log.debug("no instance");
      }
    }
    else {
      log.debug("no type");
    }

    response.setContentType('text/plain')
    def outs = response.outputStream
    outs << params.value
    outs.flush()
    outs.close()
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def setRef() {
    def rdv = RefdataCategory.lookupOrCreate(params.cat, params.value)
    def domain_class=grailsApplication.getArtefact('Domain',"com.k_int.kbplus.${params.type}")
    if ( domain_class ) {
      def instance = domain_class.getClazz().get(params.id)
      if ( instance ) {
        log.debug("Got instance ${instance}");
        // Lookup refdata value
        def binding_properties = [ "${params.elementid}":rdv ]
        // see http://grails.org/doc/latest/ref/Controllers/bindData.html
        bindData(instance, binding_properties)
        instance.save(flush:true);
      }
      else {
        log.debug("no instance");
      }
    }
    else {
      log.debug("no type");
    }

    response.setContentType('text/plain')
    def outs = response.outputStream
    if ( rdv.icon ) {
      outs << "<span class=\"select-icon ${rdv.icon}\">&nbsp;</span><span>${rdv.value}</span>"
    }
    else {
      outs << "<span>${params.value}</span>"
    }
    outs.flush()
    outs.close()

  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def setFieldNote() {
    def domain_class=grailsApplication.getArtefact('Domain',"com.k_int.kbplus.${params.type}")
    if ( domain_class ) {
      def instance = domain_class.getClazz().get(params.id)
      if ( instance ) {
        if ( params.elementid?.startsWith('__fieldNote_') ) {
          def note_domain = params.elementid.substring(12)
          instance.setNote(note_domain, params.value);
          instance.save(flush:true)
        }
      }
    }
    else {
      log.error("no type");
    }

    response.setContentType('text/plain')
    def outs = response.outputStream
    outs << params.value
    outs.flush()
    outs.close()
  }


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def genericSetValue() {
    // [id:1, value:JISC_Collections_NESLi2_Lic_IOP_Institute_of_Physics_NESLi2_2011-2012_01012011-31122012.., type:License, action:inPlaceSave, controller:ajax
    // def clazz=grailsApplication.domainClasses.findByFullName(params.type)
    log.debug("genericSetValue:${params}");

    // params.elementid (The id from the html element)  must be formed as domain:pk:property:otherstuff
    String[] oid_components = params.elementid.split(":");

    def domain_class=grailsApplication.getArtefact('Domain',"com.k_int.kbplus.${oid_components[0]}")
    def result = params.value

    if ( domain_class ) {
      def instance = domain_class.getClazz().get(oid_components[1])
      if ( instance ) {

        def value = params.value;
        if ( value == '__NULL__' ) {
           value=null;
           result='';
        }
        else {
          if ( params.dt == 'date' ) {
            log.debug("Special date processing, idf=${params.idf}");
            def formatter = new java.text.SimpleDateFormat(params.idf)
            value = formatter.parse(params.value)
            if ( params.odf ) {
              def of = new java.text.SimpleDateFormat(params.odf)
              result=of.format(value);
            }
            else {
              result=value.toString();
            }
          }
        }
        log.debug("Got instance ${instance}");
        def binding_properties = [ "${oid_components[2]}":value ]
        log.debug("Merge: ${binding_properties}");
        // see http://grails.org/doc/latest/ref/Controllers/bindData.html
        bindData(instance, binding_properties)
        instance.save(flush:true);
      }
      else {
        log.debug("no instance");
      }
    }
    else {
      log.debug("no type");
    }

    response.setContentType('text/plain')
    def outs = response.outputStream
    outs << result
    outs.flush()
    outs.close()
  }


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def genericSetRef() {
    // [id:1, value:JISC_Collections_NESLi2_Lic_IOP_Institute_of_Physics_NESLi2_2011-2012_01012011-31122012.., type:License, action:inPlaceSave, controller:ajax
    // def clazz=grailsApplication.domainClasses.findByFullName(params.type)
    log.debug("genericSetRef ${params}");

    // params.elementid (The id from the html element)  must be formed as domain:pk:property:refdatacat:otherstuff
    String[] oid_components = params.elementid.split(":");

    def domain_class=grailsApplication.getArtefact('Domain',"com.k_int.kbplus.${oid_components[0]}")
    def result = params.value


    if ( domain_class ) {
      def instance = domain_class.getClazz().get(oid_components[1])
      if ( instance ) {
        log.debug("Got instance ${instance}");
        def rdv = RefdataCategory.lookupOrCreate(oid_components[3], params.value)
        def binding_properties = [ "${oid_components[2]}":rdv ]
        log.debug("Merge: ${binding_properties}");
        // see http://grails.org/doc/latest/ref/Controllers/bindData.html
        bindData(instance, binding_properties)
        instance.save(flush:true);
      }
      else {
        log.debug("no instance");
      }
    }
    else {
      log.debug("no type");
    }

    response.setContentType('text/plain')
    def outs = response.outputStream
    outs << result
    outs.flush()
    outs.close()
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def genericSetRel() {
    // [id:1, value:JISC_Collections_NESLi2_Lic_IOP_Institute_of_Physics_NESLi2_2011-2012_01012011-31122012.., type:License, action:inPlaceSave, controller:ajax
    // def clazz=grailsApplication.domainClasses.findByFullName(params.type)
    log.debug("genericSetRel ${params}");

    // params.elementid (The id from the html element)  must be formed as domain:pk:property:refdatacat:otherstuff
    String[] target_components = params.elementid.split(":");
    String[] value_components = params.value.split(":");

    def target=resolveOID(target_components);
    def value=resolveOID(value_components);

    def result = null

    if ( target && value ) {
      def binding_properties = [ "${target_components[2]}":value ]
      bindData(target, binding_properties)
      target.save(flush:true);
      if ( params.resultProp ) {
        result = value[params.resultProp]
      }
      else {
        result = value.toString()
      }
    }
    else {
      log.debug("no type (target=${target_components}, value=${value_components}");
    }

    response.setContentType('text/plain')
    def outs = response.outputStream
    outs << result
    outs.flush()
    outs.close()
  }

  def resolveOID(oid_components) {
    def result = null;

    def domain_class=null;

    if ( oid_components[0].startsWith("com.k_int.kbplus") ) 
      domain_class = grailsApplication.getArtefact('Domain',oid_components[0])
    else 
      domain_class = grailsApplication.getArtefact('Domain',"com.k_int.kbplus.${oid_components[0]}")


    if ( domain_class ) {
      result = domain_class.getClazz().get(oid_components[1])
    }
    else {
      log.error("resolve OID failed to identify a domain class. Input was ${oid_components}");
    }
    result
  }

  def orgs() {
    log.debug("Orgs: ${params}");

    def result = [
      options:[]
    ]

    def query_params = ["%${params.query.trim().toLowerCase()}%"];

    log.debug("q params: ${query_params}");

    // result.options = Org.executeQuery("select o.name from Org as o where lower(o.name) like ? order by o.name desc",["%${params.query.trim().toLowerCase()}%"],[max:10]);
    def ol = Org.executeQuery("select o from Org as o where lower(o.name) like ? order by o.name asc",query_params,[max:10,offset:0]);

    ol.each {
      result.options.add(it.name);
    }

    render result as JSON
  }

  def validatePackageId() {
    def result = [:]
    result.response = false;
    if( params.id ) {
      def p = Package.findByIdentifier(params.id)
      if ( !p ) {
        result.response = true
      }
    }

    render result as JSON
  }
  
  def refdataSearch() {

    // log.debug("refdataSearch params: ${params}");
    
    // http://datatables.net/blog/Introducing_Scroller_-_Virtual_Scrolling_for_DataTables
    def result = [:]



    
    def config = refdata_config[params.id]
    if ( config ) {

      // result.config = config

      def query_params = []
      config.qryParams.each { qp ->
        // log.debug("Processing query param ${qp} value will be ${params[qp.param]}");
        if ( qp.clos ) {
          query_params.add(qp.clos(params[qp.param]?:''));
        }
        else {
          query_params.add(params[qp.param]);
        }
      }

      log.debug("Params: ${query_params}");
      log.debug("Count qry: ${config.countQry}");
      log.debug("Row qry: ${config.rowQry}");

      def cq = Org.executeQuery(config.countQry,query_params);    

      def rq = Org.executeQuery(config.rowQry,
                                query_params,
                                [max:params.iDisplayLength?:10,offset:params.iDisplayStart?:0]);

      if ( config.format=='map' ) {
        result.aaData = []
        result.sEcho = params.sEcho
        result.iTotalRecords = cq[0]
        result.iTotalDisplayRecords = cq[0]
    
        rq.each { it ->
          int ctr = 0;
          def row = [:]
          config.cols.each { cd ->
            // log.debug("Processing result col ${cd} pos ${ctr}");
            row["${ctr++}"] = it[cd]
          }
          row["DT_RowId"] = "${it.class.name}:${it.id}"
          result.aaData.add(row)
        }
      }
      else {
        rq.each { it ->
          result["${it.class.name}:${it.id}"] = it[config.cols[0]];
        }
      }
    }

    withFormat {
      html {
        result
      }
      json {
        render result as JSON
      }
    }
  }

  def addOrgRole() {
    log.debug("addOrgRole ${params}");
    def org_to_link = resolveOID(params.orm_orgoid?.split(":"))
    def owner = resolveOID(params.parent?.split(":"))
    def rel = RefdataValue.get(params.orm_orgRole);

    log.debug("Add link to ${org_to_link} from ${owner} rel is ${rel} recip_prop is ${params.recip_prop}");
    def new_link = new OrgRole(org:org_to_link,roleType:rel)
    new_link[params.recip_prop] = owner
    if ( new_link.save(flush:true) ) {
      log.debug("Org link added");
    }
    else {
      log.error("Problem saving new org link...");
      new_link.errors.each { e ->
        log.error(e);
      }
    }

    redirect(url: request.getHeader('referer'))
  }

  def delOrgRole() {
    log.debug("delOrgRole ${params}");
    def or = OrgRole.get(params.id)
    or.delete(flush:true);
    log.debug("Delete link: ${or}");
    redirect(url: request.getHeader('referer'))
  }

}
