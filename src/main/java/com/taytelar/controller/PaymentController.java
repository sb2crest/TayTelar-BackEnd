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

    /**
     * Creates a payment order using the provided payment request.
     *
     * This API endpoint interacts with the Razorpay payment gateway to create a new payment order.
     * It takes a PaymentRequest object as input, which contains the necessary payment details.
     * The response includes the payment order details, such as the order ID and status.
     *
     * @param paymentRequest The request object containing the payment details.
     * @return A ResponseEntity containing the PaymentResponse with the order details and status.
     * @throws RazorpayException If there is an error while creating the payment order.
     */
    @PostMapping("/createPayment")
    ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest paymentRequest) throws RazorpayException {
        return new ResponseEntity<>(paymentService.createPayment(paymentRequest), HttpStatus.OK);
    }


    /**
     * Verifies the payment signature returned by Razorpay.
     *
     * This API endpoint verifies the Razorpay signature to ensure that the payment was not tampered with.
     * It takes a PaymentData object as input, which includes the payment details and signature.
     * The response indicates whether the signature verification was successful or not.
     *
     * @param paymentData The data object containing payment details and the Razorpay signature.
     * @return A ResponseEntity containing the SuccessResponse indicating the verification result.
     */
    @PostMapping("/verifySignature")
    public ResponseEntity<SuccessResponse> verifySignature(@RequestBody PaymentData paymentData) {
        return paymentService.verifyRazorpaySignature(paymentData);
    }

}
