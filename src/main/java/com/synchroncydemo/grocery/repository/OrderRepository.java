package com.synchroncydemo.grocery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.synchroncydemo.grocery.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {}
