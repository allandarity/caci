package uk.co.erlski.caci.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.erlski.caci.controller.model.CreateOrderRequestBody;
import uk.co.erlski.caci.controller.model.OrderResponseBody;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createOrderReturnsCreated() throws Exception {
        final var requestBody = new CreateOrderRequestBody(null, 5);
        final var jsonString = createJsonBody(requestBody);
        mockMvc.perform(post("/order/createOrder")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").exists())
            .andExpect(jsonPath("$.amount").value(5));
    }

    @Test
    void createOrderWithIncorrectAmountReturnsBadRequest() throws Exception {
        final var requestBody = new CreateOrderRequestBody(null, 0);
        final var jsonString = createJsonBody(requestBody);
        mockMvc.perform(post("/order/createOrder")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Must provide an amount"))
            .andExpect(jsonPath("$.amount").value(0));
    }

    @Test
    void getOrderByIdReturnsOrder() throws Exception {
        final var requestBody = new CreateOrderRequestBody(null, 5);
        final var jsonString = createJsonBody(requestBody);
        final var createOrderResponse = mockMvc.perform(post("/order/createOrder")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").exists())
            .andExpect(jsonPath("$.amount").value(5))
                .andReturn();

        String contentAsString = createOrderResponse.getResponse().getContentAsString();
        final var response = objectMapper.readValue(contentAsString, OrderResponseBody.class);

        mockMvc.perform(get("/order/orderByOrderId/{id}", response.getOrderId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").exists())
            .andExpect(jsonPath("$.amount").value(5));

    }

    @Test
    void getOrderByIdGiveNoOrderIdReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/order/orderByOrderId/{id}", " ")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("No order id given"))
            .andExpect(jsonPath("$.amount").value(0));

    }

    private String createJsonBody(Object input) throws JsonProcessingException {
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        final var writer = objectMapper.writer().withDefaultPrettyPrinter();
        return writer.writeValueAsString(input);
    }

}
