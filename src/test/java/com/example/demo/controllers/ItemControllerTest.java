package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private final ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {
        itemController = new ItemController();
        itemController.setItemRepository(itemRepository);
    }

    @Test
    public void getAllItems() {
        ResponseEntity<List<Item>> items = itemController.getItems();
        Assert.assertEquals(200, items.getStatusCodeValue());
        Assert.assertNotNull(items.getBody());
        Assert.assertEquals(0, items.getBody().size());
    }

    @Test
    public void getSameItemByNameAndId() {
        Item item = new Item();
        item.setId(1L);
        item.setName("testName");
        item.setDescription("testDescription");
        item.setPrice(BigDecimal.TEN);
        List<Item> itemsList = new ArrayList<>();
        itemsList.add(item);
        Optional<Item> byId = Optional.of(item);
        when(itemRepository.findById(1L)).thenReturn(byId);
        when(itemRepository.findByName("testName")).thenReturn(itemsList);

        ResponseEntity<Item> items = itemController.getItemById(1L);
        ResponseEntity<List<Item>> testName = itemController.getItemsByName("testName");

        Assert.assertEquals(items.getBody(), testName.getBody().get(0));
    }


}
