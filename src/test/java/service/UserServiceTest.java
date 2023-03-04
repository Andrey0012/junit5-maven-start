package service;

import ParamResorver.UserServiceParamResolver;
import junet.dto.User;
import junet.service.UserService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
//@TestMethodOrder(MethodOrderer.DisplayName.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(UserServiceParamResolver.class)
class UserServiceTest {
    private UserService userService;
    User IVAN = User.of(1, "Ivan", "123");
    User PETR = User.of(2, "Petr", "456");


    @BeforeAll
    void init() {
        System.out.println("Before all: " + this);
    }

    @BeforeEach
    void prepare(UserService userService) {
        System.out.println("Before each: " + this);
        this.userService= userService;

    }

    @Test
    void userEmptyIfNoUsersAdded() {
        System.out.println("Test 1: " + this);
        List<User> all = userService.getAll();
        MatcherAssert.assertThat(all, empty());
        assertTrue(all.isEmpty());
    }

    @Test
    void usersSizeIfUserAdded() {
        System.out.println("Test 2: " + this);
        userService.add(IVAN);
        userService.add(PETR);
        List<User> all = userService.getAll();

        //можно использовать два варианта
        assertEquals(2, all.size());
        //     assertThat(all).hasSize(2);

    }



    @Test
    void usersConvertToMapById() {
        userService.add(IVAN, PETR);
        Map<Integer, User> userMap = userService.getAllConvertedById();
        MatcherAssert.assertThat(userMap, IsMapContaining.hasKey(IVAN.getId()));
        assertAll(
                () -> assertThat(userMap).containsKeys(IVAN.getId(), PETR.getId()),
                () -> assertThat(userMap).containsValues(IVAN, PETR)
        );
        assertThat(userMap).containsKey(2);
    }

    @AfterEach
    void deleteBase() {
        System.out.println("After each: " + this);
    }

    @AfterAll
    void closeEnd() {
        System.out.println("After all: " + this);

    }
    @Nested
    @DisplayName("test user login")
    @Tag("login")
    class TestLogin {
        @Test
   //     @Tag("login")
        void loginNameNotCorrect() {
            userService.add(IVAN);
            Optional<User> optionalUser = userService.login("ppppp", IVAN.getPassword());
            assertTrue(optionalUser.isEmpty());
        }
        @Test
 //       @Tag("login")
        void throwExceptionIfUsernameOrPasswordNull() {
            try {
                userService.login("null", null);
                fail("login is null trows exception");
            }
            catch (IllegalArgumentException error) {
                assertTrue(true);
            }

            // второй вариант
            assertAll(
                    () -> {
                        IllegalArgumentException aNull = assertThrows(IllegalArgumentException.class, () -> userService.login("null", null));
                        assertThat(aNull.getMessage()).isEqualTo("username or password is not null");
                    },
                    () -> assertThrows(IllegalArgumentException.class, () ->userService.login(null, "null"))
            );
        }
        @Test
 //       @Tag("login")
        void loginSuccessIfUserExists() {
            userService.add(IVAN);
            Optional<User> user = userService.login(IVAN.getUsername(), IVAN.getPassword());
            //можно использовать два варианта
            assertThat(user).isPresent();
            user.ifPresent(user1 -> assertThat(user1).isEqualTo(IVAN));

//        assertTrue(user.isPresent());
//        user.ifPresent(user1 -> assertEquals(IVAN, user1));
        }

        @Test
//        @Tag("login")
        void loginPasswordNotCorrect() {
            userService.add(IVAN);
            Optional<User> optionalUser = userService.login(IVAN.getUsername(), "oooooo");
            assertTrue(optionalUser.isEmpty());
        }


    }
}
