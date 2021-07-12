package gl.exercise.UserCrud.UnitTests

import com.fasterxml.jackson.databind.ObjectMapper
import gl.exercise.UserCrud.controllers.ControllerAdvice
import gl.exercise.UserCrud.controllers.UserController
import gl.exercise.UserCrud.dto.request.UserCreationRequestDTO
import gl.exercise.UserCrud.exceptions.InvalidEmailException
import gl.exercise.UserCrud.services.UserService
import groovy.json.JsonOutput
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.http.MediaType.APPLICATION_JSON

class UserControllerTest extends Specification{

    private MockMvc mockMvc

    private UserService userService

    private ObjectMapper objectMapper

    def setup() {
        objectMapper = new ObjectMapper()
        userService= Mock()
        UserController controller = new UserController(userService)
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerAdvice())
                .build()
    }

    def "when attempting to create user with invalid email format, return 400 and message be invalid email format"() {
        given:
            Map phone = [
                    number :'12345',
                    citycode: '67',
                    countrycode: '89'
            ]
            List phones = [phone]
            Map request = [
                    name : 'Dummy Name',
                    email : 'dummy@email.com',
                    password : 'A12a',
                    phones : phones
            ]

            userService.createUser(_ as UserCreationRequestDTO) >> { it -> throw new InvalidEmailException(request.email)}

        when:
        def response = mockMvc.perform(
                MockMvcRequestBuilders.post('/user')
                        .contentType(APPLICATION_JSON)
                        .content(JsonOutput.toJson(request))
                )
                .andReturn()
                .response

        then:
            response.status == HttpStatus.BAD_REQUEST.value()

        and:
            with (objectMapper.readValue(response.contentAsString, Map)) {
                it.message == String.format("Email %s does not have the correct format", request.email)
            }
    }


    //En test de integracion? igual que en el service
    //no mockear nada, solamente usar mockmvc para emular una http request
}