package uk.co.erlski.caci.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateOrderRequestBody {

    private String userId;

    @JsonProperty(value="amount", required = true)
    private int amount;

}
