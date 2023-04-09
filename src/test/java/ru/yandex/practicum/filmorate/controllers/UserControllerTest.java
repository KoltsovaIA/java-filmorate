package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private User user;
    private final UserController userController = new UserController();

    @BeforeEach
    public void beforeEach() {
        user = User.builder()
                .email("example@example.ru")
                .login("Логин")
                .name("Имя")
                .birthday(LocalDate.of(2000, 10, 15))
                .build();
    }

    @Test
    void createWithCorrectAttributesTest() {
        userController.create(user);
        User testUser = userController.getById(userController.getLastId());
        assertEquals(user, testUser, "Пользователи не совпадают");
        testUser.setLogin("Kjf");
        assertNotEquals(user, testUser, "Пользователи совпадают");
        assertEquals(1, userController.getAll().size(), "Неверное число пользователей");
    }

    @Test
    void createWithIncorrectEmailTest() {
        user.setEmail("IncorrectEmail");
        assertThrows(InvalidEmailException.class, () -> userController.create(user));
        assertEquals(0, userController.getAll().size(),
                "Сохранен пользователь с некорректным e-mail");
    }

    @Test
    void createWithEmptyLoginTest() {
        user.setLogin("");
        assertThrows(UserLoginException.class, () -> userController.create(user));
        assertEquals(0, userController.getAll().size(),
                "Сохранен пользователь с пустым логином");
    }

    @Test
    void createWithIncorrectLoginTest() {
        user.setLogin("Ло гин");
        assertThrows(UserLoginException.class, () -> userController.create(user));
        assertEquals(0, userController.getAll().size(),
                "Сохранен пользователь с некорректным логином");
    }

    @Test
    void createWithEmptyNameTest() {
        user.setName(null);
        user.setLogin("Логин");
        userController.create(user);
        User testUser = userController.getById(userController.getLastId());
        assertEquals("Логин", testUser.getName(),
                "Добавлен пользователя c пустым именем, имя должно равняться логину");
        assertEquals(1, userController.getAll().size(), "Неверное число пользователей");
    }

    @Test
    void createWithIncorrectBirthdayTest() {
        user.setBirthday(LocalDate.now().plusYears(2));
        assertThrows(UserBirthdayException.class, () -> userController.create(user),
                "Создан пользователь c датой рождения в будущем");
        assertEquals(0, userController.getAll().size(),
                "Неверное число пользователей");
    }

    @Test
    void createWithSameEmailTest() {
        userController.create(user);
        assertThrows(UserAlreadyExistException.class, () -> userController.create(user),
                "Создан пользователь c существующим e-mail");
        assertEquals(1, userController.getAll().size(),
                "Неверное число пользователей");
    }

    @Test
    void updateTest() throws Exception {
        userController.create(user);
        user.setId(userController.getLastId());
        user.setName("Новое Имя");
        userController.update(user);
        User testUser = userController.getById(userController.getLastId());
        assertEquals(user, testUser, "Пользователи не совпадают");
        assertEquals(1, userController.getAll().size(), "Неверное число пользователей");
    }

    @Test
    void getAllTest() {
        userController.create(user);
        User testUser1 = userController.getById(userController.getLastId());
        user.setEmail("example1@example.ru");
        userController.create(user);
        User testUser2 = userController.getById(userController.getLastId());
        List<User> testUsers = userController.getAll();
        assertEquals(2, testUsers.size(), "Неверное число пользователей");
        assertEquals(testUser1, testUsers.get(0), "Пользователи не совпадают");
        testUser2.setName("nvfdksn");
        assertNotEquals(testUser2, testUsers.get(1), "Пользователи совпадают");
    }
}