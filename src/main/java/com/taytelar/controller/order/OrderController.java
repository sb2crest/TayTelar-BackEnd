package com.taytelar.controller.order;

import com.taytelar.request.order.OrderRequest;
import com.taytelar.response.order.PlaceAnOrderResponse;
import com.taytelar.service.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    /**
     * Places an order based on the provided order request.
     *
     * This API endpoint processes an order request and returns the details
     * of the placed order. It expects a valid OrderRequest object in the request body.
     * If the order is successfully placed, it returns a PlaceAnOrderResponse with
     * the details of the order.
     *
     * @param  orderRequest The request object containing order details.
     * @return A ResponseEntity containing the PlaceAnOrderResponse with the status code.
     */
    @PostMapping("/placeAnOrder")
    public ResponseEntity<PlaceAnOrderResponse> placeAnOrder(@Valid @RequestBody OrderRequest orderRequest) {
        PlaceAnOrderResponse placeAnOrderResponse = orderService.placeAnOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.OK).body(placeAnOrderResponse);
    }
}
