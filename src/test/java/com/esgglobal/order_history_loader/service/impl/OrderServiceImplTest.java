package com.esgglobal.order_history_loader.service.impl;

import com.esgglobal.order_history_loader.dao.ItemRepository;
import com.esgglobal.order_history_loader.dao.OrderItemRepository;
import com.esgglobal.order_history_loader.dao.OrderRepository;
import com.esgglobal.order_history_loader.dto.*;
import com.esgglobal.order_history_loader.entity.Order;
import com.esgglobal.order_history_loader.exceptions.OrderNotFoundException;
import com.esgglobal.order_history_loader.mappers.OrderMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ItemRepository itemRepository;
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void loadOrderTest_ShouldParseAndSaveOrders() {
//        Given
        final var orderDTO = getOrderDTO("shipped", "1001", 10.0, 1);
        final var order = OrderMapper.mapOrderFromOrderDTO(orderDTO);
        final var item = OrderMapper.mapItemFromOrderDTO(orderDTO);

//        When
        when(orderRepository.save(order)).thenReturn(order);
        when(itemRepository.saveAll(item)).thenReturn(item);

        ResponseEntity<Response> response = orderService.loadOrder(List.of(orderDTO));
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(orderRepository).save(order);
        verify(itemRepository).saveAll(item);
    }

    @Test
    void readOrdersFromFile_ShouldParseAndSaveOrders() throws IOException {
        // Given
        InputStream is = new ClassPathResource("orders.json").getInputStream();
        List<OrderDTO> orderList = objectMapper.readValue(is, new TypeReference<>() {
        });
        assertFalse(orderList.isEmpty(), "Test file should not be empty");

//        Then
        ResponseEntity<List<OrderDTO>> response = orderService.readOrdersFromFile();

        // Assert
        assertEquals(orderList.size(), response.getBody().size());
        verify(orderRepository, times(orderList.size())).save(any());
        verify(itemRepository, atLeastOnce()).saveAll(any());
    }

    @Test
    void getOrderDetails_ShouldReturnListOfOrderItemViews() {
        // Given
        String orderId = "1001";
        OrderItemView mockView = mock(OrderItemView.class);
        when(mockView.getOrderId()).thenReturn(orderId);
        when(mockView.getCustomer()).thenReturn("Alice");
        when(mockView.getStatus()).thenReturn("shipped");
        when(mockView.getProduct()).thenReturn("Widget");
        when(mockView.getQuantity()).thenReturn(2);
        when(mockView.getPrice()).thenReturn(10.0);
        when(mockView.getItemCount()).thenReturn(2);
        when(mockView.getTotalPrice()).thenReturn(20.0);

        //When
        when(orderItemRepository.findOrderDetailsByOrderId(orderId))
                .thenReturn(List.of(mockView));

        // Then
        List<OrderItemView> result = orderService.getOrderDetails(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("1001", result.get(0).getOrderId());
        assertEquals("Alice", result.get(0).getCustomer());

        verify(orderItemRepository, times(1)).findOrderDetailsByOrderId(orderId);
    }

    @Test
    void getOrderSummaryByOrderId_ShouldReturnOrderSummary() {
        // Given
        String orderId = "1001";
        OrderItemSummary summary = mock(OrderItemSummary.class);
        when(summary.getOrderId()).thenReturn(orderId);
        when(summary.getItemCount()).thenReturn(3);
        when(summary.getTotalPrice()).thenReturn(45.0);
//        When
        when(orderItemRepository.getOrderSummaryByOrderId(orderId)).thenReturn(summary);

        // Then
        ResponseEntity<OrderItemSummary> response = orderService.getOrderSummaryByOrderId(orderId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("1001", response.getBody().getOrderId());
        assertEquals(3, response.getBody().getItemCount());
        assertEquals(45.0, response.getBody().getTotalPrice());

        verify(orderItemRepository, times(1)).getOrderSummaryByOrderId(orderId);
    }

    @Test
    void findAllOrderDetails_ShouldReturnListOfOrderItemView() {
        // Given
        OrderItemView mockItem = mock(OrderItemView.class);
        when(mockItem.getOrderId()).thenReturn("1001");
        when(mockItem.getCustomer()).thenReturn("Alice");

        // When
        when(orderItemRepository.findAllOrderDetails()).thenReturn(List.of(mockItem));

        // Then
        ResponseEntity<List<OrderItemView>> response = orderService.findAllOrderDetails();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("1001", response.getBody().get(0).getOrderId());
        verify(orderItemRepository, times(1)).findAllOrderDetails();
    }

    @Test
    void totalOrderAndShippedRev_ShouldReturnSummary_WhenOrdersExist() {
        // Given
        OrderItemView shippedItem1 = mock(OrderItemView.class);
        OrderItemView pendingItem = mock(OrderItemView.class);

        // When
        when(shippedItem1.getOrderId()).thenReturn("1001");
        when(shippedItem1.getStatus()).thenReturn("shipped");
        when(shippedItem1.getTotalPrice()).thenReturn(25.0);

        when(pendingItem.getOrderId()).thenReturn("1002");
        when(pendingItem.getStatus()).thenReturn("pending");
        when(pendingItem.getTotalPrice()).thenReturn(10.0);

        when(orderItemRepository.findAllOrderDetails()).thenReturn(List.of(shippedItem1, pendingItem));

        // Then
        ResponseEntity<OrderSummary> response = orderService.totalOrderAndShippedRev();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        OrderSummary summary = response.getBody();
        assertNotNull(summary);
        assertEquals(2, summary.totalOrder()); // 2 unique order IDs
        assertEquals(25.0, summary.shippedOrderRevenue()); // Only shipped
    }

    @Test
    void totalOrderAndShippedRev_ShouldThrowException_WhenNoOrders() {
        // Arrange
        when(orderItemRepository.findAllOrderDetails()).thenReturn(List.of());

        // Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.totalOrderAndShippedRev());
    }

    private static OrderDTO getOrderDTO(String status, String orderId, Double price, Integer quantity) {
        return OrderDTO.builder()
                .orderId(orderId)
                .status(status)
                .customer("customer")
                .items(List.of(getItemsDTO(price, quantity)))
                .build();
    }

    private static OrderDTO.ItemsDTO getItemsDTO(Double price, Integer quantity) {
        return OrderDTO.ItemsDTO.builder()
                .price(price)
                .product("product")
                .quantity(quantity)
                .build();
    }

}