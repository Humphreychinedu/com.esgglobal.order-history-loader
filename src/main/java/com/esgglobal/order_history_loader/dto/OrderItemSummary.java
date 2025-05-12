package com.esgglobal.order_history_loader.dto;

public interface OrderItemSummary {
    String getOrderId();
    Integer getItemCount();
    Double getTotalPrice();
}
