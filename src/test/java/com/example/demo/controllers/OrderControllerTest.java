package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setup() {
        orderController = new OrderController();
        orderController.setOrderRepository(orderRepository);
        orderController.setUserRepository(userRepository);
    }

    @Test
    public void testOrderForInvalidUser(){
        ResponseEntity<UserOrder> randomUser = orderController.submit("randomUser");
        Assert.assertEquals(404, randomUser.getStatusCodeValue());

        ResponseEntity<List<UserOrder>> randomUser1 = orderController.getOrdersForUser("randomUser");
        Assert.assertEquals(404, randomUser1.getStatusCodeValue());
    }

    @Test
    public void testOrderForValidUser(){

        User testUser = new User();
        testUser.setId(1L);
        testUser.setCart(new Cart());
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");

        when(userRepository.findByUsername("testUsername")).thenReturn(testUser);

        List<UserOrder> userOrderList = new ArrayList<>();
        userOrderList.add(new UserOrder());
        when(orderRepository.findByUser(testUser)).thenReturn(userOrderList);

        ResponseEntity<List<UserOrder>> testUsername = orderController.getOrdersForUser("testUsername");

        Assert.assertEquals(userOrderList, testUsername.getBody());
    }

    @Test
    public void testOrderSubmission(){

        User testUser = new User();
        testUser.setId(1L);
        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        testUser.setCart(cart);
        testUser.setPassword("testPassword");
        testUser.setUsername("testUsername");

        when(userRepository.findByUsername("testUsername")).thenReturn(testUser);

        UserOrder order = UserOrder.createFromCart(testUser.getCart());

        ResponseEntity<UserOrder> testUsername = orderController.submit("testUsername");

        Assert.assertEquals(order.toString(), testUsername.getBody().toString());

    }
}
