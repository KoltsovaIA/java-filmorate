package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private User user;
    UserController userController = new UserController();

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
        User user1 = userController.create(user);
        assertEquals(user, user1, "Пользователи совпадают");
        assertEquals(1, userController.getAll().size(), "Добавление пользователя прошло успешно");
    }

    @Test
    void createWithIncorrectEmailTest() {
        user.setEmail("IncorrectEmail");
        assertThrows(InvalidEmailException.class, () -> userController.create(user));
        assertEquals(0, userController.getAll().size(),
                "Пользователь с некорректным e-mail не сохранен");
    }

    @Test
    void createWithEmptyLoginTest() {
        user.setLogin("");
        assertThrows(UserLoginException.class, () -> userController.create(user));
        assertEquals(0, userController.getAll().size(), "Пользователь с пустым логином не сохранен");
    }

    @Test
    void createWithIncorrectLoginTest() {
        user.setLogin("Ло гин");
        assertThrows(UserLoginException.class, () -> userController.create(user));
        assertEquals(0, userController.getAll().size(),
                "Пользователь с некорректным логином не сохранен");
    }

    @Test
    void createWithEmptyNameTest() {
        user.setName(null);
        user.setLogin("Логин");
        User user1 = userController.create(user);
        assertEquals("Логин", user1.getName(),
                "Добавление пользователя c пустым именем прошло успешно");
        assertEquals(1, userController.getAll().size(), "Добавление пользователя прошло успешно");
    }

    @Test
    void createWithIncorrectBirthdayTest() {
        user.setBirthday(LocalDate.now().plusYears(2));
        assertThrows(UserBirthdayException.class, () -> userController.create(user),
                "Попытка создать пользователя c датой рождения в будущем");
        assertEquals(0, userController.getAll().size(),
                "Пользователь с датой рождения в будущем не сохранен");
    }

    @Test
    void createWithSameEmailTest() {
        userController.update(user);
        user.setEmail("example@example.ru");
        assertThrows(UserAlreadyExistException.class, () -> userController.create(user),
                "Попытка создать пользователя c существующим e-mail");
        assertEquals(1, userController.getAll().size(),
                "Фильм с существующим названием не сохранен");
    }

    @Test
    void updateTest() {
        userController.create(user);
        User user1 = User.builder()
                .id(1)
                .email("example@example.ru")
                .login("Логин")
                .name("Новое Имя")
                .birthday(LocalDate.of(2000, 10, 15))
                .build();
        userController.update(user1);
        assertEquals(1, userController.getAll().size(), "Пользователь успешно обновлен");
    }

    @Test
    void getAllTest() {
        userController.create(user);
        User user1 = User.builder()
                .email("example1@example.ru")
                .login("Логин")
                .name("Имя")
                .birthday(LocalDate.of(2000, 10, 15))
                .build();
        userController.create(user1);
        assertEquals(2, userController.getAll().size(), "Пользователи возвращаются не корректно");
    }
}