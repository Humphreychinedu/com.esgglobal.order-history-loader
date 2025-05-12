package com.esgglobal.order_history_loader.controller;

import com.esgglobal.order_history_loader.dto.*;
import com.esgglobal.order_history_loader.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@Slf4j
@RestController()
@RequestMapping("api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping(value = "/load", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Loads order from json api request")
    public ResponseEntity<Response> loadOrders(@RequestBody List<OrderDTO> orders) {
        log.info("Loading orders {}", orders);

        return orderService.loadOrder(orders);
    }

    @GetMapping(value = "/load_from_file", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Loads orders from json file")
    public ResponseEntity<List<OrderDTO>> loadOrdersFromFile() throws IOException {
        log.info("Loading orders");


        return orderService.readOrdersFromFile();
    }

    @GetMapping("/{orderId}/details")
    @Operation(summary = "Get order details by ID")
    public ResponseEntity<List<OrderItemView>> getOrderDetails(@PathVariable String orderId) {
        List<OrderItemView> details = orderService.getOrderDetails(orderId);
        if (details.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(details);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order details by ID")
    public ResponseEntity<OrderItemSummary> getOrderSummary(@PathVariable String orderId) {

        return orderService.getOrderSummaryByOrderId(orderId);
    }

    @GetMapping("/findAll")
    @Operation(summary = "Get all orders")
    public ResponseEntity<List<OrderItemView>> findAll() {

        return orderService.findAllOrderDetails();
    }

    @GetMapping("/shipped")
    @Operation(summary = "Get Order count and total shipped order revenue")
    public ResponseEntity<OrderSummary> orderSummaryTotalOrderAndShippedRev() {
        return orderService.totalOrderAndShippedRev();
    }
}
