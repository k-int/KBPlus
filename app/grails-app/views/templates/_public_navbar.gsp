<div class="navbar navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container">
            <img class="brand" alt="Knowledge Base + logo" src="/${meta(name:"app.name")}/images/kb_large_icon.png"/>

            <ul class="nav">
                <g:if test="${active.equals("home")}">
                    <li id="home" class="active">
                </g:if>
                <g:else><li id="home"></g:else>
                <a href="${createLink(uri: '/')}">Home</a>
            </li>
                <g:if test="${active.equals("about")}">
                    <li id="about" class="active">
                </g:if>
                <g:else>
                    <li id="about">
                </g:else>
                <a href="${createLink(uri: '/about')}">About KB+</a>
            </li>
                <g:if test="${active.equals("signup")}">
                    <li id="signup" class="active">

                </g:if>
                <g:else>
                    <li id="signup">

                </g:else>

                <a href="${createLink(uri: '/signup')}">Sign Up</a>
            </li>
               
          <li class="${active.equals('publicExport')?'active':''}">
                            <a href="${createLink(uri: '/publicExport')}">Exports</a>

            </li>
                <g:if test="${active.equals("contact")}">
                    <li id="contact" class="active">

                </g:if>
                <g:else>
                    <li id="contact">

                </g:else>

                <a href="${createLink(uri: '/contact-us')}">Contact Us</a>
            </li>
            </ul>
        </div>
    </div>
</div>


