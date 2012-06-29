package com.k_int.kbplus

import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured


class AjaxController {

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def inPlaceSave() {
    log.debug("inPlaceSave ${params}");
  }
}
