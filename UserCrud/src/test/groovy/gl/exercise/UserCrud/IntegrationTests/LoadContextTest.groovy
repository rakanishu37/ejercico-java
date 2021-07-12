package gl.exercise.UserCrud.IntegrationTests

import gl.exercise.UserCrud.controllers.UserController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class LoadContextTest extends Specification {

    @Autowired (required = false)
    private UserController userController

    def "when context is loaded then all expected beans are created"() {
        expect: "the WebController is created"
        userController
    }
}