package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    @PostMapping
    public User create(@RequestBody User user) {
        checkUser(user);
        users.forEach((key, value) -> {
            if (value.getEmail().equals(user.getEmail())) {
                throw new UserAlreadyExistException("Пользователь с электронной почтой " +
                        user.getEmail() + " уже зарегистрирован.");
            }
        });
        user.setId(getNewId());
        users.put(user.getId(), User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .build()
        );
        log.info("Вы добавили пользователя " + user.getName() + users.size());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            return create(user);
        }
        checkUser(user);
        users.put(user.getId(), User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .build()
        );
        log.info("Вы обновили данные пользователя " + user.getName());
        return user;
    }

    @GetMapping
    public List<User> getAll() {
        ArrayList<User> usersList = new ArrayList<>();
        for (User value : users.values()) {
            usersList.add(User.builder()
                    .id(value.getId())
                    .email(value.getEmail())
                    .login(value.getLogin())
                    .name(value.getName())
                    .birthday(value.getBirthday())
                    .build());
        }
        return usersList;
    }

    @GetMapping(path = {"/user"})
    public User getById(@RequestBody int id) {
        User user = users.get(id);
        return User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .build();
    }

    public int getNewId() {
        return ++id;
    }

    public int getLastId() {
        return id;
    }

    private void checkUser(User user) {
        if (StringUtils.isBlank(user.getEmail())) {
            throw new InvalidEmailException("Адрес электронной почты не может быть пустым.");
        }
        if (!user.getEmail().contains("@")) {
            throw new InvalidEmailException("Некорректный адрес электронной почты");
        }
        if (StringUtils.isBlank(user.getLogin())) {
            throw new UserLoginException("Логин пользователя не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            throw new UserLoginException("Логин пользователя не может содержать пробелы");
        }
        if (StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && !user.getBirthday().toString().isBlank() &&
                user.getBirthday().isAfter(LocalDate.now())) {
            throw new UserBirthdayException("Дата рождения не может быть в будущем");
        }
    }
}