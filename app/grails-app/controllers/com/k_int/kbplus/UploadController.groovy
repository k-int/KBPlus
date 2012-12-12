package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.auth.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;



class UploadController {

  def springSecurityService

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def so() { 
    log.debug("so");
  }
}
