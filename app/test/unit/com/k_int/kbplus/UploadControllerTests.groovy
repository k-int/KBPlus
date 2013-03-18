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
        message:'Testing SO Name composed only of spaces']
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
        def result = controller.readSubscriptionOfferedCSV(input_stream, 'filename.csv')
        // Need to mock domain objects for tests
        controller.validate(result);
        assert result.processFile == so_test_case.shouldProcess
      }
    }
}
