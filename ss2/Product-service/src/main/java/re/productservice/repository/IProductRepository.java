package re.productservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import re.productservice.entity.Product;

@Repository
public interface IProductRepository extends JpaRepository<Product,Long> {
}
