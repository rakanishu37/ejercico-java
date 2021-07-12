package gl.exercise.UserCrud.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreationInfoResponseDTO {
    private String id;
    private String username;

    @JsonProperty("created")
    private Date createdAt;
    @JsonProperty("modified")
    private Date modifiedAt;
    @JsonProperty("last_login")
    private Date lastLogin;

    private String token;

    @JsonProperty("isactive")
    private Boolean isActive;
}
