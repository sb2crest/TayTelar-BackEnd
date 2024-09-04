package com.taytelar.service.service.payment;

import com.razorpay.RazorpayException;
import com.taytelar.request.payment.PaymentRequest;
import com.taytelar.response.SuccessResponse;
import com.taytelar.response.payment.PaymentData;
import com.taytelar.response.payment.PaymentResponse;
import org.springframework.http.ResponseEntity;

public interface PaymentService {

    PaymentResponse createPayment(PaymentRequest paymentRequest) throws RazorpayException;

    ResponseEntity<SuccessResponse> verifyRazorpaySignature(PaymentData paymentData);
}
