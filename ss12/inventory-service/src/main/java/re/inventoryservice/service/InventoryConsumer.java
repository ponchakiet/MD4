package re.inventoryservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import re.inventoryservice.dto.OrderCreatedEvent;
import re.inventoryservice.entity.ProductInventory;
import re.inventoryservice.repository.InventoryRepository;

@Service
@RequiredArgsConstructor
public class InventoryConsumer {

    private final InventoryRepository inventoryRepository;

    // Khởi tạo sẵn dữ liệu kho mẫu để test
    @PostConstruct
    public void initStock() {
        inventoryRepository.save(new ProductInventory(105L, 50)); // Sản phẩm 105 có 50 hộp trong kho
    }

    @KafkaListener(topics = "order-events", groupId = "inventory-group")
    public void processInventory(OrderCreatedEvent event) {
        System.out.println("\n📦 [Inventory-Service] Đang xử lý trừ kho cho Đơn hàng: " + event.getOrderId());

        ProductInventory inventory = inventoryRepository.findById(event.getProductId())
                .orElse(new ProductInventory(event.getProductId(), 0));

        if (inventory.getStock() >= event.getQuantity()) {
            // Đủ hàng -> Thực hiện trừ kho
            inventory.setStock(inventory.getStock() - event.getQuantity());
            inventoryRepository.save(inventory);
            System.out.println("--> CẬP NHẬT KHO THÀNH CÔNG. Sản phẩm ID: " + event.getProductId() + " | Kho còn lại: " + inventory.getStock());
        } else {
            // Lỗi hết hàng
            System.err.println("--> XỬ LÝ THẤT BẠI: Sản phẩm ID " + event.getProductId() + " không đủ tồn kho! (Yêu cầu: " + event.getQuantity() + " | Hiện có: " + inventory.getStock() + ")");
        }
        System.out.println("=============================================\n");
    }

    @KafkaListener(topics = "order-events", groupId = "inventory-group") // groupId khác biệt hoàn toàn
    public void handleUpdateInventory(OrderCreatedEvent event) {
        System.out.println("\n📦 [Inventory-Service] Nhận được sự kiện từ Kafka!");
        System.out.println("--> Đang thực hiện kiểm tra và trừ tồn kho cho Sản phẩm ID: " + event.getProductId());
        System.out.println("--> Số lượng hàng trừ: " + event.getQuantity());
        System.out.println("==================================================\n");
    }
}