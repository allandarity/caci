package uk.co.erlski.caci.entity.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.erlski.caci.entity.model.Order;
import uk.co.erlski.caci.entity.model.User;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    Order getOrderById(UUID orderId);

    List<Order> getOrdersByUserId(User userId);

}
