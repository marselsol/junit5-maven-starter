package com.marselgaisin.junitTest.service;

import com.marselgaisin.junit.dto.User;
import com.marselgaisin.junit.service.UserService;
import com.marselgaisin.junitTest.paramresolver.UserServiceParamResolver;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
@Tag("user")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(
        {UserServiceParamResolver.class}
)
public class UserServiceTest {

    private static final User IVAN = User.of(1, "Ivan", "123");
    private static final User PETR = User.of(2, "Petr", "111");
    private UserService userService;

    UserServiceTest(TestInfo testinfo) {
        System.out.println();
    }

    @BeforeAll
    void init() {
        System.out.println("Before all: " + this);
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before each " + this);
        userService = new UserService();
    }

    @Test
    @Order(1)
    @DisplayName("name of  method")
    void usersEmptyIfNoUserAdded(UserService userService) {
        System.out.println("Test 1: " + this);
        var users = userService.getAll();
        assertTrue(users.isEmpty(), () -> "users list should be empty");
    }



    @Test
    void usersConvertedToMapById() {
        userService.add(IVAN, PETR);

        Map<Integer, User> users = userService.getAllConvertedById();

        assertAll(
                () -> assertThat(users).containsKeys(IVAN.getId(), PETR.getId()),
                () -> assertThat(users).containsValues(IVAN, PETR)
        );
    }

    @Test
    void usersSizeIfUserAdded() {
        System.out.println("Test 2: " + this);
        userService.add(IVAN);
        userService.add(PETR);
        var users = userService.getAll();
        assertThat(users).hasSize(2);
//        assertEquals(2, users.size());
    }

    @AfterEach
    void deleteDataFromDatabase() {
        System.out.println("After each " + this);
    }

    @AfterAll
    void closeConnectionPool() {
        System.out.println("After all: " + this);
    }

    @Nested
    @Tag("login")
    class loginTest {
        @Test
        void loginSuccessIfUserExists() {
            userService.add(IVAN);

            Optional<User> maybeUser = userService.login(IVAN.getUsername(), IVAN.getPassword());

            assertThat(maybeUser).isPresent();
            maybeUser.ifPresent(user -> assertThat(user).isEqualTo(IVAN));

//        assertTrue(maybeUser.isPresent());
//        maybeUser.ifPresent(user -> assertEquals(IVAN, user));
        }

        @Test
        void throwExceptionIfUsernameOrPasswordIsNull() {
            assertAll(
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, "dummy")),
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login("dummy", null)));
        }

        @Test
        void loginFailPasswordIdNotCorrect() {
            userService.add(IVAN);

            var maybeUser = userService.login(IVAN.getUsername(), "dummy");

            assertTrue(maybeUser.isEmpty());
        }

        @Test
        void loginFailIfUserDoesNotExist() {
            userService.add(IVAN);

            var maybeUser = userService.login("dummy", IVAN.getPassword());

            assertTrue(maybeUser.isEmpty());
        }
    }

}
