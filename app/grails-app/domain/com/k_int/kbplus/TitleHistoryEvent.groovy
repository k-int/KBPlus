package com.k_int.kbplus

class TitleHistoryEvent {

  Date eventDate
  Set participants

  static hasMany = [ participants:TitleHistoryEventParticipant ]
  static mappedBy = [ participants:'event' ]
}
