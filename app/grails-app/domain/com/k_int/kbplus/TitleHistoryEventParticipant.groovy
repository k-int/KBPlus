package com.k_int.kbplus

class TitleHistoryEventParticipant {

  def TitleHistoryEvent event
  def TitleInstance participant
  def String participantRole // in/out

  static belongsTo = [ event:TitleHistoryEvent ]
}
