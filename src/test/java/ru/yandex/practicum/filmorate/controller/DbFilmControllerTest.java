package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DbFilmControllerTest {
    static final int WRONGID = 999999;
    private Film film;
    private User user;
    private final FilmController filmController;
    private final UserController userController;


    @BeforeEach
    public void beforeEach() {
        user = User.builder()
                .email("example@example.ru")
                .login("Логин")
                .name("Имя")
                .birthday(LocalDate.of(2000, 10, 15))
                .friends(new HashSet<>())
                .build();

        film = Film.builder()
                .name("Название фильма")
                // .genre(new HashSet<String>(Arrays.asList("Комедия")))
                .genres(new ArrayList<>())
                .description("Описание фильма")
                .releaseDate(LocalDate.of(2000, 11, 15))
                .duration(120)
                .likes(new HashSet<>())
                .mpa(new Mpa(1, "G"))
                .build();
    }

    @Test
    void createWithCorrectAttributesTest() {
        int filmId = filmController.create(film).getId();
        film.setId(filmId);
        Film testFilm = filmController.getFilmById(filmId);
        assertEquals(film, testFilm, "Метод create работает некорректно. Фильмы не совпадают");
        testFilm.setName("Kjd");
        assertNotEquals(film, testFilm, "Метод create работает некорректно.Фильмы совпадают");
        assertEquals(1, filmController.getAllFilms().size(),
                "Метод create работает некорректно.Неверное число фильмов");
    }

    @Test
    void createWithEmptyNameTest() {
        film.setName("");
        assertThrows(IncorrectParameterException.class, () -> filmController.create(film));
        assertEquals(0, filmController.getAllFilms().size(),
                "Метод create работает некорректно.Сохранен Фильм с пустым названием");
    }

    @Test
    void createWithTooLongDescriptionTest() {
        film.setDescription("Это очень длинное и подробное описание фильма, длинна которое значительно превышает " +
                "максимально возможную длину описания фильма. Максимальная длинна описания фильма не может превышать " +
                "Двести символов!!!!");
        assertThrows(IncorrectParameterException.class, () -> filmController.create(film),
                "Метод create работает некорректно.Сохранен Фильм с описанием более 200 символов");
        assertEquals(0, filmController.getAllFilms().size(),
                "Метод create работает некорректно.Неверное число фильмов");
    }

    @Test
    void createWithIncorrectReleaseDateTest() {
        film.setReleaseDate(LocalDate.of(1885, 12, 28));
        assertThrows(IncorrectParameterException.class, () -> filmController.create(film),
                "Метод create работает некорректно.Сохранен фильм с датой релиза до 28.12.1895");
        assertEquals(0, filmController.getAllFilms().size(),
                "Метод create работает некорректно. Неверное число фильмов");
    }

    @Test
    void createWithNullDurationTest() {
        film.setDuration(0);
        assertThrows(IncorrectParameterException.class, () -> filmController.create(film),
                "Метод create работает некорректно. Сохранен фильм с продолжительностью 0");
        assertEquals(0, filmController.getAllFilms().size(),
                "Метод create работает некорректно. Неверное число фильмов");
    }

    @Test
    void createWithNegativeDurationTest() {
        film.setDuration(-20);
        assertThrows(IncorrectParameterException.class, () -> filmController.create(film),
                "Метод create работает некорректно. Сохранен фильм с отрицательной продолжительностью");
        assertEquals(0, filmController.getAllFilms().size(),
                "Метод create работает некорректно. Неверное число фильмов");
    }

    @Test
    void updateTest() {
        int filmId = filmController.create(film).getId();
        film.setId(filmId);
        film.setDescription("Другое описание фильма");
        filmController.update(film);
        Film testFilm = filmController.getFilmById(filmId);
        assertEquals(film, testFilm, "Метод update работает некорректно. Фильмы не совпадают");
        assertEquals(1, filmController.getAllFilms().size(),
                "Метод update работает некорректно. Неверное число фильмов");
    }

    @Test
    void getAllFilms() {
        int film1Id = filmController.create(film).getId();
        Film testFilm1 = filmController.getFilmById(film1Id);
        film.setName("Другое название фильма");
        int film2Id = filmController.create(film).getId();
        Film testFilm2 = filmController.getFilmById(film2Id);
        List<Film> testFilms = filmController.getAllFilms();
        assertEquals(2, testFilms.size(),
                "Метод getAllFilms работает некорректно. Неверное число фильмов");
        assertEquals(testFilm1, testFilms.get(0), "Фильмы не совпадают");
        testFilm2.setDescription("ups");
        assertNotEquals(testFilm2, testFilms.get(1),
                "Метод getAllFilms работает некорректно. Фильмы совпадают");
    }

    @Test
    void getFilmByIdWithCorrectAttributesTest() {
        int filmId = filmController.create(film).getId();
        film.setId(filmId);
        assertEquals(film, filmController.getFilmById(filmId), "Метод getFilmById работает некорректно.");
    }

    @Test
    void getFilmByIdWithWrongIdTest() {
        assertThrows(FilmNotFoundException.class, () -> filmController.getFilmById(WRONGID),
                "Метод getFilmById работает некорректно при попытке получить фильм с неверным id");
    }

    @Test
    void getLikesWithCorrectAttributesTest() {
        int filmId = filmController.create(film).getId();
        int user1Id = userController.create(user).getId();
        filmController.addLike(filmId, user1Id);
        int user2Id = userController.create(user).getId();
        filmController.addLike(filmId, user2Id);
        assertTrue(filmController.getFilmById(filmId).getLikes().contains(1),
                "Метод getLikes работает некорректно при попытке получить список лайков");
        assertEquals(2, filmController.getFilmById(filmId).getLikes().size(),
                "Метод getLikes работает некорректно при попытке получить список лайков");
    }

    @Test
    void getLikesWithWrongIdTest() {
        filmController.create(film);
        assertThrows(FilmNotFoundException.class, () -> filmController.getFilmById(WRONGID).getLikes(),
                "Метод getLikes работает некорректно");
    }

    @Test
    void addLikeWithCorrectAttributesTest() {
        int userId = userController.create(user).getId();
        int filmId = filmController.create(film).getId();
        assertEquals(0, filmController.getFilmById(filmId).getLikes().size(),
                "Метод addLike работает некорректно при добавлении лайков с неверным id");
        filmController.addLike(filmId, userId);
        assertEquals(1, filmController.getFilmById(filmId).getLikes().size(),
                "Метод addLike работает некорректно");
        assertTrue(filmController.getFilmById(filmId).getLikes().
                        contains(userController.getUserById(userId).getId()),
                "Метод addLike работает некорректно");
    }

    @Test
    void addLikeWithWrongAttributesTest() {
        int userId = userController.create(user).getId();
        int filmId = filmController.create(film).getId();
        assertThrows(FilmNotFoundException.class, () -> filmController.addLike(WRONGID, userId),
                "Метод addLike работает некорректно при попытке передать неверный id фильма" + WRONGID);
        assertThrows(FilmNotFoundException.class, () -> filmController.addLike((WRONGID * (-1)), userId),
                "Метод addLike работает некорректно при попытке передать id фильма меньше или равно 0");
        assertThrows(UserNotFoundException.class, () -> filmController.addLike(filmId, WRONGID),
                "Метод addLike работает некорректно при попытке передать id пользователя меньше или равно 0");

        filmController.addLike(filmId, userId);
        assertThrows(FilmAlreadyExistException.class, () -> filmController.addLike(filmId, userId),
                "Метод addLike работает некорректно при попытке поставить несколько лайков одному фильму");
    }

    @Test
    void deleteLikeWithCorrectAttributesTest() {
        int filmId = filmController.create(film).getId();
        int user1Id = userController.create(user).getId();
        filmController.addLike(filmId, user1Id);
        user.setEmail("1@1.ru");
        int user2Id = userController.create(user).getId();
        filmController.addLike(filmId, user2Id);
        assertEquals(2, filmController.getFilmById(filmId).getLikes().size());
        filmController.deleteLike(filmId, user1Id);
        assertEquals(1, filmController.getFilmById(filmId).getLikes().size(),
                "Метод deleteLike работает некорректно. Размер списка лайков изменился не верно/не изменился");
        assertTrue(filmController.getFilmById(filmId).getLikes().
                        contains(userController.getUserById(user2Id).getId()),
                "Метод deleteLike работает некорректно. Удален неверный лайк");
    }

    @Test
    void deleteLikeWithWrongAttributesTest() {
        int filmId = filmController.create(film).getId();
        assertThrows(UserNotFoundException.class, () -> filmController.deleteLike(filmId, WRONGID),
                "Метод deleteLike работает некорректно при попытке поставить лайк повторно");
    }

    @Test
    void findMostPopularFilmsTest() {
        int user1Id = userController.create(user).getId();
        user.setEmail("1@1.ru");
        int user2Id = userController.create(user).getId();
        film.setName("Фильм 2");
        int film2Id = filmController.create(film).getId();
        film.setName("Фильм 3");
        int film3Id = filmController.create(film).getId();
        filmController.addLike(film2Id, user1Id);
        filmController.addLike(film3Id, user1Id);
        filmController.addLike(film3Id, user2Id);
        assertEquals(2, filmController.findMostPopularFilms(2).size(),
                "Метод findMostPopularFilms работает некорректно. Неверное кол-во самых популярных фильмов");
        assertEquals(filmController.getFilmById(film3Id), filmController.findMostPopularFilms(1)
                .stream().iterator().next(), "Метод findMostPopularFilms работает некорректно. " +
                "Неверно определен самый популярный фильм");
        assertThrows(IncorrectParameterException.class, () -> filmController.findMostPopularFilms(0),
                "Метод findMostPopularFilms работает некорректно если запрошенное кол-во популярных " +
                        "фильмов меньше или равно 0");
    }
}