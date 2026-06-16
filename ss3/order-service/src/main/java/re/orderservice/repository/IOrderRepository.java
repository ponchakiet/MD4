package re.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import re.orderservice.entity.Order;

@Repository
public interface IOrderRepository extends JpaRepository<Order, Long> {
}
