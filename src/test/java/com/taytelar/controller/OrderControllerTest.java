package com.taytelar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taytelar.request.order.OrderItemRequest;
import com.taytelar.request.order.OrderRequest;
import com.taytelar.response.SuccessResponse;
import com.taytelar.response.order.PlaceAnOrderResponse;
import com.taytelar.service.service.order.OrderService;
import com.taytelar.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @MockBean
    public OrderService orderService;
    @Autowired
    public WebApplicationContext webApplicationContext;
    @Autowired
    private MockMvc mockMvc;

    private final  ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    void testPlaceAnOrder() throws Exception {
        OrderRequest orderRequest = getOrderRequest();
        when(orderService.placeAnOrder(any())).thenReturn(getSuccessResponse());
        mockMvc.perform(post("/api/order/placeAnOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(orderRequest)))
                        .andExpect(status().isOk());

    }

    private PlaceAnOrderResponse getSuccessResponse() {
        PlaceAnOrderResponse placeAnOrderResponse = new PlaceAnOrderResponse();
        placeAnOrderResponse.setMessage("Order placed successfully");
        placeAnOrderResponse.setOrderId("TT123456");
        return placeAnOrderResponse;
    }

    private OrderRequest getOrderRequest() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setUserId("USER1");
        orderRequest.setTotalAmount(200.00);
        orderRequest.setOrderItemRequests(getOrderItemRequests());
        orderRequest.setPaymentMethod(Constants.COD);
        return orderRequest;
    }

    private List<OrderItemRequest> getOrderItemRequests() {
        List<OrderItemRequest> requests = new ArrayList<>();

        OrderItemRequest orderItemRequest = new OrderItemRequest();
        orderItemRequest.setProductId("PRODUCT-ID01");
        orderItemRequest.setQuantity(3);
        orderItemRequest.setTotalAmount(300.00);
        requests.add(orderItemRequest);

        return requests;
    }

}