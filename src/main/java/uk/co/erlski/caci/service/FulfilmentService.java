package uk.co.erlski.caci.service;

import uk.co.erlski.caci.controller.model.UpdateFulfilmentRequestBody;
import uk.co.erlski.caci.entity.model.Order;

public interface FulfilmentService {

    Order updateFulfilment(UpdateFulfilmentRequestBody requestBody);

}
