package com.taytelar.controller;

import com.razorpay.RazorpayException;
import com.taytelar.request.payment.PaymentRequest;
import com.taytelar.response.SuccessResponse;
import com.taytelar.response.payment.PaymentData;
import com.taytelar.response.payment.PaymentResponse;
import com.taytelar.service.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/createPayment")
    ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest paymentRequest) throws RazorpayException {
        return new ResponseEntity<>(paymentService.createPayment(paymentRequest), HttpStatus.OK);
    }

    @PostMapping("/verifySignature")
    public ResponseEntity<SuccessResponse> verifySignature(@RequestBody PaymentData paymentData) {
        return paymentService.verifyRazorpaySignature(paymentData);
    }

}
