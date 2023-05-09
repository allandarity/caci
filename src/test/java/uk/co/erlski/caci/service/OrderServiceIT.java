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
import uk.co.erlski.caci.controller.model.UpdateOrderRequestBody;
import uk.co.erlski.caci.entity.repository.OrderRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureDataJpa
@Testcontainers
class OrderServiceIT {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void createOrderWithUnknownUserReturnsNewUserAndOrderCreation() {
        final var createOrder = orderService.createOrder(new CreateOrderRequestBody("", 5));

        final var order = orderRepository.getOrderById(createOrder.getId());

        assertThat(order)
            .extracting("amount", "state")
            .doesNotContainNull()
            .containsExactly(5, "PENDING");

        assertThat(order.getUserId()).isNotNull();
    }

    @Test
    void createOrderAndGetOrderWithKnownUserReturnsKnownUserCreation() {
        final var createFirstOrder = orderService.createOrder(new CreateOrderRequestBody("", 5));
        final var firstOrder = orderRepository.getOrderById(createFirstOrder.getId());

        final var createSecondOrder = orderService.createOrder(
            new CreateOrderRequestBody(firstOrder.getUserId().getId().toString(), 5));
        final var secondOrder = orderRepository.getOrderById(createSecondOrder.getId());

        assertThat(secondOrder)
            .extracting("amount", "state")
            .doesNotContainNull()
            .containsExactly(5, "PENDING");

        assertThat(secondOrder.getUserId().getId()).isEqualTo(firstOrder.getUserId().getId());
    }

    @Test
    void getOrderWithUnknownIdReturnsNull() {
        final var unknownOrder = orderService.getOrderById(UUID.randomUUID());

        assertThat(unknownOrder).isNull();
    }

    @Test
    void getOrderWithEmptyIdReturnsNull() {
        final var unknownOrder = orderService.getOrderById(new UUID(0, 0));

        assertThat(unknownOrder).isNull();
    }

    @Test
    void getOrdersByCustomerReturnsListOfOrders() {

        final var createFirstOrder = orderService.createOrder(new CreateOrderRequestBody("", 5));
        final var customerId = createFirstOrder.getUserId().getId().toString();

        for (int i = 0; i < 3; i++) {
            orderService.createOrder(new CreateOrderRequestBody(customerId, 5));
        }

        final var orders = orderService.getOrdersByCustomerId(UUID.fromString(customerId));

        assertThat(orders).hasSize(4);
    }

    @Test
    void getOrdersByUnknownCustomerReturnsNoOrders() {

        final var createFirstOrder = orderService.createOrder(new CreateOrderRequestBody("", 5));
        final var customerId = createFirstOrder.getUserId().getId().toString();

        for (int i = 0; i < 3; i++) {
            orderService.createOrder(new CreateOrderRequestBody(customerId, 5));
        }

        final var orders = orderService.getOrdersByCustomerId(UUID.randomUUID());

        assertThat(orders).isEmpty();
    }

    @Test
    void updateOrderReturnsUpdatedOrder() {
        final var createFirstOrder = orderService.createOrder(new CreateOrderRequestBody("", 5));
        final var customerId = createFirstOrder.getUserId().getId().toString();

        assertThat(createFirstOrder.getAmount()).isEqualTo(5);

        final var updateRequest = new UpdateOrderRequestBody(customerId, createFirstOrder.getId().toString(), 10);
        final var updatedOrder = orderService.updateOrder(updateRequest);

        assertThat(updatedOrder.getAmount()).isEqualTo(10);
    }

    @Test
    void updateOrderWithMismatchCustomerIdReturnsNull() {
        final var createFirstOrder = orderService.createOrder(new CreateOrderRequestBody("", 5));

        assertThat(createFirstOrder.getAmount()).isEqualTo(5);

        final var updateRequest = new UpdateOrderRequestBody(UUID.randomUUID().toString(),
            createFirstOrder.getId().toString(), 10);
        final var updatedOrder = orderService.updateOrder(updateRequest);

        assertThat(updatedOrder).isNull();
    }
}
