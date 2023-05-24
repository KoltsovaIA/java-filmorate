package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DbUserControllerTest {
    static final int WRONGID = 999999;
    private User user;
    private final UserController userController;

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
        int userId = userController.create(user).getId();
        user.setId(userId);
        User testUser = userController.getUserById(userId);
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
        int userId = userController.create(user).getId();
        User testUser = userController.getUserById(userId);
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
    void updateTest() {
        int userId = userController.create(user).getId();
        user.setId(userId);
        user.setName("Новое Имя");
        userController.update(user);
        User testUser = userController.getUserById(userId);
        assertEquals(user, testUser, "Метод update работает некорректно. Пользователи не совпадают");
        assertEquals(1, userController.getAllUsers().size(),
                "Метод update работает некорректно. Неверное число пользователей");
        assertThrows(UserNotFoundException.class, () -> userController.update(userController.getUserById(WRONGID)),
                "Метод update работает некорректно при запросе пользователя с некорректным id ");
    }

    @Test
    void getAllUsersTest() {
        int user1Id = userController.create(user).getId();
        User testUser1 = userController.getUserById(user1Id);
        user.setEmail("example1@example.ru");
        int user2Id = userController.create(user).getId();
        User testUser2 = userController.getUserById(user2Id);
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
        int userId = userController.create(user).getId();
        assertEquals(user, userController.getUserById(userId),
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
        int user1Id = userController.create(user).getId();
        user.setEmail("2@2.ru");
        int user2Id = userController.create(user).getId();
        userController.addFriend(user1Id, user2Id);
        assertEquals(1, userController.getFriends(user1Id).size(),
                "Метод addFriend работает некорректно. Пользователь не добавлен в друзья");
        assertTrue(userController.getFriends(user1Id).contains(userController.getUserById(user2Id)));
    }

    @Test
    void addFriendWithIncorrectAttributesTest() {
        assertThrows(UserNotFoundException.class, () -> userController.addFriend(WRONGID, WRONGID + 1),
                "Метод addFriend работает некорректно при запросе с некорректным id");
        assertThrows(UserNotFoundException.class, ()
                        -> userController.addFriend(WRONGID * (-1), (WRONGID + 1) * (-1)),
                "Метод addFriend работает некорректно при запросе с некорректным id");
        int user1Id = userController.create(user).getId();
        user.setEmail("2@2.ru");
        int user2Id = userController.create(user).getId();
        userController.addFriend(user1Id, user2Id);
        assertThrows(UserAlreadyExistException.class, () -> userController.addFriend(user1Id, user2Id),
                "Метод addFriend работает некорректно при попытке второй раз добавить в друзья");
    }

    @Test
    void deleteFriendWithCorrectAttributesTest() {
        int user1Id = userController.create(user).getId();
        user.setEmail("2@2.ru");
        int user2Id = userController.create(user).getId();
        userController.addFriend(user1Id, user2Id);
        userController.deleteFriend(user1Id, user2Id);
        assertEquals(0, userController.getFriends(user1Id).size(),
                "Метод deleteFriend работает некорректно. Пользователь не удален из друзей");
        assertEquals(0, userController.getFriends(user2Id).size(),
                "Метод deleteFriend работает некорректно. Пользователь не удален из друзей");
    }

    @Test
    void deleteFriendWithIncorrectAttributesTest() {
        assertThrows(UserNotFoundException.class, () -> userController.deleteFriend(WRONGID, WRONGID + 1),
                "Метод deleteFriend работает некорректно при попытке удалить из друзей с некорректным id");
        assertThrows(UserNotFoundException.class, ()
                        -> userController.deleteFriend(WRONGID * (-1), (WRONGID + 1) * (-1)),
                "Метод deleteFriend работает некорректно при попытке удалить из друзей с некорректным id");
        int user1Id = userController.create(user).getId();
        user.setEmail("2@2.ru");
        int user2Id = userController.create(user).getId();
        userController.addFriend(user1Id, user2Id);
        userController.deleteFriend(user1Id, user2Id);
        assertThrows(UserNotFoundException.class, () -> userController.deleteFriend(user1Id, user2Id),
                "Метод deleteFriend работает некорректно при попытке удалить несуществующего друга");
    }

    @Test
    void getFriendsTest() {
        int user1Id = userController.create(user).getId();
        assertEquals(0, userController.getFriends(user1Id).size(), "Метод getFriends работает некорректно");
        user.setEmail("2@2.ru");
        int user2Id = userController.create(user).getId();
        user.setEmail("3@3.ru");
        int user3Id = userController.create(user).getId();
        userController.addFriend(user1Id, user2Id);
        userController.addFriend(user1Id, user3Id);
        assertEquals(2, userController.getFriends(user1Id).size(), "Метод getFriends работает некорректно");
        Set<User> friends = new LinkedHashSet<>();
        friends.add(userController.getUserById(user2Id));
        friends.add(userController.getUserById(user3Id));
        assertEquals(friends, userController.getFriends(user1Id), "Метод getFriends работает некорректно");
        assertThrows(UserNotFoundException.class, () -> userController.getFriends(WRONGID),
                "Метод getFriends работает некорректно при попытке получить список друзей " +
                        "пользователя с некорректным id");
    }

    @Test
    void findCommonFriendsTest() {
        int user1Id = userController.create(user).getId();
        user.setEmail("2@2.ru");
        int user2Id = userController.create(user).getId();
        user.setEmail("3@3.ru");
        int user3Id = userController.create(user).getId();
        userController.addFriend(user1Id, user3Id);
        userController.addFriend(user2Id, user3Id);
        assertEquals(1, userController.findCommonFriends(user1Id, user2Id).size(),
                "Метод findCommonFriends работает некорректно");
        assertTrue(userController.findCommonFriends(user1Id, user2Id).contains(userController.getUserById(user3Id)),
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