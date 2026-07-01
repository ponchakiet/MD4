package re.pharmacyservice.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import re.pharmacyservice.entity.Medicine;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine,String> {
    @Modifying
    @Transactional
    @Query("UPDATE Medicine m SET m.stock = m.stock - :quantity WHERE m.id = :medicineId AND m.stock >= :quantity")
    int decreaseStock(String medicineId, int quantity);
}
