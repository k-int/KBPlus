<g:if test="${grailsApplication?.config?.kbplus?.analytics?.code}">
  <r:script type="text/javascript">
      var _gaq = _gaq || [];
      _gaq.push(['_setAccount', '${grailsApplication.config.kbplus.analytics.code}']);
      <g:if test="${params.shortcode != null}">
      _gaq.push(['_setCustomVar',
            1,                     // This custom var is set to slot #1.  Required parameter.
            'Institution',         // The name acts as a kind of category for the user activity.  Required parameter.
            "${params.shortcode}", // This value of the custom variable.  Required parameter.
            2                      // Sets the scope to session-level.  Optional parameter.
         ]);
      </g:if>
      <g:if test="${user?.defaultDash?.shortcode}">
      _gaq.push(['_setCustomVar',
            2,                     // This custom var is set to slot #2.  Required parameter.
            'UserDefaultOrg',         // The name acts as a kind of category for the user activity.  Required parameter.
            "${user?.defaultDash?.shortcode}", // This value of the custom variable.  Required parameter.
            3                      // Sets the scope to page-level.  Optional parameter.
         ]);
      </g:if>
      _gaq.push(['_trackPageview']);
      (function() {
          var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
          ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
          var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
      })();
  </r:script>
</g:if>