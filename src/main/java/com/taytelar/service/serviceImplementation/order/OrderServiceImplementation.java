package com.taytelar.service.serviceImplementation.order;

import com.taytelar.entity.order.OrderEntity;
import com.taytelar.entity.order.OrderItemEntity;
import com.taytelar.entity.user.UserEntity;
import com.taytelar.enums.OrderStatus;
import com.taytelar.exception.user.UserNotFoundException;
import com.taytelar.repository.OrderItemRepository;
import com.taytelar.repository.OrderRepository;
import com.taytelar.repository.UserRepository;
import com.taytelar.request.order.OrderItemRequest;
import com.taytelar.request.order.OrderRequest;
import com.taytelar.response.order.PlaceAnOrderResponse;
import com.taytelar.service.service.order.OrderService;
import com.taytelar.util.Constants;
import com.taytelar.util.Generator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImplementation implements OrderService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final UserRepository userRepository;

    private final Generator generator;


    @Override
    public PlaceAnOrderResponse placeAnOrder(OrderRequest orderRequest) {
        try {

            UserEntity userEntity = userRepository.findUserByUserId(orderRequest.getUserId());
            if (userEntity == null) {
                throw new UserNotFoundException(Constants.USER_NOT_FOUND);
            }

            OrderEntity orderEntity = getNewOrderEntity(orderRequest, userEntity);
            orderRepository.save(orderEntity);

            List<OrderItemEntity> orderItems = getNewOrderItemEntities(orderRequest.getOrderItemRequests(), orderEntity);
            orderItemRepository.saveAll(orderItems);

            PlaceAnOrderResponse placeAnOrderResponse = new PlaceAnOrderResponse();
            placeAnOrderResponse.setMessage(Constants.ORDER_PLACED_SUCCESSFULLY);
            placeAnOrderResponse.setOrderId(orderEntity.getOrderId());

            return placeAnOrderResponse;

        } catch (UserNotFoundException e) {
            log.error(Constants.USER_NOT_FOUND + ": {}", e.getMessage());
            throw e;
        }catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private List<OrderItemEntity> getNewOrderItemEntities(List<OrderItemRequest> orderItemRequests, OrderEntity orderEntity) {
        return orderItemRequests.stream()
                .map(itemRequest -> {
                    OrderItemEntity orderItemEntity = new OrderItemEntity();
                    orderItemEntity.setOrderItemId(generator.generateId(Constants.ORDER_ITEM_ID));
                    orderItemEntity.setProductId(itemRequest.getProductId());
                    orderItemEntity.setQuantity(itemRequest.getQuantity());
                    orderItemEntity.setUnitPrice(itemRequest.getTotalAmount() / itemRequest.getQuantity());
                    orderItemEntity.setTotalAmount(itemRequest.getTotalAmount());
                    orderItemEntity.setOrderEntity(orderEntity);
                    orderItemEntity.setReturnDaysPolicy(Constants.RETURN_DAYS_POLICY);
                    return orderItemEntity;
                })
                .collect(Collectors.toList());
    }

    private OrderEntity getNewOrderEntity(OrderRequest orderRequest, UserEntity userEntity) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(generator.generateId(Constants.ORDER_ID));
        orderEntity.setOrderDate(LocalDateTime.now());
        orderEntity.setTotalAmount(orderRequest.getTotalAmount());
        orderEntity.setOrderStatus(OrderStatus.PENDING);
        orderEntity.setUserEntity(userEntity);
        return orderEntity;
    }
}
