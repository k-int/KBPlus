package com.k_int.kbplus



import grails.test.mixin.*
import org.junit.*
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import grails.test.ControllerUnitTestCase


/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
// @TestFor(UploadController)
class UploadControllerTests extends ControllerUnitTestCase {

    def controller

    UploadControllerTests() {
      super(UploadController)
    }

    void setUp() {
      super.setUp()
      controller = new UploadController()
    }
    
    // Info on mocking up file upload http://roshandawrani.wordpress.com/2011/02/03/grails-mock-testing-a-file-upload/
    void testSomething() {
      // fail "Implement me"
      Resource resource = new ClassPathResource("resources/upload_so_test_001.csv")
      def file = resource.getFile()
      assert file.exists()
      
      // def input_stream = new InputStream()
      // def result = controller.readSubscriptionOfferedCSV(input_stream, 'filename.csv')
    }
}
