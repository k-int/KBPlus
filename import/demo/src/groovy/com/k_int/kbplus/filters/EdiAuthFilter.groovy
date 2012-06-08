package com.k_int.kbplus.filters

import org.springframework.security.ui.*
import org.springframework.security.context.*
import org.springframework.beans.factory.*
import org.springframework.context.*
import javax.servlet.*
import javax.servlet.http.*

class EdiAuthFilter extends SpringSecurityFilter implements InitializingBean {

	def authenticationManager

	void doFilterHttp(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {

		if (SecurityContextHolder.getContext().getAuthentication() == null) {

			// def userId = request.getParameter("userId")
			// def apiKey = request.getParameter("apiKey")
			// if ( userId && apiKey ) {

			// 	def myAuth = new CustomAppTokenAuthentication(
			// 			name: userId,
			// 			credentials: apiKey,
			// 			principal: userId,
			// 			authenticated: true
			// 	)

			// 	myAuth = authenticationManager.authenticate(myAuth);
			// 	if (myAuth) {
			// 		println "Successfully Authenticated ${userId} in object ${myAuth}"
// 
// 					// Store to SecurityContextHolder
// 					SecurityContextHolder.getContext().setAuthentication(myAuth);
// 				 }
// 			}
// 		}
		chain.doFilter(request, response)
	}

	int getOrder() {
		return FilterChainOrder.REMEMBER_ME_FILTER
	}

	void afterPropertiesSet() {
		def providers = authenticationManager.providers
		// providers.add(customProvider)
		authenticationManager.providers = providers
	}
}
