package com.synchroncydemo.grocery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.synchroncydemo.grocery.entity.GroceryItem;

public interface GroceryItemRepository extends JpaRepository<GroceryItem, Long> {}
