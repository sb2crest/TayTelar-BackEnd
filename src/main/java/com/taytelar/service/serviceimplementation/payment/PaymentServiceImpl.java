package com.taytelar.service.serviceimplementation.payment;

import com.razorpay.Order;
import com.taytelar.entity.order.OrderEntity;
import com.taytelar.entity.payment.PaymentEntity;
import com.taytelar.entity.user.UserEntity;
import com.taytelar.enums.OrderStatus;
import com.taytelar.enums.PaymentStatus;
import com.taytelar.exception.order.OrderNotFoundException;
import com.taytelar.exception.user.UserNotFoundException;
import com.taytelar.repository.order.OrderRepository;
import com.taytelar.repository.payment.PaymentRepository;
import com.taytelar.repository.user.UserRepository;
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

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private final Generator generator;
    private static final String ALGORITHM = "HmacSHA256";
    @Value("${razorpay.api.key.id}")
    private String keyID;

    @Value("${razorpay.api.key.secret}")
    private String keySecret;

    private final DateTimeFormatter localDateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss");


    @Override
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {
        log.info("Create payment request: " + paymentRequest);
        PaymentResponse response = new PaymentResponse();

        UserEntity userEntity = userRepository.findUserByUserId(paymentRequest.getUserId());
        if (isNull(userEntity)) {
            throw new UserNotFoundException(Constants.USER_NOT_FOUND);
        }

        OrderEntity orderEntity = orderRepository.findByOrderId(paymentRequest.getOrderId());
        if (isNull(orderEntity)) {
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
            log.info("Payment Entity {}", paymentRequest);

            response.setStatus(Constants.SUCCESS);
            response.setMessage(Constants.ORDER_PLACED_SUCCESSFULLY);
        } else {

            try {

                RazorpayClient razorPayClient = new RazorpayClient(keyID, keySecret);

                JSONObject orderRequest = new JSONObject();
                orderRequest.put("amount", paymentRequest.getTotalAmount() * 100);
                orderRequest.put("currency", "INR");
                orderRequest.put("receipt", "payment_receipt_" + System.currentTimeMillis());

                Order razorpayOrder = razorPayClient.orders.create(orderRequest);
                log.info("RazorPay Order : {}",razorpayOrder);
                String razorpayOrderId = razorpayOrder.get("id");
                log.info("RazorPay Order Id : {}", razorpayOrderId);

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
                log.info("Payment Entity {}", paymentRequest);

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

        log.info("Create payment response: {}", response);
        return response;
    }

    public String generateRazorpaySignature(String razorPayOrderId, String razorPayPaymentId, String keySecret) {
        log.info("RazorPayOrderId: {} , RazorPayPaymentId : {}, KeySecret : {}", razorPayOrderId, razorPayPaymentId, keySecret);
        String signature = null;
        try {
            String signatureData = razorPayOrderId + "|" + razorPayPaymentId;
            Mac sha256HMAC = Mac.getInstance(ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keySecret.getBytes(), ALGORITHM);
            sha256HMAC.init(secretKeySpec);

            byte[] bytes = sha256HMAC.doFinal(signatureData.getBytes());

            StringBuilder builder = new StringBuilder();
            for (byte aByte : bytes) {
                builder.append(String.format("%02x", aByte));
            }

            signature = builder.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.debug("Exception while generating signature " + e.getMessage());
        }
        log.info("Generating signature : {}", signature);
        return signature;
    }

    public SuccessResponse verifyRazorpaySignature(PaymentData paymentData) {
        log.info("Verify Razorpay signature Request : {}", paymentData);
        String generatedSignature = generateRazorpaySignature(paymentData.getRazorPayOrderId(), paymentData.getRazorPayPaymentId(), keySecret);
        PaymentEntity paymentEntity = paymentRepository.findByRazorPayOrderId(paymentData.getRazorPayOrderId());
        log.info("PaymentEntity : {}", paymentEntity);
        OrderEntity orderEntity = orderRepository.findByOrderId(paymentData.getOrderId());
        log.info("OrderEntity : {}", orderEntity);

        SuccessResponse successResponse = new SuccessResponse();
        if (generatedSignature != null && generatedSignature.equals(paymentData.getRazorPaySignature())) {

            orderEntity.setOrderStatus(OrderStatus.CONFIRMED);
            orderRepository.save(orderEntity);

            paymentEntity.setRazorPayPaymentId(paymentData.getRazorPayPaymentId());
            paymentEntity.setPaymentStatus(PaymentStatus.SUCCESS);
            paymentEntity.setOrderEntity(orderEntity);
            paymentRepository.save(paymentEntity);
            log.info("Payment Successful, After payment entity saved : {}",paymentEntity);

            successResponse.setMessage(Constants.PAYMENT_SUCCESS);
            successResponse.setStatusCode(HttpStatus.OK.value());
        } else {
            paymentEntity.setRazorPayPaymentId(paymentData.getRazorPayPaymentId());
            paymentEntity.setPaymentStatus(PaymentStatus.FAILED);
            paymentEntity.setOrderEntity(orderEntity);
            paymentRepository.save(paymentEntity);
            log.info("Payment failed, After payment entity saved : {}",paymentEntity);

            successResponse.setMessage(Constants.PAYMENT_FAILED);
            successResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        log.info("Verify RazorPay Signature Response : {}", successResponse);
        return successResponse;
    }
}