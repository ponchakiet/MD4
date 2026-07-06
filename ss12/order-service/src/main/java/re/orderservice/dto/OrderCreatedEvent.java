package re.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderCreatedEvent implements Serializable {
    private String orderId;
    private String customerEmail;
    private Long productId;
    private Integer quantity;
    private Double totalPrice;
}
