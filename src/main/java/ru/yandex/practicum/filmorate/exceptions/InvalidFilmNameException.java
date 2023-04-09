package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidFilmNameException extends RuntimeException {
    public InvalidFilmNameException(final String message) {
        super(message);
    }
}