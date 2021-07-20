package gl.exercise.UserCrud.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneDTO {
    private String number;

    @JsonProperty("citycode")
    private String cityCode;

    @JsonProperty("countrycode")
    private String countryCode;
}
