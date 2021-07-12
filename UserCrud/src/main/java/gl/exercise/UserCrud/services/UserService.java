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
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    private final PhoneService phoneService;

    // TODO move to ENV VAR
    private static String SECRET_KEY = "n2r5u8x/A?D(G-KaPdSgVkYp3s6v9y$B&E)H@MbQeThWmZq4t7w!z%C*F-JaNdRfUjXn2r5u8x/A?D(G+KbPeShVkYp3s6v9y$B&E)H@McQfTjWnZq4t7w!z%C*F-JaN";

    private String hashPass(String password) {
        return Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();
    }

    private String generateUUID() {
        String uuid1 = UUID.randomUUID().toString();//.replaceAll("[-]*", "");
        String uuid2 = UUID.randomUUID().toString();//.replaceAll("[-]*", "");

        return uuid1 + uuid2;
    }

    private String createJWT(String id, String issuer, String subject) {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //Key signingKey = Keys.secretKeyFor(signatureAlgorithm);

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signingKey, signatureAlgorithm);


        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    private Claims decodeJWT(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }


    private void checkEmailAvailability(String email) throws EmailUnavailableException {
        userRepository.findByEmail(email)
                .ifPresent(
                        user -> {
                            throw new EmailUnavailableException();
                        }
                );
    }

    private void checkEmailFormat(String email) throws InvalidEmailException {
        Pattern pattern = Pattern.compile("^[a-zA-Z]+@[a-zA-Z]+\\.[a-zA-Z]{2,4}$");

        if (!pattern.matcher(email).find()) {
            throw new InvalidEmailException(email);
        }
    }

    private void checkPasswordFormat(String password) throws InvalidPasswordException {
        Pattern pattern = Pattern.compile("^(?=.*?\\d.*\\d)(?=.*?[A-Z])[a-zA-Z0-9]{4,}$");

        if (!pattern.matcher(password).find()) {
            throw new InvalidPasswordException();
        }
    }



    //TODO checks in private method or validators @Valid
    //TODO User creation in separate method
    @Transactional
    public UserCreationInfoResponseDTO createUser(UserCreationRequestDTO dto) throws InvalidDataException {
        checkEmailFormat(dto.getEmail());

        checkEmailAvailability(dto.getEmail());

        checkPasswordFormat(dto.getPassword());

        Instant instant = Instant.now();
        Date date = Date.from(instant);

        String externalId = generateUUID();
        String token = createJWT(externalId, "system", dto.getName());

        User newUser = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(hashPass(dto.getPassword()))
                .externalId(externalId)
                .createdAt(instant)
                .modifiedAt(instant)
                .lastLogin(instant)
                .token(token)
                .isActive(Boolean.TRUE)
                .build();


        newUser = userRepository.save(newUser);

        phoneService.assignPhoneToUser(dto.getPhones(), newUser);

        /*

        User persistedUser = userRepository.save(newUser);
        dto.getPhones().forEach(
                phoneDTO -> phoneService.createPhone(phoneDTO, persistedUser)
        );
         */
        return UserCreationInfoResponseDTO.builder()
                .id(newUser.getExternalId())
                .username(newUser.getEmail())
                .createdAt(date)
                .modifiedAt(date)
                .lastLogin(date)
                .isActive(newUser.getIsActive())
                .token(newUser.getToken())
                .build();
    }

    //todo
    public UserInfoResponseDTO getUserInfo() {
        return  null;
    }

    //todo
    public void updateUser() {

    }

    public void deleteUser(String token) throws InvalidTokenException {
        Claims jwt = decodeJWT(token);

        User user = userRepository.findByExternalIdAndIsActive(jwt.getId())
                .orElseThrow(
                        () -> {
                            throw new InvalidTokenException();
                        }
                );

        user.setIsActive(Boolean.FALSE);
        userRepository.save(user);
    }

}
