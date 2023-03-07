package service;

import ParamResorver.UserServiceParamResolver;
import junet.dto.User;
import junet.dao.UserDAO;
import junet.service.UserService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
//@TestMethodOrder(MethodOrderer.DisplayName.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(UserServiceParamResolver.class)
class UserServiceTest {
    private UserService userService;
    private UserDAO userDAO;
    public static final User IVAN = User.of(1, "Ivan", "123");
    public static final User PETR = User.of(2, "Petr", "456");


    @BeforeAll
    void init() {
        System.out.println("Before all: " + this);
    }

    @BeforeEach
    void prepare(UserService userService) {
        System.out.println("Before each: " + this);
        this.userDAO = Mockito.mock(UserDAO.class);
        this.userService = new UserService(userDAO);
    }
    @Test
    void shouldDeleteExistedUser () {
        userService.add(IVAN);
  //      Mockito.doReturn(true).when(userDAO).delete(IVAN.getId()); //первый вариант
   //     Mockito.doReturn(true).when(userDAO).delete(Mockito.any()); //второй втариант без указнаия getId()
        Mockito.when(userDAO.delete(IVAN.getId()))
                .thenReturn(true)
                .thenReturn(false);
        boolean delete = userService.delete(IVAN.getId());
        System.out.println(userService.delete(IVAN.getId()));
        System.out.println(userService.delete(IVAN.getId()));
        assertThat(delete).isTrue();

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
    @Timeout(value = 200, unit = TimeUnit.MILLISECONDS)
    class TestLogin {
        @Test
        @Disabled("ignor test")
            //     @Tag("login")
        void loginNameNotCorrect() {
            userService.add(IVAN);
            Optional<User> optionalUser = userService.login("ppppp", IVAN.getPassword());
            assertTrue(optionalUser.isEmpty());
        }

        //       @Test
//        @Tag("login")
        @RepeatedTest(value = 5, name = RepeatedTest.LONG_DISPLAY_NAME)
        void loginPasswordNotCorrect(RepetitionInfo repetitionInfo) {
            userService.add(IVAN);
            Optional<User> optionalUser = userService.login(IVAN.getUsername(), "oooooo");
            assertTrue(optionalUser.isEmpty());
        }

        @Test
        void checkinLoginFunctionalityPerformance() {
            System.out.println(Thread.currentThread().getName());
            assertTimeoutPreemptively(Duration.ofMillis(200L), () -> {
                System.out.println(Thread.currentThread().getName());
                Thread.sleep(300L);
                return userService.login(IVAN.getUsername(), "oooooo");
            });
        }

        @Test
            //       @Tag("login")
        void throwExceptionIfUsernameOrPasswordNull() {
            try {
                userService.login("null", null);
                fail("login is null trows exception");
            } catch (IllegalArgumentException error) {
                assertTrue(true);
            }

            // второй вариант
            assertAll(
                    () -> {
                        IllegalArgumentException aNull = assertThrows(IllegalArgumentException.class, () -> userService.login("null", null));
                        assertThat(aNull.getMessage()).isEqualTo("username or password is not null");
                    },
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login(null, "null"))
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


        @ParameterizedTest(name = "{arguments} test")
//        @NullSource
//        @EmptySource
//        @ValueSource(strings = {"Ivan", "Petr"})
        @MethodSource("service.UserServiceTest#getArgumentForLoginTest")
        // @CsvSource()
        @DisplayName("login param test")
        void loginParametrizedTest(String name, String password, Optional<User> user) {
            userService.add(IVAN, PETR);
            Optional<User> loginUser = userService.login(name, password);
            assertThat(loginUser).isEqualTo(user);
        }


    }

    static Stream<Arguments> getArgumentForLoginTest() {
        return Stream.of(
                Arguments.of("Ivan", "123", Optional.of(IVAN)),
                Arguments.of("Petr", "456", Optional.of(PETR)),
                Arguments.of("Ivan", "852", Optional.empty()),
                Arguments.of("oijh", "123", Optional.empty())
        );
    }
}
