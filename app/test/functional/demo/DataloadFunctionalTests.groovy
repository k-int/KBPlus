package demo

import com.grailsrocks.functionaltest.*

class DataloadFunctionalTests extends BrowserTestCase {
    void testSomeWebsiteFeature() {
        // Here call get(uri) or post(uri) to start the session
        // and then use the custom assertXXXX calls etc to check the response
        //
        // get('/something')
        // assertStatus 200
        // assertContentContains 'the expected text'
        get('/login')

        form('loginForm') {
          j_username = "admin"
          j_password = "admin"
          click "submit"
        }
    }
}
