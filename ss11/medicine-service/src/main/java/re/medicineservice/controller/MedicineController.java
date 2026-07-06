package re.medicineservice.controller;

import org.springframework.web.bind.annotation.*;
import re.medicineservice.entity.Medicine;
import re.medicineservice.service.MedicineService;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping("/{id}")
    public Medicine getMedicine(@PathVariable Long id) {
        return medicineService.getMedicineById(id);
    }

    @PutMapping
    public Medicine updateMedicine(@RequestBody Medicine medicine) {
        return medicineService.updateMedicine(medicine);
    }

    @PostMapping("/{id}/sell")
    public String sell(@PathVariable Long id, @RequestParam int quantity) {
        try {
            medicineService.sellMedicine(id, quantity);
            return "Giao dịch hoàn tất!";
        } catch (Exception e) {
            return "Giao dịch thất bại: " + e.getMessage();
        }
    }
}