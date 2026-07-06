package re.medicineservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import re.medicineservice.entity.Medicine;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
}
