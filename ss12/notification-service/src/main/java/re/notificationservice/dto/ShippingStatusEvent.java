package re.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingStatusEvent implements Serializable {
    private String orderId;
    private String shippingId;
    private String status;
    private String customerEmail;
}