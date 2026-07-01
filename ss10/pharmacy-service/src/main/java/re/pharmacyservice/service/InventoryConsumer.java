package re.pharmacyservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import re.pharmacyservice.dto.OrderEvent;
import re.pharmacyservice.repository.MedicineRepository;

@Service
public class InventoryConsumer {
    @Autowired
    private MedicineRepository medicineRepository;

    @KafkaListener(topics = "medicine-stock-events", groupId = "inventory-group")
    public void consumeOrderEvent(OrderEvent event) {
        System.out.println(">>> Đã nhận đơn hàng: " + event.getOrderId() + " cho thuốc: " + event.getMedicineId());

        // Thực hiện cập nhật DB
        int updatedRows = medicineRepository.decreaseStock(event.getMedicineId(), event.getQuantity());

        if (updatedRows > 0) {
            System.out.println("✅ Cập nhật kho thành công cho thuốc: " + event.getMedicineId());
        } else {
            System.err.println("❌ Cập nhật kho thất bại (Có thể do hết hàng hoặc sai ID): " + event.getMedicineId());
        }
    }
}
