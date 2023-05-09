package uk.co.erlski.caci.controller;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.erlski.caci.controller.model.FulfilmentResponseBody;
import uk.co.erlski.caci.controller.model.UpdateFulfilmentRequestBody;
import uk.co.erlski.caci.service.FulfilmentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("fulfil")
public class FulfilmentController {

    private final FulfilmentService fulfilmentService;

    @PutMapping("/update")
    public ResponseEntity<FulfilmentResponseBody> updateFulfilmentStatus(
        @RequestBody UpdateFulfilmentRequestBody requestBody) {

        if (StringUtils.isBlank(requestBody.getOrderId()) || StringUtils.isBlank(requestBody.getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(FulfilmentResponseBody.builder().message("Incorrect parameters in request body").build());
        }

        final var order = fulfilmentService.updateFulfilment(requestBody);

        if (order == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(FulfilmentResponseBody.builder().message("Failed to update order").build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(
            FulfilmentResponseBody.builder()
                .orderId(order.getId())
                .status(order.getState())
                .message("Order status updated")
                .build()
        );
    }

}
