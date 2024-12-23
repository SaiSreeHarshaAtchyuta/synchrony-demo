package com.synchroncydemo.grocery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.synchroncydemo.grocery.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {}
