package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreControllerTest {
    static final int WRONGID = 999999;
    private final GenresController genresController;

    @Test
    void getAllGenresTest() {
        List<Genre> genres = new LinkedList<>(genresController.getAllGenres());
        assertEquals(6, genres.size(),
                "Метод getAllGenres работает некорректно");
        assertEquals(new Genre(1, "Комедия"), genres.get(0),
                "Метод getAllGenres работает некорректно");
        assertEquals(new Genre(2, "Драма"), genres.get(1),
                "Метод getAllGenres работает некорректно");
        assertEquals(new Genre(3, "Мультфильм"), genres.get(2),
                "Метод getAllGenres работает некорректно");
        assertEquals(new Genre(4, "Триллер"), genres.get(3),
                "Метод getAllGenres работает некорректно");
        assertEquals(new Genre(5, "Документальный"), genres.get(4),
                "Метод getAllGenres работает некорректно");
        assertEquals(new Genre(6, "Боевик"), genres.get(5),
                "Метод getAllGenres работает некорректно");
    }

    @Test
    void getGenreByIdTest() {
        assertEquals(new Genre(1, "Комедия"), genresController.getGenreById(1),
                "Метод findGenreById работает некорректно");
        assertThrows(IdNotFoundException.class, () -> genresController.getGenreById(WRONGID),
                "Метод findGenreById работает некорректно при неверном id");
    }
}