package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Set<Integer> addFriend(int id, int friendId) {
        isIdExist(friendId);
        isIdExist(id);
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        if (user.getFriends() == null) {
            user.setFriends(new LinkedHashSet<>());
        }
        if (friend.getFriends() == null) {
            friend.setFriends(new LinkedHashSet<>());
        }
        if (!user.getFriends().add(friendId)) {
            log.error("Пользователь с id " + friendId + " уже добавлен в друзья.");
            throw new UserAlreadyExistException("Этот пользователь уже добавлен в друзья.");
        }
        log.info(user.toString());
        userStorage.update(user);
        log.info("Пользователь " + userStorage.getUserById(friendId).getName() + " добавлен в друзья");
        friend.getFriends().add(id);
        log.info(friend.toString());
        userStorage.update(friend);
        log.info("Пользователь " + userStorage.getUserById(friendId).getName() + " добавил вас в друзья");
        return userStorage.getUserById(id).getFriends();
    }

    public Set<Integer> deleteFriend(int id, int friendId) {
        isIdExist(friendId);
        isIdExist(id);
        if (!userStorage.getUserById(id).getFriends().contains(friendId)) {
            log.error("Пользователя с id " + friendId + " нет в списке друзей.");
            throw new UserNotFoundException("Этого пользователя нет в друзьях.");
        }
        userStorage.getUserById(id).getFriends().remove(friendId);
        userStorage.update(userStorage.getUserById(id));
        log.info("Пользователь " + userStorage.getUserById(friendId).getName() + " удален из друзей");
        userStorage.getUserById(friendId).getFriends().remove(id);
        userStorage.update(userStorage.getUserById(friendId));
        log.info("Пользователь " + userStorage.getUserById(friendId).getName() + " удалил вас из друзей");
        return userStorage.getUserById(id).getFriends();
    }

    public Set<User> getFriends(int id) {
        isIdExist(id);
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

    private void isIdExist(int id) {
        if (!userStorage.checkId(id)) {
            throw new UserNotFoundException("Некорректный id " + id);
        }
    }
}