package uk.co.erlski.caci.service;

import java.util.List;
import java.util.UUID;
import uk.co.erlski.caci.controller.model.CreateOrderRequestBody;
import uk.co.erlski.caci.controller.model.OrderResponseBody;
import uk.co.erlski.caci.controller.model.UpdateOrderRequestBody;
import uk.co.erlski.caci.entity.model.Order;

public interface OrderService {

    Order createOrder(CreateOrderRequestBody requestBody);

    Order getOrderById(UUID id);

    List<Order> getOrdersByCustomerId(UUID id);

    List<OrderResponseBody> convertOrdersToResponseBody(List<Order> orders);

    Order updateOrder(UpdateOrderRequestBody requestBody);

}
