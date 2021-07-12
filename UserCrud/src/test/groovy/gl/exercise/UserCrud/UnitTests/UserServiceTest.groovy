package gl.exercise.UserCrud.UnitTests

import gl.exercise.UserCrud.dto.request.PhoneRequestDTO
import gl.exercise.UserCrud.dto.request.UserCreationRequestDTO
import gl.exercise.UserCrud.dto.response.UserCreationInfoResponseDTO
import gl.exercise.UserCrud.exceptions.EmailUnavailableException
import gl.exercise.UserCrud.exceptions.InvalidEmailException
import gl.exercise.UserCrud.exceptions.InvalidPasswordException
import gl.exercise.UserCrud.models.User
import gl.exercise.UserCrud.repositories.UserRepository
import gl.exercise.UserCrud.services.PhoneService
import gl.exercise.UserCrud.services.UserService
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant

class UserServiceTest extends Specification{

    /*
    que tenga lo que conozco y lo otro no sea null/vacio (como cuando le pifie al nombre de la var en postman
    agregar un @notnull?
     */
    PhoneService phoneService;
    UserService userService;
    UserRepository userRepository;

    def setup() {
        phoneService = Mock();
        userRepository = Mock();
        userService = new UserService(userRepository, phoneService);
    }

    //todo uno de integracion?
    def "should return a valid UserInfoResponseDTO"() {
        given:
            userRepository.findByEmail(_ as String) >> Optional.empty()

            UserCreationRequestDTO dto = UserCreationRequestDTO.builder()
                    .name("dummy name")
                    .email("dummy@email.com")
                    .password("A12a")
                    .phones(List.of(PhoneRequestDTO.builder()
                            .number("12345")
                            .cityCode("12")
                            .countryCode("34")
                            .build()))
                    .build()

            Instant instant = Instant.now();

        //TODO preguntar si usar clase utils en vez de privados
            User persistedUser = User.builder()
                    .name(dto.getName())
                    .email(dto.getEmail())
                    .password("aoeu123")
                    .externalId("aoeu")
                    .createdAt(instant)
                    .modifiedAt(instant)
                    .lastLogin(instant)
                    .token("dummy token")
                    .isActive(Boolean.TRUE)
                    .build();

            userRepository.save(_) >> persistedUser
        when:
            UserCreationInfoResponseDTO valid = userService.createUser(dto)
        then:
            1 * phoneService.assignPhoneToUser(_,_)
            Objects.nonNull(valid.getId())
            valid.username == dto.getEmail()
            valid.createdAt == valid.modifiedAt
            valid.modifiedAt == valid.lastLogin
            valid.isActive == Boolean.TRUE
            Objects.nonNull(valid.token)

    }

    @Unroll("invalid email #wrongEmail should throw exception")
    def "when attempting to create user with invalid email format, exception should be thrown"() {
        given:
            UserCreationRequestDTO dto = UserCreationRequestDTO.builder()
                    .email(wrongEmail)
                    .build();

        when:
            userService.createUser(dto)

        then:
            InvalidEmailException ex = thrown()

        where:
            wrongEmail              | _
            "uae1@dummy.com"        | _
            "aoeu@com"              | _
            "aoeu.com"              | _
            "aoeu_aoeu@dummy.com"   | _
            "AEIOU"                 | _
    }

    def "when attempting to create user with unavailable email, exception should be thrown"() {
        given:
            userRepository.findByEmail(_ as String) >> Optional.of(new Object())

            UserCreationRequestDTO dto = UserCreationRequestDTO.builder()
                .email("a@a.aa")
                .build()
        when:
            userService.createUser(dto)

        then:
            EmailUnavailableException ex = thrown()
    }

    @Unroll("invalid password #wrongPassword should throw exception")
    def "when attempting to create user with invalid password, exception should be thrown"() {
        given:
            userRepository.findByEmail(_ as String) >> Optional.empty()

            UserCreationRequestDTO dto = UserCreationRequestDTO.builder()
                    .email("a@a.aa")
                    .password(wrongPassword)
                    .build();

        when:
            userService.createUser(dto)

        then:
            InvalidPasswordException ex = thrown()

        where:
            wrongPassword       | _
            "uaeU"              | _
            "A12"               | _
            "34312"             | _
            "AEIOU"             | _
    }

}
