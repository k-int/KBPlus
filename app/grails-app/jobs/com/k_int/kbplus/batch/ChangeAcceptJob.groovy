package com.k_int.kbplus.batch
import com.k_int.kbplus.auth.User
import com.k_int.kbplus.RefdataCategory
import com.k_int.kbplus.PendingChange

class ChangeAcceptJob {

  def pendingChangeService
  static triggers = {
   // Delay 20 seconds, run every 10 mins.
   // Cron:: Min Hour DayOfMonth Month DayOfWeek Year
   // Example - every 10 mins 0 0/10 * * * ? 
   // At 5 past 3am every day
   cron name:'changeAcceptJobTrigger', startDelay:180000, cronExpression: "0 5 3 * * ?"
   // cronExpression: "s m h D M W Y"
   //                  | | | | | | `- Year [optional]
   //                  | | | | | `- Day of Week, 1-7 or SUN-SAT, ?
   //                  | | | | `- Month, 1-12 or JAN-DEC
   //                  | | | `- Day of Month, 1-31, ?
   //                  | | `- Hour, 0-23
   //                  | `- Minute, 0-59
   //                  `- Second, 0-59
 }
/**
* Accept pending chnages from master subscriptions on slave subscriptions 
**/
 def execute(){
  log.debug("****Running Change Accept Job****")

  def pending_change_pending_status = RefdataCategory.lookupOrCreate("PendingChangeStatus", "Pending")
  def user = User.findByDisplay("Admin")
  def httpRequestMock = [:]
  httpRequestMock.user = user
  // Get all changes associated with slaved subscriptions
  def subQueryStr = "select pc.id from PendingChange as pc where subscription.isSlaved.value = 'Yes' and ( pc.status is null or pc.status = ? ) order by pc.ts desc"
  def subPendingChanges = PendingChange.executeQuery(subQueryStr, [ pending_change_pending_status ]);
  log.debug(subPendingChanges.size() +" pending changes have been found for slaved subscriptions")
  subPendingChanges.each {
      pendingChangeService.performAccept(it,httpRequestMock)
  }

  def licQueryStr = "select pc.id from PendingChange as pc join pc.license.incomingLinks lnk where lnk.isSlaved.value = 'Yes' and ( pc.status is null or pc.status = ? ) order by pc.ts desc"
  def licPendingChanges = PendingChange.executeQuery(licQueryStr, [ pending_change_pending_status ]);
  log.debug( licPendingChanges.size() +" pending changes have been found for slaved licences")
  licPendingChanges.each {
      pendingChangeService.performAccept(it,httpRequestMock)
  }

  log.debug("****Change Accept Job Complete*****")
 }
}