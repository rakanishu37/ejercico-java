package gl.exercise.UserCrud.controllers;

import gl.exercise.UserCrud.dto.request.UserCreationRequestDTO;
import gl.exercise.UserCrud.dto.response.MessageDTO;
import gl.exercise.UserCrud.dto.response.UserCreationInfoResponseDTO;
import gl.exercise.UserCrud.exceptions.InvalidDataException;
import gl.exercise.UserCrud.exceptions.InvalidTokenException;
import gl.exercise.UserCrud.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping()
    public ResponseEntity<UserCreationInfoResponseDTO> createUser(@RequestBody UserCreationRequestDTO dto) throws InvalidDataException {
        UserCreationInfoResponseDTO response = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //TODO get (info del user pero solo el?)
    //@GetMapping("/info")

    @DeleteMapping()
    public ResponseEntity<MessageDTO> deleteUser(@RequestHeader("token") String token) throws InvalidTokenException {
        userService.deleteUser(token);
        MessageDTO dto = new MessageDTO("User deleted successfully!");

        return ResponseEntity.ok(dto);
    }

    //Put que le permito cambiar?

    //Agregar y eliminar algun telefono suyo en otro controller?

}
