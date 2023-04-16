package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(final String message) {
        super(message);
    }
}