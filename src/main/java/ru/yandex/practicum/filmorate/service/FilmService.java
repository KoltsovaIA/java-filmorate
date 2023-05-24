package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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

    private final FilmStorage filmStorage;
    private final UserService userService;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public void addLike(int id, int userId) {
        filmStorage.filmIdIsExist(id);
        userService.userIdIsExist(userId);
        Film film = filmStorage.getFilmById(id);
        if (film.getLikes().contains(userId)) {
            log.error("Один пользователь не может ставить несколько лайков одному фильму");
            throw new FilmAlreadyExistException("Один пользователь не может ставить несколько лайков одному фильму");
        }
        film.getLikes().add(userId);
        filmStorage.update(film);
        log.info("Пользователь " + userService.getUserById(userId) + " поставил лайк фильму "
                + filmStorage.getFilmById(id));
    }

    public void deleteLike(int id, int userId) {
        filmStorage.filmIdIsExist(id);
        userService.userIdIsExist(userId);
        if (!filmStorage.getFilmById(id).getLikes().contains(userId)) {
            log.error("Невозможно удалить лайк.");
            throw new UserNotFoundException("Вы еще не ставили лайк этому фильму.");
        }
        Film film = filmStorage.getFilmById(id);
        film.getLikes().remove(userId);
        filmStorage.update(film);
        log.info("Пользователь " + userService.getUserById(userId) + " удалил лайк фильму "
                + filmStorage.getFilmById(id));
    }

    public Set<Film> findMostPopularFilms(Integer count) {
        if (count < 1) {
            throw new IncorrectParameterException("Указано неверное количество фильмов для формирования списка.");
        }
        Set<Film> sortedByLikes = new LinkedHashSet<>();
        List<Film> allFilms = filmStorage.getAllFilms();
        if (allFilms.size() != 0) {
            Comparator<Film> comparator = Comparator.comparingInt((Film film) -> film.getLikes().size());
            sortedByLikes = filmStorage.getAllFilms().stream().sorted(comparator.reversed()).limit(count)
                    .collect(Collectors.toSet());
        }
        log.info("Сформирован список из " + count + " самых популярных фильмов" + sortedByLikes);
        return sortedByLikes;
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public int getLastId() {
        return filmStorage.getLastId();
    }
}