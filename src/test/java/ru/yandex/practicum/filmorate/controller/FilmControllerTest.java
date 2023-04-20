package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private Film film;

    private final InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
    private final FilmService filmService = new FilmService(filmStorage);
    private final FilmController filmController = new FilmController(filmStorage, filmService);

    @BeforeEach
    public void beforeEach() {
        film = Film.builder()
                .name("Название фильма")
                .description(" Описание фильма")
                .releaseDate(LocalDate.of(2000, 10, 28))
                .duration(60)
                .likes(new HashSet<>())
                .build();
    }

    @Test
    void createWithCorrectAttributesTest() {
        filmController.create(film);
        Film testFilm = filmController.getById(filmController.getLastId());
        assertEquals(film, testFilm, "Фильмы не совпадают");
        testFilm.setName("Kjd");
        assertNotEquals(film, testFilm, "Фильмы совпадают");
        assertEquals(1, filmController.getAllFilms().size(), "Неверное число фильмов");
    }

    @Test
    void createWithEmptyNameTest() {
        film.setName("");
        assertThrows(IncorrectParameterException.class, () -> filmController.create(film));
        assertEquals(0, filmController.getAllFilms().size(), "Сохранен Фильм с пустым названием");
    }

    @Test
    void createWithTooLongDescriptionTest() {
        film.setDescription("Это очень длинное и подробное описание фильма, длинна которое значительно превышает " +
                "максимально возможную длину описания фильма. Максимальная длинна описания фильма не может превышать " +
                "Двести символов!!!!");
        assertThrows(IncorrectParameterException.class, () -> filmController.create(film),
                "Сохранен Фильм с описанием более 200 символов");
        assertEquals(0, filmController.getAllFilms().size(),
                "Неверное число фильмов");
    }

    @Test
    void createWithIncorrectReleaseDateTest() {
        film.setReleaseDate(LocalDate.of(1885, 12, 28));
        assertThrows(IncorrectParameterException.class, () -> filmController.create(film),
                "Сохранен фильм с датой релиза до 28.12.1895");
        assertEquals(0, filmController.getAllFilms().size(),
                "Неверное число фильмов");
    }

    @Test
    void createWithNullDurationTest() {
        film.setDuration(0);
        assertThrows(IncorrectParameterException.class, () -> filmController.create(film),
                "Сохранен фильм с продолжительностью 0");
        assertEquals(0, filmController.getAllFilms().size(),
                "Неверное число фильмов");
    }

    @Test
    void createWithNegativeDurationTest() {
        film.setDuration(-20);
        assertThrows(IncorrectParameterException.class, () -> filmController.create(film),
                "Сохранен фильм с отрицательной продолжительностью");
        assertEquals(0, filmController.getAllFilms().size(),
                "Неверное число фильмов");
    }

    @Test
    void createWithSameNameTest() {
        filmController.update(film);
        assertThrows(FilmAlreadyExistException.class, () -> filmController.create(film),
                "Сохранен фильм с существующим названием");
        assertEquals(1, filmController.getAllFilms().size(),
                "Неверное число фильмов");
    }

    @Test
    void updateTest() {
        filmController.create(film);
        film.setId(filmController.getLastId());
        film.setDescription("Другое описание фильма");
        filmController.update(film);
        Film testFilm = filmController.getById(filmController.getLastId());
        assertEquals(film, testFilm, "Фильмы не совпадают");
        assertEquals(1, filmController.getAllFilms().size(), "Неверное число фильмов");
    }

    @Test
    void getAllTest() {
        filmController.create(film);
        Film testFilm1 = filmController.getById(filmController.getLastId());
        film.setName("Другое название фильма");
        filmController.create(film);
        Film testFilm2 = filmController.getById(filmController.getLastId());
        List<Film> testFilms = filmController.getAllFilms();

        assertEquals(2, testFilms.size(), "Неверное число фильмов");
        assertEquals(testFilm1, testFilms.get(0), "Фильмы не совпадают");
        testFilm2.setDescription("ups");
        assertNotEquals(testFilm2, testFilms.get(1), "Фильмы совпадают");
    }
}