package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserLoginException extends RuntimeException {
    public UserLoginException(final String message) {
        super(message);
        log.error(message);
    }
}