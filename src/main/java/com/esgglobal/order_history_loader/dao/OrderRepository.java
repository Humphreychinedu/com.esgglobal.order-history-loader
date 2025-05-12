package com.esgglobal.order_history_loader.dao;

import com.esgglobal.order_history_loader.entity.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
}
