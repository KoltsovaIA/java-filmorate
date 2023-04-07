package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private Film film;
    FilmController filmController = new FilmController();

    @BeforeEach
    public void beforeEach() {
        film = Film.builder()
                .name("Название фильма")
                .description(" Описание фильма")
                .releaseDate(LocalDate.of(2000, 10, 28))
                .duration(60)
                .build();
    }

    @Test
    void createWithCorrectAttributesTest() {
        Film film1 = filmController.create(film);
        assertEquals(film, film1, "Фильмы совпадают");
        assertEquals(1, filmController.getAll().size(), "Добавление фильма прошло успешно");
    }

    @Test
    void createWithEmptyNameTest() {
        film.setName("");
        assertThrows(InvalidFilmNameException.class, () -> filmController.create(film));
        assertEquals(0, filmController.getAll().size(), "Фильм с пустым названием не сохранен");
    }

    @Test
    void createWithTooLongDescriptionTest() {
        film.setDescription("Это очень длинное и подробное описание фильма, длинна которое значительно превышает " +
                "максимально возможную длину описания фильма. Максимальная длинна описания фильма не может превышать " +
                "Двести символов!!!!");
        assertThrows(FilmDescriptionLengthException.class, () -> filmController.create(film),
                "Попытка создать фильм описанием более 200 символов");
        assertEquals(0, filmController.getAll().size(),
                "Фильм с описанием более 200 символов не сохранен");
    }

    @Test
    void createWithIncorrectReleaseDateTest() {
        film.setReleaseDate(LocalDate.of(1885, 12, 28));
        assertThrows(FilmDateException.class, () -> filmController.create(film),
                "Попытка создать фильм c датой релиза до 28.12.1895");
        assertEquals(0, filmController.getAll().size(),
                "Фильм с датой релиза до 28.12.1895 не сохранен");
    }

    @Test
    void createWithNullDurationTest() {
        film.setDuration(0);
        assertThrows(FilmDurationException.class, () -> filmController.create(film),
                "Попытка создать фильм c продолжительностью 0");
        assertEquals(0, filmController.getAll().size(),
                "Фильм с продолжительностью 0 не сохранен");
    }

    @Test
    void createWithNegativeDurationTest() {
        film.setDuration(-20);
        assertThrows(FilmDurationException.class, () -> filmController.create(film),
                "Попытка создать фильм c отрицательной продолжительностью");
        assertEquals(0, filmController.getAll().size(),
                "Фильм с отрицательной продолжительностью не сохранен");
    }

    @Test
    void createWithSameNameTest() {
        filmController.update(film);
        film.setName("Название фильма");
        assertThrows(FilmAlreadyExistException.class, () -> filmController.create(film),
                "Попытка создать фильм c существующим названием");
        assertEquals(1, filmController.getAll().size(),
                "Фильм с существующим названием не сохранен");
    }

    @Test
    void updateTest() {
        filmController.create(film);
        Film film1 = Film.builder()
                .id(1)
                .name("Название фильма")
                .description("Другое описание фильма")
                .releaseDate(LocalDate.of(2000, 10, 28))
                .duration(60)
                .build();
        filmController.update(film1);
        assertEquals(1, filmController.getAll().size(), "Фильм успешно обновлен");
    }

    @Test
    void getAllTest() {
        filmController.create(film);
        Film film1 = Film.builder()
                .name("Другое название фильма")
                .description(" Описание фильма")
                .releaseDate(LocalDate.of(2000, 12, 10))
                .duration(60)
                .build();
        filmController.create(film1);
        assertEquals(2, filmController.getAll().size(), "Пользователи возвращаются не корректно");
    }
}