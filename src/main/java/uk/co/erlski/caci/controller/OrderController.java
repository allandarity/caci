package uk.co.erlski.caci.controller;

import io.micrometer.common.util.StringUtils;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.erlski.caci.controller.model.CreateOrderRequestBody;
import uk.co.erlski.caci.controller.model.OrderResponseBody;
import uk.co.erlski.caci.controller.model.UpdateOrderRequestBody;
import uk.co.erlski.caci.service.OrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/createOrder")
    public ResponseEntity<OrderResponseBody> createOrder(@RequestBody CreateOrderRequestBody requestBody) {

        if (requestBody.getAmount() == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(OrderResponseBody.builder().message("Must provide an amount").build());
        }

        final var order = orderService.createOrder(requestBody);

        if (order == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(OrderResponseBody.builder().message("Failed to create order").build());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
            OrderResponseBody.builder()
                .orderId(order.getId())
                .userId(order.getUserId().getId())
                .amount(order.getAmount())
                .message("Order created")
                .build()
        );
    }

    @GetMapping("/orderByOrderId/{id}")
    public ResponseEntity<OrderResponseBody> getOrderById(@PathVariable String id) {

        if (StringUtils.isBlank(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(OrderResponseBody.builder().message("No order id given").build());
        }

        final var order = orderService.getOrderById(UUID.fromString(id));

        if (order == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(OrderResponseBody.builder().message("Failed to retrieve order").build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(
            OrderResponseBody.builder()
                .orderId(order.getId())
                .userId(order.getUserId().getId())
                .amount(order.getAmount())
                .build()
        );
    }

    @GetMapping("/ordersByCustomerId/{id}")
    public ResponseEntity<List<OrderResponseBody>> getOrderByCustomerId(@PathVariable String id) {

        if (StringUtils.isBlank(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(List.of(OrderResponseBody.builder().message("No order id given").build()));
        }

        final var orders = orderService.getOrdersByCustomerId(UUID.fromString(id));

        if (orders == null || orders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(List.of(OrderResponseBody.builder().message("Failed to retrieve orders").build()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.convertOrdersToResponseBody(orders));
    }


    @PutMapping("/update")
    public ResponseEntity<OrderResponseBody> updateOrderById(@RequestBody UpdateOrderRequestBody requestBody) {

        if (StringUtils.isBlank(requestBody.getOrderId()) || StringUtils.isBlank(requestBody.getUserId())
            || requestBody.getAmount() == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(OrderResponseBody.builder().message("Incorrect parameters in request body").build());
        }

        var order = orderService.getOrderById(UUID.fromString(requestBody.getOrderId()));

        if (!order.getState().equals("PENDING")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(OrderResponseBody.builder().message("Can't update non-pending order").build());
        }

        order = orderService.updateOrder(requestBody);

        if (order == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(OrderResponseBody.builder().message("Failed to update order").build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(
            OrderResponseBody.builder()
                .orderId(order.getId())
                .userId(order.getUserId().getId())
                .amount(order.getAmount())
                .build()
        );
    }
}
