package re.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductResponseDTO {
    private Long id;
    private String name;
    private Double sellPrice;
    private Integer stockQuantity;
}
