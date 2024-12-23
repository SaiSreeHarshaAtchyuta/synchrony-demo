package com.synchroncydemo.grocery.service;

import com.synchroncydemo.grocery.entity.GroceryItem;
import com.synchroncydemo.grocery.repository.GroceryItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.RedisConnectionFailureException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GroceryServiceTest {

    @Mock
    private GroceryItemRepository groceryItemRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private GroceryService groceryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void testAddItem() throws Exception {
        GroceryItem item = new GroceryItem();
        item.setName("Apple");
        item.setPrice(new BigDecimal("1.20"));
        item.setInventory(100);

        when(groceryItemRepository.save(any(GroceryItem.class))).thenReturn(item);

        CompletableFuture<GroceryItem> savedItem = groceryService.addItem(item);
        assertNotNull(savedItem.get());
        assertEquals("Apple", savedItem.get().getName());
        verify(groceryItemRepository, times(1)).save(item);
    }

    @Test
    public void testGetAllItems() throws Exception {
        GroceryItem item1 = new GroceryItem();
        item1.setId(1L);
        item1.setName("Apple");
        item1.setPrice(new BigDecimal("1.20"));
        item1.setInventory(100);

        GroceryItem item2 = new GroceryItem();
        item2.setId(2L);
        item2.setName("Banana");
        item2.setPrice(new BigDecimal("0.50"));
        item2.setInventory(50);

        when(groceryItemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));

        CompletableFuture<List<GroceryItem>> items = groceryService.getAllItems();
        assertNotNull(items.get());
        assertEquals(2, items.get().size());
        verify(groceryItemRepository, times(1)).findAll();
        verify(valueOperations, times(1)).set("groceryItems", Arrays.asList(item1, item2));
    }

    @Test
    public void testRemoveItem() throws Exception {
        Long itemId = 1L;
        doNothing().when(groceryItemRepository).deleteById(itemId);

        CompletableFuture<Void> future = groceryService.removeItem(itemId);
        future.get(); // wait for completion

        verify(groceryItemRepository, times(1)).deleteById(itemId);
    }

    @Test
    public void testUpdateItem() throws Exception {
        GroceryItem item = new GroceryItem();
        item.setId(1L);
        item.setName("Orange");
        item.setPrice(new BigDecimal("0.80"));
        item.setInventory(30);

        when(groceryItemRepository.save(any(GroceryItem.class))).thenReturn(item);

        CompletableFuture<GroceryItem> updatedItem = groceryService.updateItem(1L, item);
        assertNotNull(updatedItem.get());
        assertEquals("Orange", updatedItem.get().getName());
        assertEquals(new BigDecimal("0.80"), updatedItem.get().getPrice());
        verify(groceryItemRepository, times(1)).save(item);
    }

    @Test
    public void testUpdateInventory() throws Exception {
        GroceryItem item = new GroceryItem();
        item.setId(1L);
        item.setName("Grapes");
        item.setPrice(new BigDecimal("2.50"));
        item.setInventory(20);

        when(groceryItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(groceryItemRepository.save(any(GroceryItem.class))).thenReturn(item);

        CompletableFuture<GroceryItem> updatedItem = groceryService.updateInventory(1L, 50);
        assertNotNull(updatedItem.get());
        assertEquals(50, updatedItem.get().getInventory());
        verify(groceryItemRepository, times(1)).findById(1L);
        verify(groceryItemRepository, times(1)).save(item);
    }

    @Test
    public void testRecoverFromSQLException() {
        GroceryItem item1 = new GroceryItem();
        item1.setId(1L);
        item1.setName("Apple");
        item1.setPrice(new BigDecimal("1.20"));
        item1.setInventory(100);

        when(valueOperations.get("groceryItems")).thenReturn(Arrays.asList(item1));

        groceryService.recover(new SQLException("Test SQL Exception"));

        List<GroceryItem> cachedItems = (List<GroceryItem>) valueOperations.get("groceryItems");
        assertNotNull(cachedItems);
        assertEquals(1, cachedItems.size());
        assertEquals("Apple", cachedItems.get(0).getName());
    }

    @Test
    public void testRecoverFromRedisConnectionFailureException() throws SQLException {
        GroceryItem item1 = new GroceryItem();
        item1.setId(1L);
        item1.setName("Apple");
        item1.setPrice(new BigDecimal("1.20"));
        item1.setInventory(100);

        when(groceryItemRepository.findAll()).thenReturn(Arrays.asList(item1));

        groceryService.recover(new RedisConnectionFailureException("Test Redis Connection Failure Exception"));

        verify(groceryItemRepository, times(1)).findAll();
    }
}
