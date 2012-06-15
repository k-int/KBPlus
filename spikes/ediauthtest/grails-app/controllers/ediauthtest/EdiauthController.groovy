package ediauthtest

class EdiauthController {

  def index() { 
    log.debug("EdiauthController::index");
  }

  def go() {
    log.debug("EdiauthController::go");
    redirect(controller:'login', action:'ediauthResponse', params:params);
  }
}
