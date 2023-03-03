import junet.dto.User;
import junet.service.UserService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {
    private UserService userService;
    User IVAN = User.of(1, "Ivan", "123");
    User PETR = User.of(2, "Petr", "456");


    @BeforeAll
    void init() {
        System.out.println("Before all: " + this);
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before each: " + this);
        userService = new UserService();

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
    void loginPasswordNotCorrect() {
        userService.add(IVAN);
        Optional<User> optionalUser = userService.login(IVAN.getUsername(), "oooooo");
        assertTrue(optionalUser.isEmpty());
    }

    @Test
    void loginNameNotCorrect() {
        userService.add(IVAN);
        Optional<User> optionalUser = userService.login("ppppp", IVAN.getPassword());
        assertTrue(optionalUser.isEmpty());
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
}
