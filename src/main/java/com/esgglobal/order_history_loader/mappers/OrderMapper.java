package com.esgglobal.order_history_loader.mappers;

import com.esgglobal.order_history_loader.dto.OrderDTO;
import com.esgglobal.order_history_loader.entity.Item;
import com.esgglobal.order_history_loader.entity.Order;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderMapper {

    public static Order mapOrderFromOrderDTO(OrderDTO orderDTO) {
        return Order.builder()
                .orderId(orderDTO.getOrderId())
                .customer(orderDTO.getCustomer())
                .status(orderDTO.getStatus())
                .build();
    }

    public static List<Item> mapItemFromOrderDTO(OrderDTO orderDTO) {
        return orderDTO.getItems()
                .stream()
                .map(itemDTO -> Item.builder().orderId(orderDTO.getOrderId())
                        .price(itemDTO.getPrice())
                        .quantity(itemDTO.getQuantity())
                        .product(itemDTO.getProduct())
                        .build())
                .toList();
    }

}
