package re.edu.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderResponseDTO {
    private String message;
    private Long orderId;
    private Double totalAmount;
}
