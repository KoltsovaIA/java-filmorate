package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FilmDateException extends RuntimeException {
    public FilmDateException(final String message) {
        super(message);
    }
}