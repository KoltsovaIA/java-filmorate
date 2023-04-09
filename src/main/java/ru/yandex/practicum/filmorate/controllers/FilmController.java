package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @PostMapping
    public Film create(@RequestBody Film film) {
        checkFilm(film);
        films.forEach((key, value) -> {
            if (value.getName().equals(film.getName())) {
                throw new FilmAlreadyExistException("Фильм с названием " +
                        film.getName() + " уже существует.");
            }
        });
        film.setId(getNewId());
        films.put(film.getId(), Film.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .build()
        );
        log.info("Вы добавили фильм " + film.getName());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            return create(film);
        }
        checkFilm(film);
        films.put(film.getId(), Film.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .build()
        );
        log.info("Вы обновили фильм " + film.getName());
        return film;
    }

    @GetMapping
    public List<Film> getAll() {
        ArrayList<Film> filmsList = new ArrayList<>();
        for (Film value : films.values()) {
            filmsList.add(Film.builder()
                    .id(value.getId())
                    .name(value.getName())
                    .description(value.getDescription())
                    .releaseDate(value.getReleaseDate())
                    .duration(value.getDuration())
                    .build());
        }
        return filmsList;
    }

    @GetMapping(path = {"/film"})
    public Film getById(@RequestBody int id) {
        Film film = films.get(id);
        return Film.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .build();
    }

    public int getNewId() {
        return ++id;
    }

    public int getLastId() {
        return id;
    }

    private void checkFilm(Film film) {
        if (StringUtils.isBlank(film.getName())) {
            throw new InvalidFilmNameException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new FilmDescriptionLengthException("Описание фильма не должно превышать 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().toString().isBlank() ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new FilmDateException("Дата релиза не может быть раньше 28.12.1895");
        }
        if (film.getDuration() <= 0) {
            throw new FilmDurationException("Продолжительность фильма должна быть больше нуля");
        }
    }
}