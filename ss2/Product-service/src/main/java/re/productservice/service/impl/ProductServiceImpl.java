package re.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import re.productservice.dto.response.ProductResponseDTO;
import re.productservice.entity.Product;
import re.productservice.repository.IProductRepository;
import re.productservice.service.IProductService;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {
    private final IProductRepository productRepository;


    @Override
    public ProductResponseDTO getProductById(Long id) {
        Product p = new Product(id, "iPhone 15 Pro", "IP15P-256", 25000000.0, 29000000.0, 50);

        return new ProductResponseDTO(
                p.getId(),
                p.getName(),
                p.getSellPrice(),
                p.getStockQuantity()
        );
    }
}
