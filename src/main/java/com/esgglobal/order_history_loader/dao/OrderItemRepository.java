package com.esgglobal.order_history_loader.dao;

import com.esgglobal.order_history_loader.dto.OrderDTO;
import com.esgglobal.order_history_loader.dto.OrderItemSummary;
import com.esgglobal.order_history_loader.dto.OrderItemView;
import com.esgglobal.order_history_loader.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<Order, String> {

    @Query(value = """
            SELECT
              o.order_id AS orderId,
              o.customer AS customer,
              o.status AS status,
              i.product AS product,
              i.quantity AS quantity,
              i.price AS price,
              COUNT(*) OVER (PARTITION BY o.order_id) AS itemCount,
              SUM(i.price) OVER (PARTITION BY o.order_id) AS totalPrice
            FROM orders o
            JOIN items i ON o.order_id = i.order_id
            WHERE o.order_id = :orderId
            """, nativeQuery = true)
    List<OrderItemView> findOrderDetailsByOrderId(@Param("orderId") String orderId);

    @Query(value = """
            SELECT
            DISTINCT
              o.order_id,
              COUNT(*)           OVER (PARTITION BY o.order_id) AS item_count,
              SUM(i.price)       OVER (PARTITION BY o.order_id) AS total_price
            FROM orders o
            JOIN items i ON o.order_id = i.order_id
            WHERE o.order_id = :orderId
            """, nativeQuery = true)
    OrderItemSummary getOrderSummaryByOrderId(@Param("orderId") String orderId);

    @Query(value = """
            SELECT
              o.order_id AS orderId,
              o.customer AS customer,
              o.status AS status,
              i.product AS product,
              i.quantity AS quantity,
              i.price AS price,
              COUNT(*) OVER (PARTITION BY o.order_id) AS itemCount,
              SUM(i.price) OVER (PARTITION BY o.order_id) AS totalPrice
            FROM orders o
            JOIN items i ON o.order_id = i.order_id
            """, nativeQuery = true)
    List<OrderItemView> findAllOrderDetails();
}
