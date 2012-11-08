<!doctype html>
<html>
  <head>
    <meta name="layout" content="pubbootstrap"/>
    <title>About | Knowledge Base+</title>
  </head>

    <body class="public">
        <div class="navbar navbar-fixed-top">
            <div class="navbar-inner navbar-public">
                <div class="container">
                    <img class="brand" alt="Knowledge Base + logo" src="images/kb_large_icon.png" />
                    <div class="nav-collapse">               
                    <ul class="nav">
                            <li>
                                <a href="${createLink(uri: '/')}"> Home </a>
                            </li>
                            <li class="active">
                                <a href="${createLink(uri: '/about')}"> About KB+ </a>
                            </li>
                            <li>
                                <a href="${createLink(uri: '/signup')}"> Sign Up </a>
                            </li>
                            <li>
                                <a href="${createLink(uri: '/publicExport')}"> Exports </a>
                            </li>
                            <li class="last">
                                <a href="${createLink(uri: '/contact-us')}"> Contact Us </a>
                            </li>
                        </ul>            
                    </div>                     
                </div>           
            </div>
        </div>

        <div class="navbar-push-public"></div>

        <div class="container">
            <h1>About KB+</h1>
        </div>

        <div class="container">
            <div class="row">
                <div class="span8">
<p>Knowledge Base+ is a new shared service from JISC Collections launching in the autumn that aims to help UK libraries manage their e-resources more efficiently.</p>
<p>Over the course of the last year HEFCE has invested significantly in the creation of a shared service knowledge base for UK academic libraries to support the management of e-resources by the academic community – Knowledge Base+ (KB+)</p>
<p>JISC Collections has been leading this work on behalf of HEFCE and JISC, drawing on its own knowledge and experience in the field of licensing and negotiation as well as the work initiated by SCONUL and JISC to identify the e-resource management requirements of UK academic institutions.</p>
<p>As we near the end of phase 1 of service development and the initial release of KB+, now is a good moment to look at the issues KB+ has been set up to address, the approach we’ve taken, what will be available at launch and our thoughts on where the service will go next.</p>

<h2>Why are we doing this?</h2>
<p>Recently I was talking with a librarian who was faced with an embarrassing question from a publisher who wanted to know which of their journals her library subscribed to, because they weren’t sure. Unfortunately neither she, nor her colleagues, nor her subscription agent knew the answer either.</p> 
<p>The information must have been available somewhere, it just wasn’t anywhere easy for anyone you might need or expect to know to find.</p>
<p>When I’ve told this story to other librarians, the reaction is not one of amazement, but recognition, because unfortunately the quality of data and metadata about publications, packages, subscriptions, entitlements and licences that is available throughout the e-resource supply chain is not what it should or could be.</p>
<p>Incorrect data, in the wrong formats, is in the wrong places and is difficult to share between the different systems that libraries rely on to provide access to e-resources for their users, with the result that both within individual libraries and across academic libraries as a whole too much time is being wasted correcting and maintaining basic e-resource information and not enough time is left to undertake the sort of decision making that academic libraries would like to be doing to improve services for their users.</p> 
<p>These are the challenges facing libraries that KB+ has been established to start addressing.</p>

<h2>How are we going to do it?</h2>
<ol>
<li>
	<p>Focus on the data - The first decision we made was to focus on the quality and accuracy of the data that was important to libraries – publication information, the content and coverage of packages, historical entitlement information and licence data – and to make that data available across the supply chain to libraries, publishers, subscription agents, systems vendors and anyone else who needed it in order to improve the quality of their knowledge bases or internal records.</p>
