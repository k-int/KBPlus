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

  def uploadBibJson() {
    def result=[:]
    log.debug("uploadBibJson");
    log.debug("Auth request from ${request.getRemoteAddr()}");
    if ( request.getRemoteAddr() == '127.0.0.1' ) {
      result.message = "Working...";
    }
    else {
      result.message = "uploadBibJson only callable from 127.0.0.1";
    }
    render result as JSON
  }
}
