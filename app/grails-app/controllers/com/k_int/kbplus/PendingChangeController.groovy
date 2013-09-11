package com.k_int.kbplus

import grails.converters.*
import grails.plugins.springsecurity.Secured
import com.k_int.kbplus.auth.*;

class PendingChangeController {

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def accept() {
    log.debug("Accept");
    redirect(url: request.getHeader('referer'))
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def reject() {
    log.debug("Reject");
    redirect(url: request.getHeader('referer'))
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def acceptAll() {
    redirect(url: request.getHeader('referer'))
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def rejectAll() {
    redirect(url: request.getHeader('referer'))
  }
}
