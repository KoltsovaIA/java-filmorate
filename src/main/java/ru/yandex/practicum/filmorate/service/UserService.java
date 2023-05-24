package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int id, int friendId) {
        userStorage.userIdIsExist(friendId);
        userStorage.userIdIsExist(id);
        User user = userStorage.getUserById(id);
        if (user.getFriends() == null) {
            user.setFriends(new LinkedHashSet<>());
        }
        if (!user.getFriends().add(friendId)) {
            log.error("Пользователь с id " + friendId + " уже добавлен в друзья у пользователя" +
                    userStorage.getUserById(id));
            throw new UserAlreadyExistException("Этот пользователь уже добавлен в друзья.");
        }
        log.info(user.toString());
        user.getFriends().add(friendId);
        userStorage.update(user);
        log.info("Пользователь " + userStorage.getUserById(friendId).getName() + " с id " + friendId +
                " добавлен в  друзья к пользователю " + userStorage.getUserById(id).getName() + " с id " + id);
    }

    public void deleteFriend(int id, int friendId) {
        userStorage.userIdIsExist(friendId);
        userStorage.userIdIsExist(id);
        User user = userStorage.getUserById(id);
        if (!user.getFriends().contains(friendId)) {
            log.error("Пользователя с id " + friendId + " нет в списке друзей.");
            throw new UserNotFoundException("Этого пользователя нет в друзьях.");
        }
        user.getFriends().remove(friendId);
        userStorage.update(user);
        log.info("Пользователь " + userStorage.getUserById(friendId).getName() + " удален из друзей");
        userStorage.getUserById(friendId).getFriends().remove(id);
        userStorage.update(userStorage.getUserById(friendId));
        log.info("Пользователь " + userStorage.getUserById(friendId).getName() + " удалил вас из друзей");
    }

    public Set<User> getFriends(int id) {
        userStorage.userIdIsExist(id);
        Set<Integer> friendsId = userStorage.getUserById(id).getFriends();
        Set<User> friends = new LinkedHashSet<>();
        if (friendsId == null) {
            friendsId = new LinkedHashSet<>();
        }
        for (Integer friendId : friendsId) {
            friends.add(userStorage.getUserById(friendId));
        }
        log.info("Список друзей сформирован: " + friends);
        return friends;
    }

    public Set<User> findCommonFriends(int firstId, int secondId) {
        userStorage.userIdIsExist(firstId);
        userStorage.userIdIsExist(secondId);
        HashSet<User> firstUserFriends = new HashSet<>(getFriends(firstId));
        HashSet<User> secondUserFriends = new HashSet<>(getFriends(secondId));
        try {
            firstUserFriends.retainAll(secondUserFriends);
        } catch (NullPointerException e) {
            firstUserFriends = new HashSet<>();
        }
        log.info("Список общих друзей сформирован: " + firstUserFriends);
        return firstUserFriends;
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public int getLastId() {
        return userStorage.getLastId();
    }

    public void userIdIsExist(int id) {
        userStorage.userIdIsExist(id);
    }
}