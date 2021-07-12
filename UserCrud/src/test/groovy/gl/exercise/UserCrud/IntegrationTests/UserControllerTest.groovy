package gl.exercise.UserCrud.IntegrationTests

import com.fasterxml.jackson.databind.ObjectMapper
import gl.exercise.UserCrud.controllers.ControllerAdvice
import gl.exercise.UserCrud.controllers.UserController
import gl.exercise.UserCrud.dto.request.UserCreationRequestDTO
import gl.exercise.UserCrud.exceptions.InvalidEmailException
import gl.exercise.UserCrud.services.UserService
import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.http.MediaType.APPLICATION_JSON

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest extends Specification{

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private ObjectMapper objectMapper

    def "when creating a new user response should be 200 and valid UserCreationInfoResponseDTO"() {
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

        when:
            def response = mockMvc.perform(
                    MockMvcRequestBuilders.post('/user')
                            .contentType(APPLICATION_JSON)
                            .content(JsonOutput.toJson(request))
                    )
                    .andReturn()
                    .response

        then:
            response.status == HttpStatus.CREATED.value()

        and:
            with (objectMapper.readValue(response.contentAsString, Map)) {
                it.id
                it.username == request.email
                it.created
                it.modified == it.created
                it.last_login == it.modified
                it.token
                it.isactive == Boolean.TRUE
            }
    }
}