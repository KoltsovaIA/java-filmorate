package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Component("inMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 0;

    public User create(User user) {
        checkUser(user);
        users.forEach((key, value) -> {
            if (value.getEmail().equals(user.getEmail())) {
                throw new UserAlreadyExistException("Пользователь с электронной почтой " +
                        user.getEmail() + " уже зарегистрирован.");
            }
        });
        user.setId(getNewId());
        user.setFriends(new LinkedHashSet<>());
        users.put(user.getId(), User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .friends(user.getFriends())
                .build()
        );
        log.info("Добавлен пользователь " + user.getName() + users.size());
        log.info(user.toString());
        return user;
    }

    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден.");
        }
        checkUser(user);
        users.put(user.getId(), User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .friends(user.getFriends())
                .build()
        );
        log.info("Обновлен пользователь " + user.getName());
        return user;
    }

    public List<User> getAllUsers() {
        ArrayList<User> usersList = new ArrayList<>();
        for (User value : users.values()) {
            usersList.add(User.builder()
                    .id(value.getId())
                    .email(value.getEmail())
                    .login(value.getLogin())
                    .name(value.getName())
                    .birthday(value.getBirthday())
                    .friends(value.getFriends())
                    .build());
        }
        return usersList;
    }

    public User getUserById(int id) {
        userIdIsExist(id);
        User user = users.get(id);
        return User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .friends(user.getFriends())
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
            throw new IncorrectParameterException("Адрес электронной почты не может быть пустым.");
        }
        if (!user.getEmail().contains("@")) {
            throw new IncorrectParameterException("Некорректный адрес электронной почты");
        }
        if (StringUtils.isBlank(user.getLogin())) {
            throw new IncorrectParameterException("Логин пользователя не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            throw new IncorrectParameterException("Логин пользователя не может содержать пробелы");
        }
        if (StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && !user.getBirthday().toString().isBlank() &&
                user.getBirthday().isAfter(LocalDate.now())) {
            throw new IncorrectParameterException("Дата рождения не может быть в будущем");
        }
    }

    public void userIdIsExist(int id) {
        if ((id <= 0) || (!users.containsKey(id))) {
            log.error("Передан некорректный id " + id);
            throw new UserNotFoundException("Некорректный id " + id);
        }
    }
}