package re.productservice.service;

import re.productservice.dto.response.ProductResponseDTO;

import java.util.List;

public interface IProductService {
    ProductResponseDTO getProductById(Long id);
}
