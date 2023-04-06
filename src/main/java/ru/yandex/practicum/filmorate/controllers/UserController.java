package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidEmailException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserBirthdayException;
import ru.yandex.practicum.filmorate.exceptions.UserLoginException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new InvalidEmailException("Адрес электронной почты не может быть пустым.");
        }
        if (!user.getEmail().contains("@")) {
            throw new InvalidEmailException("Некорректный адрес электронной почты");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new UserLoginException("Логин пользователя не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            throw new UserLoginException("Логин пользователя не может содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new UserBirthdayException("Дата рождения не может быть в будущем");
        }
        users.forEach((key, value) -> {
            if (value.getEmail().equals(user.getEmail())) {
                throw new UserAlreadyExistException("Пользователь с электронной почтой " +
                        user.getEmail() + " уже зарегистрирован.");
            }
        });
        users.put(users.size() + 1, user);
        log.info("Вы добавили пользователя " + user.getName());
        user.setId(users.size());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            create(user);
        } else {
            if (user.getEmail() == null || user.getEmail().isBlank()) {
                throw new InvalidEmailException("Адрес электронной почты не может быть пустым.");
            }
            if (!user.getEmail().contains("@")) {
                throw new InvalidEmailException("Некорректный адрес электронной почты");
            }
            if (user.getLogin() == null || user.getLogin().isBlank()) {
                throw new UserLoginException("Логин пользователя не может быть пустым");
            }
            if (user.getLogin().contains(" ")) {
                throw new UserLoginException("Логин пользователя не может содержать пробелы");
            }
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            if (user.getBirthday().isAfter(LocalDate.now())) {
                throw new UserBirthdayException("Дата рождения не может быть в будущем");
            }
            users.put(user.getId(), user);
            log.info("Вы обновили данные пользователя " + user.getName());
        }
        return user;
    }

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }
}