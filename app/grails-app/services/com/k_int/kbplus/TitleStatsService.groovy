package com.k_int.kbplus

class TitleStatsService {

    def getTitleStatsByIds(title_id, org_id) {
      def corresponding_title = Title.Instance.get(title_id)
      def corresponding_org = Org.Instance.get(title_id)
      return getTitleStats(corresponding_title,corresponding_org);
    }

    def getTitleStats(title, org) {
      def result = []
      def existing_details = OrgTitleStats.findByTitleAndOrg(title,org)

      if ( existing_details ) {
        // Timeout after a day
        if ( System.currentTimeMillis() - existing_details.lastRetrievedTimestamp > ( 1000 * 60 * 60 * 24 ) ) {
          updateFromJUSP(existing_details);
        }
      }
      else {
        // no existing details.. Create and fetch
        existing_details = new OrgTitleStats(title:title, org:org, lastRetrievedTimestamp:currentTimeMillis()).save(flush:true);
        updateFromJUSP(existing_details);
      }

      reasult;
    }

  def updateFromJUSP(org_title_details) {
    // JUSP URLs of the form - 
    // https://www.jusp.mimas.ac.uk/api/v1/Journals/Statistics/?jid=13471&sid=2&loginid=cra&startrange=2012-01&endrange=2012-12&granularity=summary   
    
  }
}
