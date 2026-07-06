package re.inventoryservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import re.inventoryservice.entity.ProductInventory;

@Repository
public interface InventoryRepository extends JpaRepository<ProductInventory, Long> {
}