package com.taytelar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taytelar.request.payment.PaymentRequest;
import com.taytelar.response.SuccessResponse;
import com.taytelar.response.payment.PaymentData;
import com.taytelar.response.payment.PaymentResponse;
import com.taytelar.service.service.payment.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

   @MockBean
   private PaymentService paymentService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    void createPayment() throws Exception {
        PaymentRequest paymentRequest = getPaymentPojo();
        when(paymentService.createPayment(Mockito.any())).thenReturn(new PaymentResponse());
        mvc.perform(post("/api/payment/createPayment").content(objectMapper.writeValueAsString(paymentRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void verifySignatureWhenSignatureIsValid() throws Exception {
        PaymentData paymentData = getPaymentData();
        when(paymentService.verifyRazorpaySignature(Mockito.any())).thenReturn(new ResponseEntity<>(new SuccessResponse(), HttpStatus.OK));
        mvc.perform(post("/api/payment/verifySignature").content(objectMapper.writeValueAsString(paymentData))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    private PaymentRequest getPaymentPojo() {
        PaymentRequest paymentDto = new PaymentRequest();
        paymentDto.setUserId("NB34ye");
        paymentDto.setTotalAmount(400.00);
        return paymentDto;
    }

    private PaymentData getPaymentData(){
        PaymentData  paymentData = new PaymentData();
        paymentData.setRazorPayOrderId("abc");
        paymentData.setRazorPayPaymentId("abd");
        paymentData.setRazorPayPaymentId("xyz");
        return paymentData;
    }
}