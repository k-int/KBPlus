package com.k_int.kbplus

import org.junit.*
import grails.test.mixin.*
import groovy.time.TimeCategory


@TestFor(TitleInstitutionProvider)
@Mock([CoreAssertion,TitleInstitutionProvider,])
class TitleInstitutionProviderTests {

	void testCoreExtendNew(){
		def today = new Date()
		def lastYear 

		use(TimeCategory) {
		    lastYear = today - 1.years
		}

		domain.extendCoreExtent(today,null)
		assertTrue domain.coreDates.size() == 1
		assertTrue domain.coreDates[0].endDate == null && domain.coreDates[0].startDate == today

		domain.extendCoreExtent(lastYear,null)

		assertTrue domain.coreDates.size() == 1
	}
}