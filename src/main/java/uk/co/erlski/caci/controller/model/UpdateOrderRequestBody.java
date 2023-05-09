package uk.co.erlski.caci.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateOrderRequestBody {

    private String userId;
    private String orderId;
    @JsonProperty(value="amount", required = true)
    private int amount;

}
