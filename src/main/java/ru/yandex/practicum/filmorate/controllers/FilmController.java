package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new InvalidFilmNameException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new FilmDescriptionLengthException("Описание фильма не должно превышать 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)) ||
                film.getReleaseDate() == null || film.getReleaseDate().toString().isBlank()) {
            throw new FilmDateException("Дата релиза не может быть раньше 28.12.1895");
        }
        if (film.getDuration() <= 0) {
            throw new FilmDurationException("Продолжительность фильма должна быть больше нуля");
        }
        films.forEach((key, value) -> {
            if (value.getName().equals(film.getName())) {
                throw new FilmAlreadyExistException("Фильм с названием " +
                        film.getName() + " уже существует.");
            }
        });
        films.put(films.size() + 1, film);
        log.info("Вы добавили фильм " + film.getName());
        film.setId(films.size());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            create(film);
        } else {
            if (film.getName() == null || film.getName().isBlank()) {
                throw new InvalidFilmNameException("Название фильма не может быть пустым");
            }
            if (film.getDescription().length() > 200) {
                throw new FilmDescriptionLengthException("Описание фильма не должно превышать 200 символов");
            }
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)) ||
                    film.getReleaseDate() == null || film.getReleaseDate().toString().isBlank()) {
                throw new FilmDateException("Дата релиза не может быть раньше 28.12.1895");
            }
            if (film.getDuration() <= 0) {
                throw new FilmDurationException("Продолжительность фильма должна быть больше нуля");
            }
            films.put(film.getId(), film);
            log.info("Вы обновили фильм " + film.getName());
        }
        return film;
    }

    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }
}