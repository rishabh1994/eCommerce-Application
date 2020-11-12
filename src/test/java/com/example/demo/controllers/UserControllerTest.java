package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();


    @Before
    public void setup() {
        userController = new UserController();
        userController.setUserRepository(userRepository);
        userController.setCartRepository(cartRepository);
        userController.setBCryptPasswordEncoder(bCryptPasswordEncoder);
    }

    @Test
    public void testNonExistingUserData() {
        ResponseEntity<User> searchById = userController.findById(5L);
        assertEquals(404, searchById.getStatusCodeValue());
        assertNull(searchById.getBody());
        ResponseEntity<User> searchByName = userController.findByUserName("abcd");
        assertEquals(404, searchByName.getStatusCodeValue());
        assertNull(searchByName.getBody());
    }

    @Test
    public void testValidUserCreation(){
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");
        ResponseEntity<User> user = userController.createUser(createUserRequest);
        assertEquals(200, user.getStatusCodeValue());
        assertNotNull(user.getBody());
        assertEquals("testUser", user.getBody().getUsername());

        User createdUser = getUserFromCreateUserRequest(createUserRequest);
        Optional<User> byId = Optional.of(createdUser);

        when(userRepository.findById(1L)).thenReturn(byId);
        when(userRepository.findByUsername("testUser")).thenReturn(createdUser);

        ResponseEntity<User> searchById = userController.findById(1L);
        assertEquals(200, searchById.getStatusCodeValue());
        assertNotNull(searchById.getBody());
        assertEquals("testUser", searchById.getBody().getUsername());

        ResponseEntity<User> searchByName = userController.findByUserName("testUser");
        assertNotNull(searchByName.getBody());
        assertEquals("testUser", searchByName.getBody().getUsername());
    }

    @Test
    public void testShortPasswordUserCreationFailure(){
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setPassword("test");
        createUserRequest.setConfirmPassword("test");
        ResponseEntity<User> user = userController.createUser(createUserRequest);
        assertEquals(400, user.getStatusCodeValue());
        assertNull(user.getBody());
    }

    @Test
    public void testPasswordMisMatchUserCreationFailure(){
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testUser");
        createUserRequest.setPassword("firstPassword");
        createUserRequest.setConfirmPassword("secondPassword");
        ResponseEntity<User> user = userController.createUser(createUserRequest);
        assertEquals(400, user.getStatusCodeValue());
        assertNull(user.getBody());
    }

    public User getUserFromCreateUserRequest(CreateUserRequest createUserRequest){
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        user.setPassword(createUserRequest.getPassword());
        user.setCart(new Cart());
        user.setId(1L);
        return user;
    }

}
