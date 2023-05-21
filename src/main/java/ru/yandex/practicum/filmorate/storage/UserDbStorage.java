package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component("userDbStorage")
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAllUsers() {
        LinkedList<User> users = new LinkedList<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users");
        while (userRows.next()) {
            int id = userRows.getInt("user_id");
            User user = User.builder()
                    .id(id)
                    .email(userRows.getString("user_email"))
                    .login(userRows.getString("user_login"))
                    .name(userRows.getString("user_name"))
                    .birthday(Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate())
                    .friends(getFriendsOfUser(id))
                    .build();
            users.add(user);
        }
        return users;
    }

    @Override
    public User create(User user) {
        checkUser(user);
        jdbcTemplate.update("INSERT INTO users (user_email, user_login, user_name, birthday) VALUES (?, ?, ?, ?)",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday()
        );
        user.setId(getLastId());
        return user;
    }

    @Override
    public User update(User user) {
        checkUser(user);
        userIdIsExist(user.getId());
        jdbcTemplate.update("UPDATE users SET user_email = ?, user_login = ?, user_name = ?, birthday = ? " +
                        "WHERE user_id = ?",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        if (user.getFriends() != null) {
            jdbcTemplate.update("DELETE FROM friendship WHERE user_id = ?;", user.getId());
            for (int friend : user.getFriends()) {
                jdbcTemplate.update("INSERT INTO friendship (user_id, status, friend_id ) values (?, ?, ?)",
                        user.getId(), true, friend);
            }
        }
        return user;
    }

    @Override
    public User getUserById(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id = ?", id);
        if (userRows.next()) {
            User user = User.builder()
                    .id(userRows.getInt("user_id"))
                    .email(userRows.getString("user_email"))
                    .login(userRows.getString("user_login"))
                    .name(userRows.getString("user_name"))
                    .birthday(Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate())
                    .friends(getFriendsOfUser(id))
                    .build();
            return user;
        } else {
            log.error("Пользователь с идентификатором {} не найден.", id);
            throw new UserNotFoundException("Пользователь с id " + id + " не найден.");
        }
    }

    @Override
    public int getLastId() {
        SqlRowSet userRow = jdbcTemplate.queryForRowSet("SELECT max(user_id) FROM users");
        if (userRow.next()) {
            return userRow.getInt(1);
        }
        return 0;
    }

    @Override
    public void userIdIsExist(int id) {
        boolean b = Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT FROM users WHERE user_id = ?)", Boolean.class, id));
        if (!b) {
            log.error("Передан некорректный id " + id);
            throw new UserNotFoundException("Некорректный id " + id);
        }
    }

    private Set<Integer> getFriendsOfUser(int id) {
        Set<Integer> friends = new HashSet<>();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM friendship WHERE user_id = ?", id);
        while (userRows.next()) {
            friends.add(userRows.getInt("friend_id"));
        }
        return friends;
    }

    public void deleteUser(int id) {
        if (jdbcTemplate.update("DELETE FROM users WHERE user_id = ? ", id) == 0) {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден!");
        }
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
}