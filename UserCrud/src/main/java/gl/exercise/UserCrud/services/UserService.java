package gl.exercise.UserCrud.services;

import gl.exercise.UserCrud.dto.request.UserCreationRequestDTO;
import gl.exercise.UserCrud.dto.response.UserCreationInfoResponseDTO;
import gl.exercise.UserCrud.dto.response.UserInfoResponseDTO;

public interface UserService {
    UserInfoResponseDTO getUserInfo(String token);
    UserCreationInfoResponseDTO createUser(UserCreationRequestDTO dto);
    //update
    void deleteUser(String token);
}
