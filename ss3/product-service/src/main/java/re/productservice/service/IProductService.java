package re.productservice.service;

import re.productservice.dto.request.ProductRequestDTO;
import re.productservice.entity.Product;

import java.util.List;

public interface IProductService {
    Product saveProduct(ProductRequestDTO dto);
    Product getById(Long id);
    List<Product> getAll();
}
