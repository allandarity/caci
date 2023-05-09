package uk.co.erlski.caci.controller.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class UpdateFulfilmentRequestBody {

    private String orderId;
    private String status;

}
