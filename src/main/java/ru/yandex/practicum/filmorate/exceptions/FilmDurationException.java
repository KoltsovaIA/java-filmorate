package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FilmDurationException extends RuntimeException {
    public FilmDurationException(final String message) {
        super(message);
        log.error(message);
    }
}