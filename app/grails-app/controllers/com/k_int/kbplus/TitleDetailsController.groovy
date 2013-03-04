package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.auth.*;
import org.apache.log4j.*
import java.text.SimpleDateFormat



class TitleDetailsController {

  @Secured(['ROLE_ADMIN', 'KBPLUS_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def findTitleMatches() { 
    // find all titles by n_title proposedTitle
    def result=[:]
    if ( params.proposedTitle ) {
      log.debug("Looking for matches for proposed title ${params.proposedTitle}");
      def proposed_title_key = com.k_int.kbplus.TitleInstance.generateKeyTitle(params.proposedTitle)
      result.titleMatches=com.k_int.kbplus.TitleInstance.findAllByKeyTitle(proposed_title_key)
    }
    result
  }

}
