package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;


@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    public Film create(Film film) {
        checkFilm(film);
        films.forEach((key, value) -> {
            if (value.getName().equals(film.getName())) {
                throw new FilmAlreadyExistException("Фильм с названием " +
                        film.getName() + " уже существует.");
            }
        });
        film.setId(getNewId());
        film.setLikes(new LinkedHashSet<>());
        films.put(film.getId(), Film.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .likes(film.getLikes())
                .build()
        );
        log.info("Вы добавили фильм " + film.getName());
        return film;
    }

    public Film update(Film film) {
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
                .likes(film.getLikes())
                .build()
        );
        log.info("Вы обновили фильм " + film.getName());
        return film;
    }

    public List<Film> getAllFilms() {
        ArrayList<Film> filmsList = new ArrayList<>();
        for (Film value : films.values()) {
            filmsList.add(Film.builder()
                    .id(value.getId())
                    .name(value.getName())
                    .description(value.getDescription())
                    .releaseDate(value.getReleaseDate())
                    .duration(value.getDuration())
                    .likes(value.getLikes())
                    .build());
        }
        return filmsList;
    }

    public Film getFilmById(int id) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Фильм c id " + id + " не найден.");
        }
        Film film = films.get(id);
        return Film.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .likes(film.getLikes())
                .build();
    }

    public Set<Integer> getLikes(int id) {
        return films.get(id).getLikes();
    }

    public int getNewId() {
        return ++id;
    }

    public int getLastId() {
        return id;
    }

    private void checkFilm(Film film) {
        if (StringUtils.isBlank(film.getName())) {
            throw new IncorrectParameterException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new IncorrectParameterException("Описание фильма не должно превышать 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().toString().isBlank() ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new IncorrectParameterException("Дата релиза не может быть раньше 28.12.1895");
        }
        if (film.getDuration() <= 0) {
            throw new IncorrectParameterException("Продолжительность фильма должна быть больше нуля");
        }
    }
}