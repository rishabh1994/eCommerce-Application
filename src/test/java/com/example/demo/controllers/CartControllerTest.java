package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {
        cartController = new CartController();
        cartController.setUserRepository(userRepository);
        cartController.setCartRepository(cartRepository);
        cartController.setItemRepository(itemRepository);
    }

    @Test
    public void testCartModificationForInvalidUser() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        ResponseEntity<Cart> cartResponseEntity = cartController.addTocart(request);
        Assert.assertEquals(404, cartResponseEntity.getStatusCodeValue());
        Assert.assertNull(cartResponseEntity.getBody());

        cartResponseEntity = cartController.removeFromcart(request);
        Assert.assertEquals(404, cartResponseEntity.getStatusCodeValue());
        Assert.assertNull(cartResponseEntity.getBody());
    }

    @Test
    public void testCartModificationForInvalidItem() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setCart(new Cart());
        testUser.setPassword("testPassword");
        testUser.setUsername("testUser");

        when(userRepository.findByUsername("testUser")).thenReturn(testUser);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        ResponseEntity<Cart> cartResponseEntity = cartController.addTocart(request);
        Assert.assertEquals(404, cartResponseEntity.getStatusCodeValue());
        Assert.assertNull(cartResponseEntity.getBody());

        cartResponseEntity = cartController.removeFromcart(request);
        Assert.assertEquals(404, cartResponseEntity.getStatusCodeValue());
        Assert.assertNull(cartResponseEntity.getBody());
    }

    @Test
    public void testCartAdditionForValidUserAndValidItem(){

        User testUser = new User();
        testUser.setId(1L);
        testUser.setCart(new Cart());
        testUser.setPassword("testPassword");
        testUser.setUsername("testUser");

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(5);

        Item item = new Item();
        item.setPrice(BigDecimal.TEN);
        item.setName("testItem");
        item.setDescription("testDescription");;

        when(userRepository.findByUsername("testUser")).thenReturn(testUser);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Cart> cartResponseEntity = cartController.addTocart(request);
        Assert.assertEquals(200, cartResponseEntity.getStatusCodeValue());
        Assert.assertNotNull(cartResponseEntity.getBody());

    }

    @Test
    public void testCartRemovalForValidUserAndValidItem(){

        User testUser = new User();
        testUser.setId(1L);
        testUser.setCart(new Cart());
        testUser.setPassword("testPassword");
        testUser.setUsername("testUser");

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(5);

        Item item = new Item();
        item.setPrice(BigDecimal.TEN);
        item.setName("testItem");
        item.setDescription("testDescription");;

        when(userRepository.findByUsername("testUser")).thenReturn(testUser);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Cart> cartResponseEntity = cartController.removeFromcart(request);
        Assert.assertEquals(200, cartResponseEntity.getStatusCodeValue());
        Assert.assertNotNull(cartResponseEntity.getBody());

    }

}
