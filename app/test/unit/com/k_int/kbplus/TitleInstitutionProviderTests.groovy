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

	void testCoreExtendExisting(){
		def today = new Date()
		def yesterday 

		// We ignore changes up to 1 day
		use(TimeCategory){
			yesterday = today - 1.days
		}
		domain.extendCoreExtent(today,null)
		assertTrue domain.coreDates.size() == 1
		domain.extendCoreExtent(yesterday,null)
		
		assertTrue domain.coreDates.size() == 1
		assertTrue domain.coreDates.collect{if (it.startDate == today) return true} == [true]
	}

	void testCoreExtendExistingTwo(){
		def today = new Date()
		def yesterday 

		// We ignore changes up to 1 day
		use(TimeCategory){
			yesterday = today - 3.days
		}
		domain.extendCoreExtent(today,null)
		assertTrue domain.coreDates.size() == 1
		domain.extendCoreExtent(yesterday,null)
		
		assertTrue domain.coreDates.size() == 1
		assertTrue domain.coreDates.collect{if (it.startDate == yesterday) return true} == [true]
	}
}