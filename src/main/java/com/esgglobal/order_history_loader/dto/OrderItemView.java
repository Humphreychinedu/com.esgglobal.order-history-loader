package com.esgglobal.order_history_loader.dto;

public interface OrderItemView {
    String getOrderId();
    String getCustomer();
    String getStatus();
    String getProduct();
    Integer getQuantity();
    Double getPrice();
    Integer getItemCount();
    Double getTotalPrice();
}
