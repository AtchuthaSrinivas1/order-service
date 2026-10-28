package com.ecommerce.order_service.service;

import com.ecommerce.order_service.dto.request.CreateOrderRequest;
import com.ecommerce.order_service.dto.request.ReduceStockRequest;
import com.ecommerce.order_service.dto.response.OrderResponse;
import com.ecommerce.order_service.dto.response.ProductResponse;
import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.entity.OrderStatus;
import com.ecommerce.order_service.exception.OrderNotFoundException;
import com.ecommerce.order_service.exception.OrderStatusNotFoundException;
import com.ecommerce.order_service.exception.OutOfStockException;
import com.ecommerce.order_service.gateway.ProductGateway;
import com.ecommerce.order_service.repository.OrderRepository;
import com.ecommerce.order_service.repository.OrderStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import producer.OrderProducer;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final String DEFAULT_STATUS = "PENDING";

    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final ProductGateway productGateway;
    private final OrderProducer orderProducer;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order for productId={}, quantity={}",
                request.getProductId(), request.getQuantity());

        ProductResponse productResponse = productGateway.getProductById(request.getProductId());

        if (request.getQuantity() <= productResponse.getStock()) {
            ReduceStockRequest stockRequest = new ReduceStockRequest();
            stockRequest.setQuantity(request.getQuantity());
            productGateway.reduceStock(request.getProductId(), stockRequest);
        } else {
            log.error("Insufficient stock for productId={}, requested={}, available={}",
                    request.getProductId(), request.getQuantity(), productResponse.getStock());
            throw new OutOfStockException(request.getProductId(), request.getQuantity(), productResponse.getStock());
        }

        OrderStatus pendingStatus = orderStatusRepository.findByName(DEFAULT_STATUS)
                .orElseThrow(() -> new OrderStatusNotFoundException(DEFAULT_STATUS));
        BigDecimal totalAmount = BigDecimal.valueOf(productResponse.getPrice())
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        Order order = Order.builder()
                .productId(productResponse.getId())
                .productName(productResponse.getName())
                .price(BigDecimal.valueOf(productResponse.getPrice()))
                .quantity(request.getQuantity())
                .status(pendingStatus)
                .totalAmount(totalAmount)
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with id={}, status={}", savedOrder.getId(), savedOrder.getStatus().getName());

        orderProducer.publish(savedOrder);

        return mapToResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        log.info("Fetching order with id={}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        log.info("Order found: id={}, status={}", order.getId(), order.getStatus().getName());
        return mapToResponse(order);
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .productId(order.getProductId())
                .productName(order.getProductName())
                .price(order.getPrice())
                .quantity(order.getQuantity())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().getName())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}

