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

class DocstoreController {

  def docstoreService

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() { 
    def doc = Doc.findByUuid(params.id);
    if ( doc ) {
      switch ( doc.contentType ) {
        case 0:
          break;
        case 1:
          docstoreService.retrieve(params.id, response, doc.mimeType, doc.filename);
          break;
        case 3:
          doc.render(response)
          break;
      }
    }
  }
}
