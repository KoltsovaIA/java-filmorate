package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;

        this.userStorage = userStorage;
    }

    public void addLike(int id, int userId) {
        filmStorage.filmIdIsExist(id);
        userStorage.userIdIsExist(userId);
        Film film = filmStorage.getFilmById(id);
        if (film.getLikes().contains(userId)) {
            log.error("Один пользователь не может ставить несколько лайков одному фильму");
            throw new FilmAlreadyExistException("Один пользователь не может ставить несколько лайков одному фильму");
        }
        film.getLikes().add(userId);
        filmStorage.update(film);
        log.info("Пользователь " + userStorage.getUserById(userId) + " поставил лайк фильму "
                + filmStorage.getFilmById(id));
    }

    public void deleteLike(int id, int userId) {
        filmStorage.filmIdIsExist(id);
        userStorage.userIdIsExist(userId);
        if (!filmStorage.getFilmById(id).getLikes().contains(userId)) {
            log.error("Невозможно удалить лайк.");
            throw new UserNotFoundException("Вы еще не ставили лайк этому фильму.");
        }
        filmStorage.getFilmById(id).getLikes().remove(userId);
        filmStorage.update(filmStorage.getFilmById(id));
        log.info("Пользователь " + userStorage.getUserById(userId) + " удалил лайк фильму "
                + filmStorage.getFilmById(id));
    }

    public Set<Film> findMostPopularFilms(Integer count) {
        if (count < 1) {
            throw new IncorrectParameterException("Указано неверное количество фильмов для формирования списка.");
        }
        Set<Film> sortedByLikes = new LinkedHashSet<>();
        List<Film> allFilms = filmStorage.getAllFilms();
        log.info(allFilms.toString());
        if (allFilms.size() != 0) {
            Comparator<Film> comparator = Comparator.comparingInt((Film film) -> film.getLikes().size());
            sortedByLikes = filmStorage.getAllFilms().stream().sorted(comparator.reversed()).limit(count)
                    .collect(Collectors.toSet());
        }
        log.info("Сформирован список из " + count + " самых популярных фильмов" + sortedByLikes);
        return sortedByLikes;
    }
}