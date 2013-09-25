package com.k_int.kbplus



import grails.test.mixin.*
import org.junit.*
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import grails.test.ControllerUnitTestCase
import grails.test.mixin.domain.DomainClassUnitTestMixin

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(UploadController)
@TestMixin(DomainClassUnitTestMixin)
class UploadControllerTests {

    def controller
    def test_list = [

        [ filename:'resources/upload_so_test_001.csv', 
            shouldProcess: false, 
            message:'Testing missing SO Name'],
        
        [ filename:'resources/upload_so_test_002.csv', 
            shouldProcess: false, 
            message:'Testing SO Name composed only of spaces'],
        
        [ filename:'resources/upload_so_test_003.csv', 
            shouldProcess: false, 
            message:'Package Name missing'],
        
        [ filename:'resources/upload_so_test_004.csv', 
            shouldProcess: false, 
            message:'Testing Package name composed only of spaces'],
        
        [ filename:'resources/upload_so_test_005.csv', 
            shouldProcess: false, 
            message:'invalid start date'],
        
        [ filename:'resources/upload_so_test_006.csv', 
            shouldProcess: false, 
            message:'invalid end date'],
        
        [ filename:'resources/upload_so_test_007.csv', 
            shouldProcess: false, 
            message:'no provider'],
        
        [ filename:'resources/upload_so_test_008.csv', 
            shouldProcess: false, 
            message:'only blank spaces for provider'],
        
        [ filename:'resources/upload_so_test_009.csv', 
            shouldProcess: false, 
            message:'no package id'],
        
        [ filename:'resources/upload_so_test_010.csv', 
            shouldProcess: false, 
            message:'only blank spaces for package id'],
        
        [ filename:'resources/upload_so_test_011.csv', 
            shouldProcess: false, 
            message:'no SO ID'],
        
        [ filename:'resources/upload_so_test_012.csv', 
            shouldProcess: false, 
            message:'only blank spaces for SO ID'],
        
        [ filename:'resources/upload_so_test_013.csv', 
            shouldProcess: false, 
            message:'no TIPP name'],
        
        [ filename:'resources/upload_so_test_014.csv', 
            shouldProcess: false, 
            message:'no ISSN,eISSN,Kbart,DOI'],
        
        [ filename:'resources/upload_so_test_015.csv', 
            shouldProcess: true, 
            message:'only ISSN'],
        
        [ filename:'resources/upload_so_test_016.csv', 
            shouldProcess: true, 
            message:'only eISSN'],
        
        [ filename:'resources/upload_so_test_017.csv', 
            shouldProcess: true, 
            message:'only kbart'],
        
        [ filename:'resources/upload_so_test_018.csv', 
            shouldProcess: true, 
            message:'only DOI'],
        
        [ filename:'resources/upload_so_test_019.csv', 
            shouldProcess: false, 
            message:'host platform url missing'],
        
        [ filename:'resources/upload_so_test_020.csv', 
            shouldProcess: false, 
            message:'host platform url only blank spaces'],
        
        [ filename:'resources/upload_so_test_021.csv', 
            shouldProcess: false, 
            message:'invalid start TIPP date'],
        
        [ filename:'resources/upload_so_test_022.csv', 
            shouldProcess: false, 
            message:'invalid end TIPP date'],
        
        // It's valid to not have a coverage depth
        //
        //[ filename:'resources/upload_so_test_023.csv', 
        //    shouldProcess: false, 
        //    message:'no TIPP coverage depth'],
        
        // [ filename:'resources/upload_so_test_024.csv', 
        //    shouldProcess: false, 
        //    message:'TIPP coverage depth only blank spaces'],
        
        [ filename:'resources/upload_so_test_025.csv', 
            shouldProcess: true, 
            message:'TIPP coverage depth using (selected articles),(abstracts)'],
        
        [ filename:'resources/upload_so_test_000.csv', 
            shouldProcess: true, 
            message:'This should pass']
        
    ];

    void setUp() {
        controller = new UploadController()
    }
    
    // Info on mocking up file upload http://roshandawrani.wordpress.com/2011/02/03/grails-mock-testing-a-file-upload/
    void testMissingSubOfferedName() {
        mockDomain(Package, [
                [identifier: "001"],
                [identifier: "002"],
                [identifier: "003"] ])

        mockDomain(Subscription, [
                [identifier: "001"],
                [identifier: "002"],
                [identifier: "003"] ])

            
        // fail "Implement me"
        test_list.each { so_test_case ->
            //log.debug("${so_test_case.message} Expecting shouldProcess to return ${so_test_case.shouldProcess}")
            Resource resource = new ClassPathResource(so_test_case.filename)
            def file = resource.getFile()
            assert file.exists()
            def input_stream = new FileInputStream(file)
            // def result = controller.readSubscriptionOfferedCSV(input_stream, 'filename.csv')
            // Need to mock domain objects for tests
            // controller.validate(result);
            // assert result.processFile == so_test_case.shouldProcess
        }
    }
}
