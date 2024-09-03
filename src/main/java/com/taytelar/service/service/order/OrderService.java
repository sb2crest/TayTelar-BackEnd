package com.taytelar.service.service.order;

import com.taytelar.request.order.OrderRequest;
import com.taytelar.response.order.PlaceAnOrderResponse;

public interface OrderService {
    PlaceAnOrderResponse placeAnOrder(OrderRequest orderRequest);
}
