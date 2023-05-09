package uk.co.erlski.caci.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.co.erlski.caci.controller.model.CreateOrderRequestBody;
import uk.co.erlski.caci.controller.model.UpdateFulfilmentRequestBody;
import uk.co.erlski.caci.entity.repository.OrderRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureDataJpa
@Testcontainers
class FulfilmentServiceIT {

    @Autowired
    private OrderService orderService;

    @Autowired
    private FulfilmentService fulfilmentService;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void updateFulfilmentReturnsUpdatedOrder() {
        final var createOrder = orderService.createOrder(new CreateOrderRequestBody("", 5));
        final var order = orderRepository.getOrderById(createOrder.getId());

        final var fulfilmentRequest = new UpdateFulfilmentRequestBody();
        fulfilmentRequest.setOrderId(order.getId().toString());
        fulfilmentRequest.setStatus("DISPATCHED");


        final var updatedFulfilment = fulfilmentService.updateFulfilment(fulfilmentRequest);

        assertThat(updatedFulfilment)
            .extracting("amount", "state")
            .doesNotContainNull()
            .containsExactly(5, "DISPATCHED");
    }

    @Test
    void updateFulfilmentNullOrderReturnsNull() {
        final var createOrder = orderService.createOrder(new CreateOrderRequestBody("", 5));
        orderRepository.getOrderById(createOrder.getId());

        final var fulfilmentRequest = new UpdateFulfilmentRequestBody();
        fulfilmentRequest.setOrderId(UUID.randomUUID().toString());
        fulfilmentRequest.setStatus("DISPATCHED");

        final var updatedFulfilment = fulfilmentService.updateFulfilment(fulfilmentRequest);

        assertThat(updatedFulfilment).isNull();
    }

}
