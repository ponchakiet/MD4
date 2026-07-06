package re.pharmacyservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import re.pharmacyservice.entity.Medicine;
import re.pharmacyservice.repository.MedicineRepository;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    @Autowired
    private MedicineRepository medicineRepository;

    @GetMapping("/{medicineId}")
    public ResponseEntity<?> getStock(@PathVariable String medicineId) {
        return medicineRepository.findById(medicineId)
                .map(medicine -> ResponseEntity.ok(medicine))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public List<Medicine> getAllStock() {
        return medicineRepository.findAll();
    }
}
