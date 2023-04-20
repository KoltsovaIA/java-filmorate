package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }


    public void addLike(int id, int filmId) {
        if ((id <= 0) || (filmId <= 0)) {
            throw new IncorrectParameterException("id не может быть меньше единицы");
        }
        Film film = filmStorage.getFilmById(filmId);
        if (film.getLikes() == null) {
            film.setLikes(new LinkedHashSet<>());
        }
        if (film.getLikes().contains(id)) {
            log.error("Один пользователь не может ставить несколько лайков одному фильму");
            throw new FilmAlreadyExistException("Один пользователь не может ставить несколько лайков одному фильму");
        }
        film.getLikes().add(id);
        filmStorage.update(film);
        log.info("Вам понравился фильм " + filmStorage.getFilmById(filmId));
    }

    public void deleteLike(int id, int filmId) {
        if (!filmStorage.getFilmById(filmId).getLikes().contains(id)) {
            log.error("Невозможно удалить лайк.");
            throw new UserNotFoundException("Вы еще не ставили лайк этому фильму.");
        }
        filmStorage.getFilmById(filmId).getLikes().remove(id);
        filmStorage.update(filmStorage.getFilmById(filmId));
        log.info("Вам больше не нравится фильм " + filmStorage.getFilmById(filmId));
    }

    public Set<Film> findMostPopularFilms(Integer count) {
        if (count < 1) {
            throw new IncorrectParameterException("Указано неверное количество фильмов для формирования списка.");
        }
        Set<Film> sortedByLikes = new LinkedHashSet<>();
        List<Film> allFilms = filmStorage.getAllFilms();
        if (allFilms.size() != 0) {
            allFilms.stream().sorted(Comparator.nullsLast(Comparator.comparingInt(o -> o.getLikes().size()))).
                    forEach(sortedByLikes::add);
            sortedByLikes = sortedByLikes.stream().skip(0).limit(count).collect(Collectors.toSet());
        }
        log.info("Сформирован список из " + count + " самых популярных фильмов");
        return sortedByLikes;
    }
}