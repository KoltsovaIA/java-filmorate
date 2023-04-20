package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FilmAlreadyExistException extends RuntimeException {
    public FilmAlreadyExistException(final String message) {
        super(message);
    }
}