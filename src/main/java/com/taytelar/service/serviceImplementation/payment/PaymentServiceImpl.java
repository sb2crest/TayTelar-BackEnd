package com.taytelar.service.serviceImplementation.payment;

import com.razorpay.Order;
import com.taytelar.entity.order.OrderEntity;
import com.taytelar.entity.payment.PaymentEntity;
import com.taytelar.entity.user.UserEntity;
import com.taytelar.enums.OrderStatus;
import com.taytelar.enums.PaymentStatus;
import com.taytelar.exception.order.OrderNotFoundException;
import com.taytelar.exception.user.UserNotFoundException;
import com.taytelar.repository.OrderRepository;
import com.taytelar.repository.PaymentRepository;
import com.taytelar.repository.UserRepository;
import com.taytelar.request.payment.PaymentRequest;
import com.taytelar.response.SuccessResponse;
import com.taytelar.response.payment.PaymentData;
import com.taytelar.response.payment.PaymentResponse;
import com.taytelar.service.service.payment.PaymentService;
import com.taytelar.util.Constants;
import com.taytelar.util.Generator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private final Generator generator;

    @Value("${razorpay.api.key.id}")
    private String keyID;

    @Value("${razorpay.api.key.secret}")
    private String keySecret;

    private final DateTimeFormatter localDateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss");


    @Override
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {
        PaymentResponse response = new PaymentResponse();

        UserEntity userEntity = userRepository.findUserByUserId(paymentRequest.getUserId());
        if (userEntity == null) {
            throw new UserNotFoundException(Constants.USER_NOT_FOUND);
        }

        OrderEntity orderEntity = orderRepository.findByOrderId(paymentRequest.getOrderId());
        if (orderEntity == null) {
            throw new OrderNotFoundException(Constants.ORDER_NOT_FOUND);
        }

        if (paymentRequest.getPaymentMethod().equalsIgnoreCase(Constants.COD)) {


            PaymentEntity paymentEntity = new PaymentEntity();
            // payment id is for us reference
            paymentEntity.setPaymentId(generator.generateId(Constants.PAYMENT_ID));
            paymentEntity.setTotalAmount(paymentRequest.getTotalAmount());
            paymentEntity.setPaymentDate(LocalDateTime.now());
            paymentEntity.setUserId(paymentRequest.getUserId());
            paymentEntity.setPaymentMethod(paymentRequest.getPaymentMethod());
            paymentEntity.setPaymentStatus(PaymentStatus.PENDING);
            paymentEntity.setOrderEntity(orderEntity);
            paymentRepository.save(paymentEntity);

            response.setStatus(Constants.SUCCESS);
            response.setMessage(Constants.ORDER_PLACED_SUCCESSFULLY);
            response.setPaymentDate(localDateFormat.format(paymentEntity.getPaymentDate()));
        } else {

            try {

                RazorpayClient razorPayClient = new RazorpayClient(keyID, keySecret);

                JSONObject orderRequest = new JSONObject();
                orderRequest.put("amount", paymentRequest.getTotalAmount() * 100);
                orderRequest.put("currency", "INR");
                orderRequest.put("receipt", "payment_receipt_" + System.currentTimeMillis());

                Order razorpayOrder = razorPayClient.orders.create(orderRequest);
                String razorpayOrderId = razorpayOrder.get("id");

                PaymentEntity paymentEntity = new PaymentEntity();
                // payment id is for us reference
                paymentEntity.setPaymentId(generator.generateId(Constants.PAYMENT_ID));
                paymentEntity.setRazorPayOrderId(razorpayOrderId);
                paymentEntity.setTotalAmount(paymentRequest.getTotalAmount());
                paymentEntity.setPaymentDate(LocalDateTime.now());
                paymentEntity.setUserId(paymentRequest.getUserId());
                paymentEntity.setPaymentMethod(paymentRequest.getPaymentMethod());
                paymentEntity.setPaymentStatus(PaymentStatus.PENDING);
                paymentRepository.save(paymentEntity);

                response.setStatus(Constants.SUCCESS);
                response.setMessage(Constants.PAYMENT_CREATED_SUCCESSFULLY);
                response.setRazorPayOrderId(razorpayOrderId);
                response.setPaymentDate(localDateFormat.format(paymentEntity.getPaymentDate()));
            } catch (RazorpayException e) {
                log.error("While initiating the payment RazorPayException occurs: {}", e.getMessage());
                response.setStatus(Constants.ERROR);
                response.setMessage(Constants.ERROR_MESSAGE + e.getMessage());
            }
        }

        return response;
    }

    public String generateRazorpaySignature(String razorPayOrderId, String razorPayPaymentId, String keySecret) {
        String signature = null;
        try {
            String signatureData = razorPayOrderId + "|" + razorPayPaymentId;
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(keySecret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] bytes = sha256_HMAC.doFinal(signatureData.getBytes());

            StringBuilder builder = new StringBuilder();
            for (byte aByte : bytes) {
                builder.append(String.format("%02x", aByte));
            }

            signature = builder.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.debug("Exception while generating signature " + e.getMessage());
        }
        return signature;
    }

    public ResponseEntity<SuccessResponse> verifyRazorpaySignature(PaymentData paymentData) {
        String generatedSignature = generateRazorpaySignature(paymentData.getRazorPayOrderId(), paymentData.getRazorPayPaymentId(), keySecret);
        PaymentEntity paymentEntity = paymentRepository.findByRazorPayOrderId(paymentData.getRazorPayOrderId());
        OrderEntity orderEntity = orderRepository.findByOrderId(paymentData.getOrderId());

        SuccessResponse successResponse = new SuccessResponse();
        if (generatedSignature != null && generatedSignature.equals(paymentData.getRazorPaySignature())) {

            orderEntity.setOrderStatus(OrderStatus.CONFIRMED);
            orderRepository.save(orderEntity);

            paymentEntity.setRazorPayPaymentId(paymentData.getRazorPayPaymentId());
            paymentEntity.setPaymentStatus(PaymentStatus.SUCCESS);
            paymentEntity.setOrderEntity(orderEntity);
            paymentRepository.save(paymentEntity);

            successResponse.setMessage(Constants.PAYMENT_SUCCESS);
            successResponse.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<>(successResponse, HttpStatus.OK);
        } else {
            paymentEntity.setRazorPayPaymentId(paymentData.getRazorPayPaymentId());
            paymentEntity.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(paymentEntity);

            successResponse.setMessage(Constants.PAYMENT_FAILED);
            successResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(successResponse, HttpStatus.PAYMENT_REQUIRED);
        }
    }
}