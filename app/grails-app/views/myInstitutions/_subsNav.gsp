<ul class="nav nav-pills">
  <li class="${actionName=='currentSubscriptions'?'active':''}"><g:link controller="myInstitutions" 
                             action="currentSubscriptions" 
                             params="${[shortcode:params.shortcode]}">Current Subscriptions</g:link></li>

  <li class="${actionName=='addSubscription'?'active':''}"><g:link controller="myInstitutions" 
              action="addSubscription" 
              params="${[shortcode:params.shortcode]}">New Subscription (via Package)</g:link></li>
              
  <li class="${actionName=='emptySubscription'?'active':''}"><g:link controller="myInstitutions" 
              action="emptySubscription" 
              params="${[shortcode:params.shortcode]}">New Subscription (Empty)</g:link></li>

</ul>

