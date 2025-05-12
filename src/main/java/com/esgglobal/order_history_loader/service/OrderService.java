package com.esgglobal.order_history_loader.service;

import com.esgglobal.order_history_loader.dto.*;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public interface OrderService {
    ResponseEntity<Response> loadOrder(List<OrderDTO> orders);

    ResponseEntity<List<OrderDTO>> readOrdersFromFile() throws IOException;

    List<OrderItemView> getOrderDetails(String orderId);

    ResponseEntity<OrderItemSummary> getOrderSummaryByOrderId(String orderId);

    ResponseEntity<List<OrderItemView>> findAllOrderDetails();

    ResponseEntity<OrderSummary> totalOrderAndShippedRev();
}
