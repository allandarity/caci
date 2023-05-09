package uk.co.erlski.caci.service.impl;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.erlski.caci.controller.model.CreateOrderRequestBody;
import uk.co.erlski.caci.controller.model.OrderResponseBody;
import uk.co.erlski.caci.controller.model.UpdateOrderRequestBody;
import uk.co.erlski.caci.entity.model.Order;
import uk.co.erlski.caci.entity.model.User;
import uk.co.erlski.caci.entity.repository.OrderRepository;
import uk.co.erlski.caci.service.OrderService;
import uk.co.erlski.caci.service.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultOrderService implements OrderService {

    private final UserService userService;
    private final OrderRepository orderRepository;


    @Override
    @Transactional
    public Order createOrder(CreateOrderRequestBody requestBody) {

        User user;

        if (StringUtils.isBlank(requestBody.getUserId())) {
            user = userService.createUser();
        } else {
            user = userService.findUserById(UUID.fromString(requestBody.getUserId()));
            if (user == null) {
                user = userService.createUser();
            }
        }

        final var newOrder = new Order();
        newOrder.setUserId(user);
        newOrder.setAmount(requestBody.getAmount());
        newOrder.setState("PENDING");

        return orderRepository.save(newOrder);
    }

    @Override
    public Order getOrderById(UUID id) {

        if (StringUtils.isBlank(id.toString())) {
            return null;
        }

        return orderRepository.getOrderById(id);
    }

    @Override
    public List<Order> getOrdersByCustomerId(UUID id) {

        if (StringUtils.isBlank(id.toString())) {
            return Collections.emptyList();
        }

        User user = userService.findUserById(id);

        if (user == null) {
            return Collections.emptyList();
        }

        return orderRepository.getOrdersByUserId(user);
    }

    @Override
    public List<OrderResponseBody> convertOrdersToResponseBody(List<Order> orders) {
        return orders.stream().map(o -> OrderResponseBody.builder()
            .orderId(o.getId())
            .userId(o.getUserId().getId())
            .amount(o.getAmount()).build()).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Order updateOrder(UpdateOrderRequestBody requestBody) {

        final var order = getOrderById(UUID.fromString(requestBody.getOrderId()));

        if (order == null) {
            return null;
        }

        if (!order.getUserId().getId().equals(UUID.fromString(requestBody.getUserId()))) {
            log.warn("User id requested doesn't match user id on order - denying");
            return null;
        }

        order.setAmount(requestBody.getAmount());

        return orderRepository.saveAndFlush(order);
    }
}
