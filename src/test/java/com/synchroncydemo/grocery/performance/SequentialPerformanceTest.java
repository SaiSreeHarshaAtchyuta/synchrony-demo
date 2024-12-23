package com.synchroncydemo.grocery.performance;

import com.synchroncydemo.grocery.config.AppConfig;
import com.synchroncydemo.grocery.entity.GroceryItem;
import com.synchroncydemo.grocery.service.GroceryService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

public class SequentialPerformanceTest {

    public static void main(String[] args) throws Exception {
        // Initialize Spring Context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        GroceryService groceryService = context.getBean(GroceryService.class);

        long startTime;
        long endTime;
        long duration;

        // Sequential addItem calls
        startTime = System.nanoTime();
        for (int i = 0; i < 10; i++) {
            GroceryItem item = new GroceryItem();
            item.setName("Item" + i);
            item.setPrice(BigDecimal.valueOf(i + 1));
            item.setInventory(100 + i);
            groceryService.addItem(item).get();
        }
        endTime = System.nanoTime();
        duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println("Sequential addItem duration: " + duration + " ms");

        // Sequential getAllItems call
        startTime = System.nanoTime();
        groceryService.getAllItems().get();
        endTime = System.nanoTime();
        duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println("Sequential getAllItems duration: " + duration + " ms");

        // Sequential updateItem calls
        startTime = System.nanoTime();
        for (int i = 0; i < 10; i++) {
            GroceryItem item = new GroceryItem();
            item.setName("UpdatedItem" + i);
            item.setPrice(BigDecimal.valueOf(i + 1));
            item.setInventory(200 + i);
            groceryService.updateItem((long) i + 1, item).get();
        }
        endTime = System.nanoTime();
        duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println("Sequential updateItem duration: " + duration + " ms");

        // Sequential updateInventory calls
        startTime = System.nanoTime();
        for (int i = 0; i < 10; i++) {
            groceryService.updateInventory((long) i + 1, 300 + i).get();
        }
        endTime = System.nanoTime();
        duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println("Sequential updateInventory duration: " + duration + " ms");

        // Sequential removeItem calls
        startTime = System.nanoTime();
        for (int i = 0; i < 10; i++) {
            groceryService.removeItem((long) i + 1).get();
        }
        endTime = System.nanoTime();
        duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.println("Sequential removeItem duration: " + duration + " ms");

        context.close();
    }
}
