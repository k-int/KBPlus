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
    ],
    'YN' : [
      domain:'RefdataValue',
      countQry:"select count(rdv) from RefdataValue as rdv where rdv.owner.desc='YN'",
      rowQry:"select rdv from RefdataValue as rdv where rdv.owner.desc='YN'",
      qryParams:[],
      cols:['value'],
      format:'simple'
    ],
    'YNO' : [
      domain:'RefdataValue',
      countQry:"select count(rdv) from RefdataValue as rdv where rdv.owner.desc='YNO'",
      rowQry:"select rdv from RefdataValue as rdv where rdv.owner.desc='YNO'",
      qryParams:[],
      cols:['value'],
      format:'simple'
    ],
    'TIPPStatus' : [
      domain:'RefdataValue',
      countQry:"select count(rdv) from RefdataValue as rdv where rdv.owner.desc='TIPP Status'",
      rowQry:"select rdv from RefdataValue as rdv where rdv.owner.desc='TIPP Status'",
      qryParams:[],
      cols:['value'],
      format:'simple'
    ],
    'ConcurrentAccess' : [
      domain:'RefdataValue',
      countQry:"select count(rdv) from RefdataValue as rdv where rdv.owner.desc='Concurrent Access'",
      rowQry:"select rdv from RefdataValue as rdv where rdv.owner.desc='Concurrent Access'",
      qryParams:[],
      cols:['value'],
      format:'simple'
    ],
    'Licenses' : [
      domain:'License',
      countQry:"select count(l) from License as l",
      rowQry:"select l from License as l",
      qryParams:[],
      cols:['reference'],
      format:'simple'
    ],
    'TIPPStatusReason' : [
      domain:'RefdataValue',
      countQry:"select count(rdv) from RefdataValue as rdv where rdv.owner.desc='Tipp.StatusReason'",
      rowQry:"select rdv from RefdataValue as rdv where rdv.owner.desc='Tipp.StatusReason'",
      qryParams:[],
      cols:['value'],
      format:'simple'
    ],
    'TIPPDelayedOA' : [
      domain:'RefdataValue',
      countQry:"select count(rdv) from RefdataValue as rdv where rdv.owner.desc='TitleInstancePackagePlatform.DelayedOA'",
      rowQry:"select rdv from RefdataValue as rdv where rdv.owner.desc='TitleInstancePackagePlatform.DelayedOA'",
      qryParams:[],
      cols:['value'],
      format:'simple'
    ],
    'TIPPHybridOA' : [
      domain:'RefdataValue',
      countQry:"select count(rdv) from RefdataValue as rdv where rdv.owner.desc='TitleInstancePackagePlatform.HybridOA'",
      rowQry:"select rdv from RefdataValue as rdv where rdv.owner.desc='TitleInstancePackagePlatform.HybridOA'",
      qryParams:[],
      cols:['value'],
      format:'simple'
    ],


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
  def setFieldTableNote() {
    log.debug("setFieldTableNote(${params})")
    def domain_class=grailsApplication.getArtefact('Domain',"com.k_int.kbplus.${params.type}")
    if ( domain_class ) {
      println params.id
      def instance = domain_class.getClazz().get(params.id)
       
      if ( instance ) {
        String temp = '__fieldNote_'+params.name
        if ( temp?.startsWith('__fieldNote_') ) {
          def note_domain = temp.substring(12)
          log.debug("note_domain: " + note_domain +" : \""+ params.value+"\"")
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
              def of = new java.text.SimpleDateFormat(session.sessionPreferences?.globalDateFormat)
              result=of.format(value)
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
  def genericSetRel() {
    // [id:1, value:JISC_Collections_NESLi2_Lic_IOP_Institute_of_Physics_NESLi2_2011-2012_01012011-31122012.., type:License, action:inPlaceSave, controller:ajax
    // def clazz=grailsApplication.domainClasses.findByFullName(params.type)
    log.debug("genericSetRel ${params}");

    // params.elementid (The id from the html element)  must be formed as domain:pk:property:refdatacat:otherstuff
    String[] target_components = params.pk.split(":");
    String[] value_components = params.value.split(":");

    def target=resolveOID(target_components);
    def value=resolveOID(value_components);

    def result = null

    if ( target && value ) {
      def binding_properties = [ "${params.name}":value ]
      bindData(target, binding_properties)
      target.save(flush:true);
      if ( params.resultProp ) {
        result = value[params.resultProp]
      }
      else {
        if ( value ) {
          result = renderObjectValue(value);
          // result = value.toString()
        }
      }
    }
    else {
      log.debug("no type (target=${target_components}, value=${value_components}");
    }

    // response.setContentType('text/plain')
    def resp = [ newValue: result ]
    log.debug("return ${resp as JSON}");
    render resp as JSON
    //def outs = response.outputStream
    //outs << result
    //outs.flush()
    //outs.close()
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

    log.debug("refdataSearch params: ${params}");
    
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

    // log.debug("refdataSearch returning ${result as JSON}");
    withFormat {
      html {
        result
      }
      json {
        render result as JSON
      }
    }
  }

  def sel2RefdataSearch() {

    log.debug("sel2RefdataSearch params: ${params}");
    
    def result = []
    
    def config = refdata_config[params.id]
    if ( config ) {

      def query_params = []
      config.qryParams.each { qp ->
        if ( qp.clos ) {
          query_params.add(qp.clos(params[qp.param]?:''));
        }
        else {
          query_params.add(params[qp.param]);
        }
      }

  //     log.debug("Params: ${query_params}");
  //    log.debug("Count qry: ${config.countQry}");
  //    log.debug("Row qry: ${config.rowQry}");

      def cq = Org.executeQuery(config.countQry,query_params);    
      def rq = Org.executeQuery(config.rowQry,
                                query_params,
                                [max:params.iDisplayLength?:10,offset:params.iDisplayStart?:0]);

      rq.each { it ->
        result.add([value:"${it.class.name}:${it.id}",text:"${it[config.cols[0]]}"]);
      }
    }
    else {
      log.error("No config for refdata search ${params.id}");
    }

    //log.debug("refdataSearch returning ${result as JSON}");
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

  def lookup() {
    log.debug("AjaxController::lookup ${params}");
    def result = [:]
    params.max = params.max ?: 10;
    def domain_class = grailsApplication.getArtefact('Domain',params.baseClass)
    if ( domain_class ) {
      result.values = domain_class.getClazz().refdataFind(params);
    }
    else {
      log.error("Unable to locate domain class ${params.baseClass}");
      result.values=[]
    }
    //result.values = [[id:'Person:45',text:'Fred'],
    //                 [id:'Person:23',text:'Jim'],
    //                 [id:'Person:22',text:'Jimmy'],
    //                 [id:'Person:3',text:'JimBob']]
    render result as JSON
  }

  def addToCollection() {
    log.debug("AjaxController::addToCollection ${params}");

    def contextObj = resolveOID2(params.__context)
    def domain_class = grailsApplication.getArtefact('Domain',params.__newObjectClass)

    if ( domain_class ) {

      if ( contextObj ) {
        log.debug("Create a new instance of ${params.__newObjectClass}");

        def new_obj = domain_class.getClazz().newInstance();

        domain_class.getPersistentProperties().each { p -> // list of GrailsDomainClassProperty
          log.debug("${p.name} (assoc=${p.isAssociation()}) (oneToMany=${p.isOneToMany()}) (ManyToOne=${p.isManyToOne()}) (OneToOne=${p.isOneToOne()})");
          if ( params[p.name] ) {
            if ( p.isAssociation() ) {
              if ( p.isManyToOne() || p.isOneToOne() ) {
                // Set ref property
                log.debug("set assoc ${p.name} to lookup of OID ${params[p.name]}");
                // if ( key == __new__ then we need to create a new instance )
                new_obj[p.name] = resolveOID2(params[p.name])              
              }
              else {
                // Add to collection
                log.debug("add to collection ${p.name} for OID ${params[p.name]}");
                new_obj[p.name].add(resolveOID2(params[p.name]))
              }
            }
            else {
              log.debug("Set simple prop ${p.name} = ${params[p.name]}");
              new_obj[p.name] = params[p.name]
            }
          }
        }

        if ( params.__recip ) {
          log.debug("Set reciprocal property ${params.__recip} to ${contextObj}");
          new_obj[params.__recip] = contextObj
        }

        log.debug("Saving ${new_obj}");
        if ( new_obj.save() ) {
          log.debug("Saved OK");
        }
        else {
          new_obj.errors.each { e ->
            log.debug("Problem ${e}");
          }
        }

      }
      else {
        log.debug("Unable to locate instance of context class with oid ${params.__context}");
      }
    }
    else {
      log.error("Unable to ookup domain class ${params.__newObjectClass}");
    }

    redirect(url: request.getHeader('referer'))
  }

  def resolveOID2(oid) {
    def oid_components = oid.split(':');
    def result = null;
    def domain_class=null;
    domain_class = grailsApplication.getArtefact('Domain',oid_components[0])
    if ( domain_class ) {
      if ( oid_components[1]=='__new__' ) {
        result = domain_class.getClazz().refdataCreate(oid_components)
        log.debug("Result of create ${oid} is ${result}");
      }
      else {
        result = domain_class.getClazz().get(oid_components[1])
      }
    }
    else {
      log.error("resolve OID failed to identify a domain class. Input was ${oid_components}");
    }
    result
  }

  def deleteThrough() {
    log.debug("deleteThrough(${params})");
    def context_object = resolveOID2(params.contextOid)
    def target_object = resolveOID2(params.targetOid)
    if ( context_object."${params.contextProperty}".contains(target_object) ) {
      def otr = context_object."${params.contextProperty}".remove(target_object)
      target_object.delete()
      context_object.save(flush:true);
    }
    redirect(url: request.getHeader('referer'))

  }

  def deleteManyToMany() {
    log.debug("deleteManyToMany(${params})");
    def context_object = resolveOID2(params.contextOid)
    def target_object = resolveOID2(params.targetOid)
    if ( context_object."${params.contextProperty}".contains(target_object) ) {
      context_object."${params.contextProperty}".remove(target_object)
      context_object.save(flush:true);
    }
    redirect(url: request.getHeader('referer'))    
  }


  def editableSetValue() {
    log.debug("editableSetValue ${params}");
    def target_object = resolveOID2(params.pk)
    if ( target_object ) {
      if ( params.type=='date' ) {
        target_object."${params.name}" = params.date('value','yyyy-MM-dd')
      }
      else {
        def binding_properties = [:]
        binding_properties[ params.name ] = params.value
        bindData(target_object, binding_properties)
        // target_object."${params.name}" = params.value
      }
      target_object.save(flush:true);
    }

    response.setContentType('text/plain')
    def outs = response.outputStream
    outs << params.value
    outs.flush()
    outs.close()
  }

  def removeUserRole() {
    def user = resolveOID2(params.user);
    def role = resolveOID2(params.role);
    if ( user && role ) {
      com.k_int.kbplus.auth.UserRole.remove(user,role,true);
    }
    redirect(url: request.getHeader('referer'))    
  }

  /**
   * ToDo: This function is a duplicate of the one found in InplaceTagLib, both should be moved to a shared static utility
   */
  def renderObjectValue(value) {
    def result=''
    if ( value ) {
      switch ( value.class ) {
        case com.k_int.kbplus.RefdataValue.class:
          if ( value.icon != null ) {
            result="<span class=\"select-icon ${value.icon}\"></span>${value.value}"
          }
          else {
            result=value.value
          }
          break;
        default:
          result=value.toString();
          break;
      }
    }
    // log.debug("Result of render: ${value} : ${result}");
    result;
  }

}
