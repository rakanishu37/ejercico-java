package gl.exercise.UserCrud.controllers;

import gl.exercise.UserCrud.dto.response.MessageDTO;
import gl.exercise.UserCrud.exceptions.EmailUnavailableException;
import gl.exercise.UserCrud.exceptions.InvalidEmailException;
import gl.exercise.UserCrud.exceptions.InvalidPasswordException;
import gl.exercise.UserCrud.exceptions.InvalidTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageDTO> fallbackHandler(Exception exception) {
        exception.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.I_AM_A_TEAPOT)
                .body(new MessageDTO(exception.getMessage()));
    }


    @ExceptionHandler(EmailUnavailableException.class)
    public ResponseEntity<MessageDTO> handleEmailUnavailableException(EmailUnavailableException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MessageDTO(exception.getMessage()));
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<MessageDTO> handleInvalidEmailException(InvalidEmailException exception) {
        log.error("Rejected invalid email");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MessageDTO(exception.getMessage()));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<MessageDTO> handleInvalidPasswordException(InvalidPasswordException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MessageDTO(exception.getMessage()));
    }


    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<MessageDTO> handleInvalidTokenException(InvalidTokenException exception) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new MessageDTO(exception.getMessage()));
    }
}
