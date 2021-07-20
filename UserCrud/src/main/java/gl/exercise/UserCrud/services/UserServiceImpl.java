package gl.exercise.UserCrud.services;

import com.google.common.hash.Hashing;
import gl.exercise.UserCrud.dto.request.UserCreationRequestDTO;
import gl.exercise.UserCrud.dto.response.UserCreationInfoResponseDTO;
import gl.exercise.UserCrud.dto.response.UserInfoResponseDTO;
import gl.exercise.UserCrud.exceptions.EmailUnavailableException;
import gl.exercise.UserCrud.exceptions.InvalidDataException;
import gl.exercise.UserCrud.exceptions.InvalidEmailException;
import gl.exercise.UserCrud.exceptions.InvalidPasswordException;
import gl.exercise.UserCrud.exceptions.InvalidTokenException;
import gl.exercise.UserCrud.models.User;
import gl.exercise.UserCrud.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.regex.Pattern;

import static gl.exercise.UserCrud.utils.JWTUtil.createJWT;
import static gl.exercise.UserCrud.utils.JWTUtil.decodeJWT;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    private final PhoneService phoneService;

    private String hashPass(String password) {
        return Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();
    }

    /*
    private String generateUUID() {
        String uuid1 = UUID.randomUUID().toString();//.replaceAll("[-]*", "");
        String uuid2 = UUID.randomUUID().toString();//.replaceAll("[-]*", "");

        return uuid1 + uuid2;
    }
    */


    private Boolean isEmailAvailable(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private void validateEmailAvailability(String email) throws EmailUnavailableException {
        if (!isEmailAvailable(email)) {
            throw new EmailUnavailableException();
        }
    }

    private void validateEmailFormat(String email) throws InvalidEmailException {
        Pattern pattern = Pattern.compile("^[a-zA-Z]+@[a-zA-Z]+\\.[a-zA-Z]{2,4}$");

        if (!pattern.matcher(email).find()) {
            throw new InvalidEmailException(email);
        }
    }

    private void validatePasswordFormat(String password) throws InvalidPasswordException {
        Pattern pattern = Pattern.compile("^(?=.*?\\d.*\\d)(?=.*?[A-Z])[a-zA-Z0-9]{4,}$");

        if (!pattern.matcher(password).find()) {
            throw new InvalidPasswordException();
        }
    }

    private User createNewUser(UserCreationRequestDTO dto) {
        Instant instant = Instant.now();

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(hashPass(dto.getPassword()))
                //.externalId(externalId) ???
                .createdAt(instant)
                .modifiedAt(instant)
                .lastLogin(instant)
                .isActive(Boolean.TRUE)
                .build();

        String token = createJWT(user.getExternalId(), "system", dto.getName());

        user.setToken(token);

        return user;
    }


    //TODO checks in private method or validators @Valid
    @Transactional
    public UserCreationInfoResponseDTO createUser(UserCreationRequestDTO dto) throws InvalidDataException {
        validateEmailFormat(dto.getEmail());

        validateEmailAvailability(dto.getEmail());

        validatePasswordFormat(dto.getPassword());

        User newUser = createNewUser(dto);

        newUser = userRepository.save(newUser);

        phoneService.assignPhoneToUser(dto.getPhones(), newUser);

        /*
        User persistedUser = userRepository.save(newUser);
        dto.getPhones().forEach(
                phoneDTO -> phoneService.createPhone(phoneDTO, persistedUser)
        );
         */

        return UserCreationInfoResponseDTO.fromUser(newUser);
    }


    private User getUserByToken(String token) {
        Claims jwt = decodeJWT(token);

        return userRepository.findByExternalIdAndIsActive(jwt.getId())
                .orElseThrow(
                        () -> {
                            throw new InvalidTokenException();
                        }
                );
    }

    public UserInfoResponseDTO getUserInfo(String token) {
        User user = getUserByToken(token);
        return UserInfoResponseDTO.builder()
                .name(user.getName())
                .email(user.getEmail())
                //.phones(user.ge)
                .build();
    }

    //todo
    public void updateUser(String token) {
        User user = getUserByToken(token);
    }

    public void deleteUser(String token) throws InvalidTokenException {
        User user = getUserByToken(token);

        user.setIsActive(Boolean.FALSE);
        userRepository.save(user);
    }

}
