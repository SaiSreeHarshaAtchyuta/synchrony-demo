package com.synchroncydemo.grocery.performance;

import com.synchroncydemo.grocery.config.AppConfig;
import com.synchroncydemo.grocery.entity.GroceryItem;
import com.synchroncydemo.grocery.service.GroceryService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class ParallelPerformanceTest {

    public static void main(String[] args) throws Exception {
        // Initialize Spring Context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        GroceryService groceryService = context.getBean(GroceryService.class);

        long startTime;
        long endTime;
        long duration;

        // Parallel addItem calls
        startTime = System.nanoTime();
        CompletableFuture<Void>[] addFutures = IntStream.range(0, 10)
                .mapToObj(i -> {
                    GroceryItem item = new GroceryItem();
                    item.setName("Item" + i);
                    item.setPrice(BigDecimal.valueOf(i + 1));
                    item.setInventory(100 + i);
                    return groceryService.addItem(item).thenAccept(result -> {
                        System.out.println("Added item: " + result.getName());
                    });
                }).toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(addFutures).get();
        endTime = System.nanoTime();
        duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println("Parallel addItem duration: " + duration + " ms");

        // Parallel getAllItems call
        startTime = System.nanoTime();
        groceryService.getAllItems().get();
        endTime = System.nanoTime();
        duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println("Parallel getAllItems duration: " + duration + " ms");

        // Parallel updateItem calls
        startTime = System.nanoTime();
        CompletableFuture<Void>[] updateFutures = IntStream.range(0, 10)
                .mapToObj(i -> {
                    GroceryItem item = new GroceryItem();
                    item.setName("UpdatedItem" + i);
                    item.setPrice(BigDecimal.valueOf(i + 1));
                    item.setInventory(200 + i);
                    return groceryService.updateItem((long) i + 1, item).thenAccept(result -> {
                        System.out.println("Updated item: " + result.getName());
                    });
                }).toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(updateFutures).get();
        endTime = System.nanoTime();
        duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println("Parallel updateItem duration: " + duration + " ms");

        // Parallel updateInventory calls
        startTime = System.nanoTime();
        CompletableFuture<Void>[] inventoryFutures = IntStream.range(0, 10)
                .mapToObj(i -> groceryService.updateInventory((long) i + 1, 300 + i).thenAccept(result -> {
                    System.out.println("Updated inventory for item: " + result.getId());
                })).toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(inventoryFutures).get();
        endTime = System.nanoTime();
        duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println("Parallel updateInventory duration: " + duration + " ms");

        // Parallel removeItem calls
        startTime = System.nanoTime();
        CompletableFuture<Void>[] removeFutures = IntStream.range(0, 10)
                .mapToObj(i -> groceryService.removeItem((long) i + 1).thenRun(() -> {
                    System.out.println("Removed item with id: " + (i + 1));
                })).toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(removeFutures).get();
        endTime = System.nanoTime();
        duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println("Parallel removeItem duration: " + duration + " ms");

        context.close();
    }
}
