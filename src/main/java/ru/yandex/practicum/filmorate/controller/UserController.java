package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage;
    @Autowired
    private final UserService userService;

    public UserController(@Qualifier("userDbStorage") UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return userStorage.create(user);
    }

    @PutMapping
    public User update(@RequestBody User user) {
        return userStorage.update(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @GetMapping({"/{id}"})
    public User getUserById(@PathVariable Integer id) {
        return userStorage.getUserById(id);
    }

    public int getLastId() {
        return userStorage.getLastId();
    }

    @PutMapping({"{id}/friends/{friendId}"})
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping({"/{id}/friends/{friendId}"})
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping({"{id}/friends"})
    public Set<User> getFriends(@PathVariable Integer id) {
        return userService.getFriends(id);
    }

    @GetMapping({"{id}/friends/common/{otherId}"})
    public Set<User> findCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.findCommonFriends(id, otherId);
    }
}