package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserBirthdayException extends RuntimeException {
    public UserBirthdayException(final String message) {
        super(message);
    }
}