</li>
<li>
	<p>Be Open - In order to maximise the impact of our work, demonstrate neutrality and achieve value for money wherever possible we have adopted a policy of openness.</p>
	<p>This means that wherever possible we have used open source software, and will make any software developed by KB+ available under an appropriate open source licence. In addition all of the data created by the project will be made openly available under a Creative Commons licence (though institutions will choose what local data is included).</p>
	<p>KB+ is also committed to working in partnership with groups throughout the supply chain. We have a very close relationship with SCONUL, who did so much to represent the requirements and frustrations of library directors on these issues and with the project board as well as its community and technical advisory groups are made up of institutional representatives to ensure we deliver against those requirements. Sector involvement and collaboration is a core aspect of KB+ and it model for shared community activity.</p>
	<p>We are working with international projects such as GOKb, funded by the Andrew W. Mellon Foundation in the US, in the hope that a shared understanding of the issues, as well as shared data models and technical approaches can deliver benefits and efficiencies to all by minimising the burden on any one set of institutions.</p>
	<p>We are also working closely with existing JISC services at EDINA, JISC Collections, MIMAS and the UK Federation to benefit from their knowledge and the work that has been put into JUSP, SUNCAT, elcat and identity management.</p>
	<p>Finally, we understand that institutions have long standing and important relationships with their systems vendors and subscription agents, so we’ll be making the data in KB+ available to those organisations so that they are able to improve the services that they provide to academic libraries.</p>
</li>
<li>
	<p>Standards and Best Practice - A practical implication of this focus on the quality and sharing of data is to make use of existing standards and best practice wherever possible. For KB+, the KBART recommendations provided a natural starting point for the collection and normalisation of publication information so that it would be of use to knowledge base vendors and others. KB+ also makes use of work done by JISC Collections to encode licences in ONIX-PL, by EDINA with the international ISSN Registry and by MIMAS to harvest COUNTER compliant usage statistics using SUSHI.</p>
	<p>Where available KB+ uses standard identifiers to help improve the quality of the data and support exchange of data with others both now and in the future. In this first phase of development the project makes use of ISSNs, Ringgold institutional IDs and entity IDs from the UK Federation, all of which mean that KB+ doesn’t need to duplicate work undertaken elsewhere.</p>
	<p>This should lay the foundations for KB+ to be interoperable with any other services and initiatives throughout the supply chain that have also invested in compliance with standards and best practice.</p> 
</li>
</ol>
<p>All of this is underpinned by the simple idea of ‘Do once and share’. If KB+, a library, a systems vendor or a publisher can do something once and then share it, then there are savings in time, cost and effort for all.</p>

<h2>What will KB+ look like at launch?</h2>
<p>This September we will be launching the first phase of KB+. At its core will be a centrally maintained knowledgebase, where JISC Collections will undertake the verification, normalisation, updating and sharing of data held in KB+.</p>

<h2>What data will be included?</h2>
<ul>
<li>Publication Data: title, package, platform and coverage information for all NESLi2, SHEDL and WHEEL agreements, about 12,000 titles in all.</li>
<li>Subscription Information: there will be information on the subscription arrangements for those agreements including renewal dates and notice periods. Wherever available we have also included information on institutional participation in those agreements and local institutional entitlements (a challenging piece of work!), whilst institutions can also amend this information themselves.</li>
<li>Licence information: KB+ will capture a number of key permissions associated with the agreements mentioned above, covering Walk-In Users, Course Packs, ILL, concurrent users, remote access, post-cancellation access, partner, alumni, SME and multi-site access.</li>
</ul>
<p>The system also allows institutions to upload their own licence documents in any format and any associated notes and documents as well as linking to the full version of the licence in elcat.</p>
<p>Usage statistics: KB+ will also be importing usage statistics from JUSP as they become available.</p>

<h2>What can institutions do?</h2>
<p>With KB+ we are starting to build an infrastructure not only to help UK academic libraries manage their e-resources more effectively, but also to share in and benefit from the community’s combined knowledge and expertise. So in addition to the central maintenance undertaken by JISC Collections and alongside the ability to compare data and generate reports, libraries will be able to post, access and share documents, notes and alerts across the system as a whole. This sharing of community knowledge and expertise is central to the success of KB+ as a whole, because it is our belief that it is only through shared activity that some of the challenges we all face can be overcome.</p>

