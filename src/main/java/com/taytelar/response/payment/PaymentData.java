package com.taytelar.response.payment;

import lombok.Data;

@Data
public class PaymentData {

    private String orderId;

    private String razorPayOrderId;

    private String razorPayPaymentId;

    private String razorPaySignature;
}
