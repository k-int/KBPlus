package com.k_int.kbplus

import javax.persistence.Transient

class SystemObject {

  String sysId
  String announcementsForumId

  static mapping = {
    id column:'sys_id'
    sysId column:'sys_id_str'
    announcementsForumId column:'sys_ann_forum_id'
  }

  static constraints = {
    sysId(nullable:false, blank:false)
    announcementsForumId(nullable:true, blank:false)
  }

  @Transient
  def getNotificationEndpoints() {
    def result = []
    if (  announcementsForumId != null ) {
      result.add([ service:'zendesk.forum', remoteid:this.announcementsForumId ]);
    }
    result;
  }


  public String toString() {
    sysId
  }
}
