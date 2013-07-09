package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import grails.converters.*
import org.elasticsearch.groovy.common.xcontent.*
import groovy.xml.MarkupBuilder
import com.k_int.kbplus.auth.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hslf.model.*;
import java.text.SimpleDateFormat




class ApiController {

  def springSecurityService

  def index() { 
  }

  @Secured(['ROLE_API', 'IS_AUTHENTICATED_FULLY'])
  def uploadBibJson() {
    log.debug("uploadBibJson");
  }
}
