package com.example.demo.controllers;

import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
@Slf4j
@Getter
@Setter
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        log.debug("Search user by id for id : {}", id);
        return ResponseEntity.of(userRepository.findById(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        log.debug("Search user for name : {}", username);
        User user = userRepository.findByUsername(username);
        log.info("User found is : {}", user);
        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        log.debug("Input create user request : {}", createUserRequest);
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        log.debug("User name set is : {}", createUserRequest.getUsername());

        String password = createUserRequest.getPassword();
        String confirmPassword = createUserRequest.getConfirmPassword();

        boolean isPasswordLengthValid = password.length() >= 8;
        boolean isBothPasswordsSame = password.equals(confirmPassword);

        if (isBothPasswordsSame && isPasswordLengthValid) {
            log.debug("both the passwords match and the password length is also proper");
            user.setPassword(bCryptPasswordEncoder.encode(password));
            log.info("Encoded password stored");
            Cart cart = new Cart();
            cartRepository.save(cart);
            user.setCart(cart);
            userRepository.save(user);
            log.info("User saved successfully");
            return ResponseEntity.ok(user);
        } else {
            log.error("passwords don't match or password length is <8");
            return ResponseEntity.badRequest().build();
        }
    }

}
