package com.marselgaisin.junitTest.service;

import com.marselgaisin.junit.dto.User;
import com.marselgaisin.junit.service.UserService;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {

    @BeforeAll
    void init() {
        System.out.println("Before all: " + this);
    }
    private UserService userService;
    @BeforeEach
    void prepare() {
        System.out.println("Before each " + this);
        userService = new UserService();
    }
    @Test
    void usersEmptyIfNoUserAdded() {
        System.out.println("Test 1: " + this);
        var users = userService.getAll();
        assertTrue(users.isEmpty(), () -> "users list should be empty");
    }

    @Test
    void usersSizeIfUserAdded() {
        System.out.println("Test 2: " + this);
        userService.add(new User());
        userService.add(new User());
        var users = userService.getAll();
        assertEquals(2, users.size());
    }

    @AfterEach
    void deleteDataFromDatabase() {
        System.out.println("After each " + this);
    }

    @AfterAll
    void closeConnectionPool() {
        System.out.println("After all: " + this);
    }
}
