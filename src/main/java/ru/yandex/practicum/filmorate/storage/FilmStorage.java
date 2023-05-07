package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface FilmStorage {
    List<Film> getAllFilms();

    Film create(Film film);

    Film update(Film film);

    Film getFilmById(int id);

    int getLastId();

    Set<Integer> getLikes(int id);

    void filmIdIsExist(int id);
}