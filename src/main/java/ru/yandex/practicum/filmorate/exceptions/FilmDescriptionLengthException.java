package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FilmDescriptionLengthException extends RuntimeException {
    public FilmDescriptionLengthException(final String message) {
        super(message);
        log.error(message);
    }
}