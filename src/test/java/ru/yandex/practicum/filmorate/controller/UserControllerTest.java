package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    static final int WRONGID = 999999;
    private User user;
    private final InMemoryUserStorage userStorage = new InMemoryUserStorage();
    private final UserService userService = new UserService(userStorage);
    private final UserController userController = new UserController(userService);

    @BeforeEach
    public void beforeEach() {
        user = User.builder()
                .email("example@example.ru")
                .login("Логин")
                .name("Имя")
                .birthday(LocalDate.of(2000, 10, 15))
                .friends(new HashSet<>())
                .build();
    }

    @Test
    void createWithCorrectAttributesTest() {
        userController.create(user);
        User testUser = userController.getUserById(userStorage.getLastId());
        System.out.println(testUser.getId());
        assertEquals(user, testUser, "Метод create работает некорректно. Пользователи не совпадают");
        testUser.setLogin("Kjf");
        assertNotEquals(user, testUser, "Метод create работает некорректно. Пользователи совпадают");
        assertEquals(1, userController.getAllUsers().size(), "Метод create работает некорректно." +
                "Неверное число пользователей");
    }

    @Test
    void createWithIncorrectEmailTest() {
        user.setEmail("IncorrectEmail");
        assertThrows(IncorrectParameterException.class, () -> userController.create(user),
                "Метод create работает некорректно при попытке создать пользователя с неверным e-mail");
        assertEquals(0, userController.getAllUsers().size(),
                "Метод create работает некорректно. Сохранен пользователь с некорректным e-mail");
    }

    @Test
    void createWithEmptyLoginTest() {
        user.setLogin("");
        assertThrows(IncorrectParameterException.class, () -> userController.create(user));
        assertEquals(0, userController.getAllUsers().size(),
                "Метод create работает некорректно. Сохранен пользователь с пустым логином");
    }

    @Test
    void createWithIncorrectLoginTest() {
        user.setLogin("Ло гин");
        assertThrows(IncorrectParameterException.class, () -> userController.create(user));
        assertEquals(0, userController.getAllUsers().size(),
                "Метод create работает некорректно. Сохранен пользователь с некорректным логином");
    }

    @Test
    void createWithEmptyNameTest() {
        user.setName(null);
        user.setLogin("Логин");
        userController.create(user);
        User testUser = userController.getUserById(userService.getLastId());
        assertEquals("Логин", testUser.getName(),
                "Метод create работает некорректно. " +
                        "Добавлен пользователя c пустым именем, имя должно равняться логину");
        assertEquals(1, userController.getAllUsers().size(),
                "Метод create работает некорректно.Неверное число пользователей");
    }

    @Test
    void createWithIncorrectBirthdayTest() {
        user.setBirthday(LocalDate.now().plusYears(2));
        assertThrows(IncorrectParameterException.class, () -> userController.create(user),
                "Метод create работает некорректно. Создан пользователь c датой рождения в будущем");
        assertEquals(0, userController.getAllUsers().size(),
                "Метод create работает некорректно. Неверное число пользователей");
    }

    @Test
    void createWithSameEmailTest() {
        userController.create(user);
        assertThrows(UserAlreadyExistException.class, () -> userController.create(user),
                "Метод create работает некорректно. Создан пользователь c существующим e-mail");
        assertEquals(1, userController.getAllUsers().size(),
                "Метод create работает некорректно. Неверное число пользователей");
    }

    @Test
    void updateTest() {
        userController.create(user);
        user.setId(userService.getLastId());
        user.setName("Новое Имя");
        userController.update(user);
        User testUser = userController.getUserById(userService.getLastId());
        assertEquals(user, testUser, "Метод update работает некорректно. Пользователи не совпадают");
        assertEquals(1, userController.getAllUsers().size(),
                "Метод update работает некорректно. Неверное число пользователей");
        assertThrows(UserNotFoundException.class, () -> userController.update(userController.getUserById(WRONGID)),
                "Метод update работает некорректно при запросе пользователя с некорректным id ");
    }

    @Test
    void getAllUsersTest() {
        userController.create(user);
        User testUser1 = userController.getUserById(userService.getLastId());
        user.setEmail("example1@example.ru");
        userController.create(user);
        User testUser2 = userController.getUserById(userService.getLastId());
        List<User> testUsers = userController.getAllUsers();
        assertEquals(2, testUsers.size(),
                "Метод getAllUsers работает некорректно. Неверное число пользователей");
        assertEquals(testUser1, testUsers.get(0),
                "Метод getAllUsers работает некорректно. Пользователи не совпадают");
        testUser2.setName("nvfdksn");
        assertNotEquals(testUser2, testUsers.get(1),
                "Метод getAllUsers работает некорректно. Пользователи совпадают");
    }

    @Test
    void getUserByIdWithCorrectAttributesTest() {
        userController.create(user);
        assertEquals(user, userController.getUserById(1),
                "Метод getUserById работает некорректно. Пользователи не совпадают");
    }

    @Test
    void getUserByIdWithIncorrectIdTest() {
        userController.create(user);
        assertThrows(UserNotFoundException.class, () -> userController.getUserById(WRONGID),
                "Метод getUserById работает некорректно при запросе несуществующего пользователя");
        assertThrows(UserNotFoundException.class, () -> userController.getUserById(WRONGID * (-1)),
                "Метод getUserById работает некорректно при запросе пользователя с некорректным id ");
    }

    @Test
    void addFriendWithCorrectAttributesTest() {
        userController.create(user);
        user.setEmail("2@2.ru");
        userController.create(user);
        userController.addFriend(1, 2);
        assertEquals(1, userController.getFriends(1).size(),
                "Метод addFriend работает некорректно. Пользователь не добавлен в друзья");
        assertTrue(userController.getFriends(1).contains(userController.getUserById(2)));
        assertTrue(userController.getFriends(1).contains(userController.getUserById(2)));
    }

    @Test
    void addFriendWithIncorrectAttributesTest() {
        assertThrows(UserNotFoundException.class, () -> userController.addFriend(WRONGID, WRONGID + 1),
                "Метод addFriend работает некорректно при запросе с некорректным id");
        assertThrows(UserNotFoundException.class, ()
                        -> userController.addFriend(WRONGID * (-1), (WRONGID + 1) * (-1)),
                "Метод addFriend работает некорректно при запросе с некорректным id");
        userController.create(user);
        user.setEmail("2@2.ru");
        userController.create(user);
        userController.addFriend(1, 2);
        assertThrows(UserAlreadyExistException.class, () -> userController.addFriend(1, 2),
                "Метод addFriend работает некорректно при попытке второй раз добавить в друзья");
    }

    @Test
    void deleteFriendWithCorrectAttributesTest() {
        userController.create(user);
        user.setEmail("2@2.ru");
        userController.create(user);
        userController.addFriend(1, 2);
        userController.deleteFriend(1, 2);
        assertEquals(0, userController.getFriends(1).size(),
                "Метод deleteFriend работает некорректно. Пользователь не удален из друзей");
        assertEquals(0, userController.getFriends(2).size(),
                "Метод deleteFriend работает некорректно. Пользователь не удален из друзей");
    }

    @Test
    void deleteFriendWithIncorrectAttributesTest() {
        assertThrows(UserNotFoundException.class, () -> userController.deleteFriend(WRONGID, WRONGID + 1),
                "Метод deleteFriend работает некорректно при попытке удалить из друзей с некорректным id");
        assertThrows(UserNotFoundException.class, ()
                        -> userController.deleteFriend(WRONGID * (-1), (WRONGID + 1) * (-1)),
                "Метод deleteFriend работает некорректно при попытке удалить из друзей с некорректным id");
        userController.create(user);
        user.setEmail("2@2.ru");
        userController.create(user);
        userController.addFriend(1, 2);
        userController.deleteFriend(1, 2);
        assertThrows(UserNotFoundException.class, () -> userController.deleteFriend(1, 2),
                "Метод deleteFriend работает некорректно при попытке удалить несуществующего друга");
    }

    @Test
    void getFriendsTest() {
        userController.create(user);
        assertEquals(0, userController.getFriends(1).size(), "Метод getFriends работает некорректно");
        user.setEmail("2@2.ru");
        userController.create(user);
        user.setEmail("3@3.ru");
        userController.create(user);
        userController.addFriend(1, 2);
        userController.addFriend(1, 3);
        assertEquals(2, userController.getFriends(1).size(), "Метод getFriends работает некорректно");
        Set<User> friends = new LinkedHashSet<>();
        friends.add(userController.getUserById(2));
        friends.add(userController.getUserById(3));
        assertEquals(friends, userController.getFriends(1), "Метод getFriends работает некорректно");
        assertThrows(UserNotFoundException.class, () -> userController.getFriends(WRONGID),
                "Метод getFriends работает некорректно при попытке получить список друзей " +
                        "пользователя с некорректным id");
    }

    @Test
    void findCommonFriendsTest() {
        userController.create(user);
        user.setEmail("2@2.ru");
        userController.create(user);
        user.setEmail("3@3.ru");
        userController.create(user);
        userController.addFriend(1, 3);
        userController.addFriend(2, 3);
        assertEquals(1, userController.findCommonFriends(1, 2).size(),
                "Метод findCommonFriends работает некорректно");
        assertTrue(userController.findCommonFriends(1, 2).contains(userController.getUserById(3)),
                "Метод findCommonFriends работает некорректно. Общие друзья найдены не верно/не найдены");
        assertThrows(UserNotFoundException.class, () -> userController.findCommonFriends(WRONGID, WRONGID + 1),
                "Метод findCommonFriends работает некорректно при попытке вызвать " +
                        "пользователей с несуществующими id");
        assertThrows(UserNotFoundException.class, () ->
                        userController.findCommonFriends(WRONGID * (-1), (WRONGID + 1) * (-1)),
                "Метод findCommonFriends работает некорректно при попытке вызвать " +
                        "пользователей с несуществующими id");
    }
}