<h2>What will we be doing from September onwards?</h2>
<p>As mentioned above, the September 2012 release of KB+ is an initial release based around a limited set of core requirements and functionality. In its first year of service KB+ will be freely available as we seek to develop and refine the service, but we’ve already starting planning for and working on the next phase of development to take place from September onwards, which will fall into two main streams of activity:</p>
<ol>
<li>More data – at launch KB+ will provide information on an important subset of the resources that libraries are currently managing, but we will be adding data on more non-NESLi2 e-journals, full text databases, e-books and open access publications in order to make coverage as comprehensive as possible for UK libraries. We’ll also be undertaking a project to gather more comprehensive information on institutional holdings and entitlements so that KB+ can be pre-populated with as much institutional data as possible.</li>
<li>Workflows and KB+ - at present we have a group of institutions working with us to define the workflows that KB+ should support in order to be most useful to institutions. These are issues around institutional cycles concerning the review and analysis of agreements, day to day tasks, updates and changes to the data in KB+, changes to publisher information and the import and export of data.</li>
</ol>
<p>Whilst the exact nature of the work has yet to be defined, what is certain is that it will be based on feedback from institutions as they use KB+ and start to derive value from it.</p>

<h2>The Impact of KB+</h2>
<p>I started this piece by asking the question of why we’re doing KB+ and I would like to end by looking at what impact we hope KB+ will have.</p>
<ol>
<li>Improving the reliability of the e-library – making better quality and more accurate information available to the services that libraries use can only help improve the quality of service that libraries provide to their end users.</li>
<li>Reducing time and cost spent managing data that underpins ERM – it doesn’t seem like the best use of academic librarians’ time to have them all maintaining the same data, in the same products when that work could either be done once centrally and shared or co-ordinated across the wider academic library community both nationally and internationally.</li>
<li>Improve decision making – the corollary of reducing the time and effort spent managing the data, is that librarians can get on with the job of making decisions based on that data, developing collections and improving services for their users.</li>
<li>Ensuring the value of vendor knowledge bases that drive library systems – if KB+ can improve the supply and quality of data that goes into vendor knowledge bases, then libraries will benefit from an improved return on the investment they make in those systems.</li>
<li>Putting institutions in control of their data across systems and services – one of the inspirations for KB+ was the challenge libraries faced managing different versions of ‘their’ data in different silos – be it the publisher, the link resolver knowledge base, their subscription agent, or indeed their own local systems. KB+ has an opportunity to provide one central source of institutional data that is shared across all stakeholders as required.</li>
<li>Transform the culture and practice of ERM across the supply chain – KB+ came about because libraries decided the current ways of managing e-resources weren’t working well enough and that shared activity at the community level was the only way to deliver both the quality of service and efficiencies that were required of the sector. KB+ is taking the first steps to address those issues through a combination of central community management supported by shared community activity.</li>
</ol>
                </div>
                <div class="span4">
					<div class="well">
						<h2>Login</h2>
						<p><span class="externalLinkIcon"><g:link controller="myInstitutions" action="dashboard">Knowledge Base+ Member Login</g:link></span></p>
					</div>                                    
					<div class="twitter">                    
<script charset="utf-8" src="http://widgets.twimg.com/j/2/widget.js"></script>
<script>
new TWTR.Widget({
  version: 2,
  type: 'profile',
  rpp: 4,
  interval: 30000,
  width: 'auto',
  height: 300,
  theme: {
    shell: {
      background: '#990000',
      color: '#ffffff'
    },
    tweets: {
      background: '#ffffff',
      color: '#222222',
      links: '#005580'
    }
  },
  features: {
    scrollbar: false,
    loop: false,
    live: false,
    behavior: 'all'
  }
}).render().setUser('JISCKBPlus').start();
</script>
<noscript>
<div class="well">
<p><span class="externalLinkIcon"><a rel="external" href="http://twitter.com/JISCKBPlus">Follow Knowledge Base + on Twitter (@JISCKBPlus)</a></span></p>
</div>
</noscript>
					</div>
                </div>
            </div>
        </div>

</html>
