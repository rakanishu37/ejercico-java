package gl.exercise.UserCrud.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreationRequestDTO {

   private String name;

   private String email;

   private String password;

   private List<PhoneRequestDTO> phones;
}
