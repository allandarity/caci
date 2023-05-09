package uk.co.erlski.caci.service.impl;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.erlski.caci.controller.model.UpdateFulfilmentRequestBody;
import uk.co.erlski.caci.entity.model.Order;
import uk.co.erlski.caci.entity.repository.OrderRepository;
import uk.co.erlski.caci.service.FulfilmentService;
import uk.co.erlski.caci.service.OrderService;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultFulfilmentService implements FulfilmentService {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @Override
    public Order updateFulfilment(UpdateFulfilmentRequestBody requestBody) {

        final var order = orderService.getOrderById(UUID.fromString(requestBody.getOrderId()));

        if (order == null) {
            log.warn("no order found for the id {}", requestBody.getOrderId());
            return null;
        }

        order.setState(requestBody.getStatus());

        return orderRepository.saveAndFlush(order);
    }
}
