package re.orderservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import re.orderservice.dto.OrderCreatedEvent;
import re.orderservice.dto.ShippingStatusEvent;
import re.orderservice.entity.Order;
import re.orderservice.repository.OrderRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "order-events";

    public Order placeOrder(String email, Long productId, Integer quantity, Double price) {
        // 1. Khởi tạo đơn hàng ở trạng thái chờ xử lý (PENDING)
        Order order = Order.builder()
                .id(UUID.randomUUID().toString())
                .customerEmail(email)
                .productId(productId)
                .quantity(quantity)
                .totalPrice(quantity * price)
                .status("PENDING")
                .build();

        // 2. Lưu vào Database của Order-Service
        Order savedOrder = orderRepository.save(order);
        System.out.println("==> [Order-Service] Đã lưu đơn hàng PENDING vào DB: " + savedOrder.getId());

        // 3. Đóng gói Event bắn qua Kafka Topic giải phóng cho Client luôn không bắt chờ
        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getCustomerEmail(),
                savedOrder.getProductId(),
                savedOrder.getQuantity(),
                savedOrder.getTotalPrice()
        );

        kafkaTemplate.send(TOPIC, savedOrder.getId(), event);
        System.out.println("==> [Order-Service] Đã gửi sự kiện OrderCreatedEvent lên Kafka.");

        return savedOrder;
    }

    public String createOrder(String email, Long productId, int quantity, double price) {
        String orderId = UUID.randomUUID().toString();
        System.out.println("==> [Order-Service] Đã lưu đơn hàng " + orderId + " thành công vào DB.");

        OrderCreatedEvent event = new OrderCreatedEvent(orderId, email, productId, quantity, quantity * price);

        kafkaTemplate.send(TOPIC, orderId, event);
        System.out.println("==> [Order-Service] Đã phát sự kiện OrderCreatedEvent lên Kafka Topic.");

        return orderId;
    }

    @KafkaListener(topics = "shipping-events", groupId = "order-shipping-group")
    public void consumeShippingStatus(ShippingStatusEvent event) {
        System.out.println("\n--- [Order-Service] Nhận được sự kiện cập nhật vận chuyển ---");
        if ("DELIVERED".equalsIgnoreCase(event.getStatus())) {
            System.out.println("--> Trạng thái là DELIVERED. Tiến hành cập nhật Database...");

            System.out.println("--> [DB] Đơn hàng mã số " + event.getOrderId() + " đã chuyển trạng thái thành [COMPLETED]!");
        } else {
            System.out.println("--> Trạng thái: " + event.getStatus() + " (Bỏ qua, không xử lý cập nhật đơn thành COMPLETED)");
        }
        System.out.println("-------------------------------------------------------------\n");
    }
}