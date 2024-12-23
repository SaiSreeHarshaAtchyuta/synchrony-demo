package com.synchroncydemo.grocery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.synchroncydemo.grocery.entity.GroceryItem;
import com.synchroncydemo.grocery.repository.GroceryItemRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class GroceryService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Autowired
    private GroceryItemRepository groceryItemRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Retryable(value = { SQLException.class, RedisConnectionFailureException.class }, maxAttempts = 3)
    @Async
    public CompletableFuture<GroceryItem> addItem(GroceryItem item) {
        return CompletableFuture.completedFuture(groceryItemRepository.save(item));
    }

    @Retryable(value = { SQLException.class, RedisConnectionFailureException.class }, maxAttempts = 3)
    @Async
    public CompletableFuture<List<GroceryItem>> getAllItems() {
        return CompletableFuture.supplyAsync(() -> {
            List<GroceryItem> items = groceryItemRepository.findAll();
            redisTemplate.opsForValue().set("groceryItems", items);
            return items;
        }, executorService);
    }

    @Retryable(value = { SQLException.class, RedisConnectionFailureException.class }, maxAttempts = 3)
    @Async
    public CompletableFuture<Void> removeItem(Long id) {
        return CompletableFuture.runAsync(() -> {
            groceryItemRepository.deleteById(id);
        }, executorService);
    }

    @Retryable(value = { SQLException.class, RedisConnectionFailureException.class }, maxAttempts = 3)
    @Async
    public CompletableFuture<GroceryItem> updateItem(Long id, GroceryItem item) {
        return CompletableFuture.supplyAsync(() -> {
            item.setId(id);
            return groceryItemRepository.save(item);
        }, executorService);
    }

    @Retryable(value = { SQLException.class, RedisConnectionFailureException.class }, maxAttempts = 3)
    @Async
    public CompletableFuture<GroceryItem> updateInventory(Long id, int inventory) {
        return CompletableFuture.supplyAsync(() -> {
            GroceryItem item = groceryItemRepository.findById(id).orElseThrow();
            item.setInventory(inventory);
            return groceryItemRepository.save(item);
        }, executorService);
    }

    @SuppressWarnings("unchecked")
    @Recover
    public void recover(SQLException e) {
        System.out.println("SQL exception occurred, providing fallback mechanism: " + e.getMessage());
        List<GroceryItem> fallbackItems = (List<GroceryItem>) redisTemplate.opsForValue().get("groceryItems");
        if (fallbackItems != null) {
            fallbackItems.forEach(System.out::println);
        } else {
            System.out.println("No cached data available.");
        }
    }

    @Recover
    public void recover(RedisConnectionFailureException e) throws SQLException {
        System.out.println("Redis connection failure occurred, providing fallback mechanism: " + e.getMessage());
        List<GroceryItem> items = groceryItemRepository.findAll();
        items.forEach(System.out::println);
    }
}
