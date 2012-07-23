package com.k_int.kbplus

class GenericReconcilerService {

  static transactional = false

  @javax.annotation.PostConstruct
  def init() {
    log.debug("Reconciler Init");
  }

  def reconcile(s1,s2,ruleset) {
    log.debug("GenericReconcilerService::reconcile");
    processNode(s1, s2, ruleset);
  }

  def processNode(s1, s2, ruleset) {
    log.debug("processNode");
  }
}
