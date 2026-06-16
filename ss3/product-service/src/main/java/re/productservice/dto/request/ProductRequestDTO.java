package re.productservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequestDTO {
    @NotBlank(message = "Product name cannot be blank")
    private String name;

    @Min(value = 1, message = "Price must be greater than 0")
    private Double price;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;
}
