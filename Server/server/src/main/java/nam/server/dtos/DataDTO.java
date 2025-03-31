package nam.server.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DataDTO {
    @JsonProperty("temperature")
    private float temperature;

    @JsonProperty("humidity")
    private float humidity;

    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("deviceId")
    private String deviceId;
}
