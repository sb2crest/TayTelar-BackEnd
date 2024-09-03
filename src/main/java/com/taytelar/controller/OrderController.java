package com.taytelar.controller;

import com.taytelar.request.order.OrderRequest;
import com.taytelar.response.SuccessResponse;
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

    @PostMapping("/placeAnOrder")
    public ResponseEntity<PlaceAnOrderResponse> placeAnOrder(@Valid @RequestBody OrderRequest orderRequest) {
        PlaceAnOrderResponse placeAnOrderResponse = orderService.placeAnOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.OK).body(placeAnOrderResponse);
    }
}
