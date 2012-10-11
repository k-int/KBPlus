package com.k_int.kbplus

import com.k_int.kbplus.auth.User

class Comment implements Comparable {

  static belongsTo = [
    alert:Alert
  ]

  int compareTo(obj) {
    commentDate.compareTo(obj.commentDate)
  }


  Date commentDate
  String comment
  User by

  static mapping = {
    table 'kb_comment'
    id column: 'comm_id'
    alert column:'comm_alert_fk'
    commentDate column:'comm_date'
    comment column:'comm_text'
    by column:'comm_by_user_fk'
  }

  static constraints = {
    alert(nullable:false, blank:false);
    commentDate(nullable:false, blank:false);
    comment(nullable:false, blank:false);
    by(nullable:false, blank:false);
  }

}
