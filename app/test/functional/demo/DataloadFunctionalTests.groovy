package demo

import functionaltestplugin.*
import com.grailsrocks.functionaltest.*


class DataloadFunctionalTests extends FunctionalTestCase {

    void testUserCreation() {
        setBrowser("CHROME")
        // setBrowser(BrowserVersion.CHROME_16)
        // Here call get(uri) or post(uri) to start the session
        // and then use the custom assertXXXX calls etc to check the response
        //
        // get('/something')
        // assertStatus 200
        // assertContentContains 'the expected text'
        get('/login')

        assertNotNull page.forms['loginForm']

        form('#loginForm') {
          j_username = "admin"
          j_password = "admin"
          click "submit"
        }

        log.debug("Create user");

        get('/userDetails/create')

        // Create TestUserA - We will assign perms later on
        form('#createUserForm') {
          username='TestUserA';
          display='TestUserA';
          password='password'
          email='TestUserA@InstitutionA.ac.uk'
          click "submit"
        }

    }
}
