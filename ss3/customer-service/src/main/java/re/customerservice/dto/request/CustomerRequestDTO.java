package re.customerservice.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequestDTO {
    private String fullName;
    private String email;
    private String password;
    private String address;
}
