package re.doctorservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseError {
    private long timestamp;
    private int status;
    private String error;
    private String message;
}
