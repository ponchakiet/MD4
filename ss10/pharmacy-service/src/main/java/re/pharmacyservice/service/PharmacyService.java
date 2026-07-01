package re.pharmacyservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import re.pharmacyservice.dto.OrderEvent;

@Service
@RefreshScope
public class PharmacyService {
    @Value("${kafka.topic.stock-events}")
    private String topicName;

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void sendOrder(OrderEvent event) {
        System.out.println("Đang gửi đến Topic: " + topicName);
        kafkaTemplate.send(topicName, event.getMedicineId(), event);
    }
}
