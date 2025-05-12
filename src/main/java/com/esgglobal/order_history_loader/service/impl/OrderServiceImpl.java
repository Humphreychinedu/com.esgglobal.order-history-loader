package com.esgglobal.order_history_loader.service.impl;

import com.esgglobal.order_history_loader.dao.ItemRepository;
import com.esgglobal.order_history_loader.dao.OrderItemRepository;
import com.esgglobal.order_history_loader.dao.OrderRepository;
import com.esgglobal.order_history_loader.dto.*;
import com.esgglobal.order_history_loader.exceptions.OrderNotFoundException;
import com.esgglobal.order_history_loader.mappers.OrderMapper;
import com.esgglobal.order_history_loader.service.OrderService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.esgglobal.order_history_loader.config.Config.getObjectMapper;
import static com.esgglobal.order_history_loader.constants.Constants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * This method is used to parse and loads the order data from API without reading a file
     *
     * @param orders
     * @return
     */
    public ResponseEntity<Response> loadOrder(List<OrderDTO> orders) {
        if (orders.isEmpty()) return ResponseEntity.ok(Response.builder()
                .description(ERROR)
                .code(ERROR_CODE)
                .build());

        orders.forEach(order -> {
            orderRepository.save(OrderMapper.mapOrderFromOrderDTO(order));
            itemRepository.saveAll(OrderMapper.mapItemFromOrderDTO(order));
        });
        return ResponseEntity.ok(Response.builder()
                .code(SUCCESS_CODE)
                .description(SUCCESS)
                .build());
    }

    /**
     * This method is used to parse and load the order data from a file and is initiated via the controller
     *
     * @return
     * @throws IOException
     */
    public ResponseEntity<List<OrderDTO>> readOrdersFromFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("orders.json");
        try (InputStream is = resource.getInputStream()) {
            // Tell Jackson to read a List<Order>
            List<OrderDTO> orderDTOS = getObjectMapper().readValue(is, new TypeReference<List<OrderDTO>>() {
            });

            orderDTOS.forEach(orderDTO -> {
                orderRepository.save(OrderMapper.mapOrderFromOrderDTO(orderDTO));
                itemRepository.saveAll(OrderMapper.mapItemFromOrderDTO(orderDTO));
            });

            return ResponseEntity.ok(orderDTOS);
        }
    }

    /**
     * This method is used to list all orders and computes total price and item count for each order.
     *
     * @param orderId
     * @return
     */
    public List<OrderItemView> getOrderDetails(String orderId) {
        return orderItemRepository.findOrderDetailsByOrderId(orderId);
    }

    /**
     * This method computes total price and item count for each order.
     *
     * @param orderId
     * @return
     */
    public ResponseEntity<OrderItemSummary> getOrderSummaryByOrderId(String orderId) {
        return ResponseEntity.ok(orderItemRepository.getOrderSummaryByOrderId(orderId));
    }

    /**
     * This method is used to list all orders with total cost and item count included
     *
     * @return List of OrderItemView
     */
    public ResponseEntity<List<OrderItemView>> findAllOrderDetails() {
        return ResponseEntity.ok(orderItemRepository.findAllOrderDetails());
    }

    /**
     * This method is used to aggregate summary of total orders and total revenue from *shipped* orders
     *
     * @return OrderSummary
     */
    public ResponseEntity<OrderSummary> totalOrderAndShippedRev() {
        final var orderItems = orderItemRepository.findAllOrderDetails();

        if (orderItems.isEmpty()) {
           throw new OrderNotFoundException("Order not found");
        }
        Set<String> orderIds = orderItems.stream()
                .map(OrderItemView::getOrderId)
                .collect(Collectors.toSet());

        Set<String> shippedOrderIds = new HashSet<>();
        final var shippedTotalPrice = orderItems.stream()
                .distinct()
                .filter(orderItem -> "shipped".equalsIgnoreCase(orderItem.getStatus()))
                .filter(orderItem -> shippedOrderIds.add(orderItem.getOrderId()))
                .mapToDouble(OrderItemView::getTotalPrice)
                .sum();

        return ResponseEntity.ok(new OrderSummary(orderIds.size(), shippedTotalPrice));
    }
}
