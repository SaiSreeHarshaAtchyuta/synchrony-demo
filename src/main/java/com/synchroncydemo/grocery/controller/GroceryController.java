package com.synchroncydemo.grocery.controller;

import com.synchroncydemo.grocery.entity.GroceryItem;
import com.synchroncydemo.grocery.service.GroceryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/admin")
public class GroceryController {

    @Autowired
    private GroceryService groceryService;

    @PostMapping("/items")
    public CompletableFuture<GroceryItem> addItem(@RequestBody GroceryItem item) {
        return groceryService.addItem(item);
    }

    @GetMapping("/items")
    public CompletableFuture<List<GroceryItem>> getAllItems() {
        return groceryService.getAllItems();
    }

    @DeleteMapping("/items/{id}")
    public CompletableFuture<Void> removeItem(@PathVariable Long id) {
        return groceryService.removeItem(id);
    }

    @PutMapping("/items/{id}")
    public CompletableFuture<GroceryItem> updateItem(@PathVariable Long id, @RequestBody GroceryItem item) {
        return groceryService.updateItem(id, item);
    }

    @PatchMapping("/items/{id}/inventory")
    public CompletableFuture<GroceryItem> updateInventory(@PathVariable Long id, @RequestBody int inventory) {
        return groceryService.updateInventory(id, inventory);
    }
}
