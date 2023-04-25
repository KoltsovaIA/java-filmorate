package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {
    static final int WRONGID = 999999;
    private Film film;
    private User user;

    private final InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
    private final InMemoryUserStorage userStorage = new InMemoryUserStorage();
    private final FilmService filmService = new FilmService(filmStorage, userStorage);
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

        user = User.builder()
                .email("example@example.ru")
                .login("Логин")
                .name("Имя")
                .birthday(LocalDate.of(2000, 10, 15))
                .friends(new HashSet<>())
                .build();
    }

    @Test
    void createWithCorrectAttributesTest() {
        filmController.create(film);
        Film testFilm = filmController.getFilmById(filmController.getLastId());
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
    void createWithSameNameTest() {
        filmController.create(film);
        assertThrows(FilmAlreadyExistException.class, () -> filmController.create(film),
                "Метод create работает некорректно. Сохранен фильм с существующим названием");
        assertEquals(1, filmController.getAllFilms().size(),
                "Метод create работает некорректно. Неверное число фильмов");
    }

    @Test
    void updateTest() {
        filmController.create(film);
        film.setId(filmController.getLastId());
        film.setDescription("Другое описание фильма");
        filmController.update(film);
        Film testFilm = filmController.getFilmById(filmController.getLastId());
        assertEquals(film, testFilm, "Метод update работает некорректно. Фильмы не совпадают");
        assertEquals(1, filmController.getAllFilms().size(),
                "Метод update работает некорректно. Неверное число фильмов");
    }

    @Test
    void getAllFilms() {
        filmController.create(film);
        Film testFilm1 = filmController.getFilmById(filmController.getLastId());
        film.setName("Другое название фильма");
        filmController.create(film);
        Film testFilm2 = filmController.getFilmById(filmController.getLastId());
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
        filmController.create(film);
        assertEquals(film, filmController.getFilmById(1), "Метод getFilmById работает некорректно.");
    }

    @Test
    void getFilmByIdWithWrongIdTest() {
        filmController.create(film);
        assertThrows(FilmNotFoundException.class, () -> filmController.getFilmById(WRONGID),
                "Метод getFilmById работает некорректно при попытке получить фильм с неверным id");
    }

    @Test
    void getLikesWithCorrectAttributesTest() {
        filmController.create(film);
        film.getLikes().add(10);
        film.getLikes().add(20);
        film.setId(1);
        filmController.update(film);
        assertTrue(filmController.getFilmById(1).getLikes().contains(10),
                "Метод getLikes работает некорректно при попытке получить список лайков");
        assertEquals(2, filmController.getFilmById(1).getLikes().size(),
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
        userStorage.create(user);
        filmController.create(film);
        assertEquals(0, filmController.getFilmById(1).getLikes().size(),
                "Метод addLike работает некорректно при добавлении лайков с неверным id");
        filmController.addLike(1, 1);
        assertEquals(1, filmController.getFilmById(1).getLikes().size(),
                "Метод addLike работает некорректно");
        assertTrue(filmController.getFilmById(1).getLikes().contains(userStorage.getUserById(1).getId()),
                "Метод addLike работает некорректно");
    }

    @Test
    void addLikeWithWrongAttributesTest() {
        userStorage.create(user);
        filmController.create(film);
        assertThrows(FilmNotFoundException.class, () -> filmController.addLike(WRONGID, 5),
                "Метод addLike работает некорректно при попытке передать неверный id фильма" + WRONGID);
        assertThrows(FilmNotFoundException.class, () -> filmController.addLike((WRONGID * (-1)), 5),
                "Метод addLike работает некорректно при попытке передать id фильма меньше или равно 0");
        assertThrows(UserNotFoundException.class, () -> filmController.addLike(1, 0),
                "Метод addLike работает некорректно при попытке передать id пользователя меньше или равно 0");

        filmController.addLike(1, 1);
        assertThrows(FilmAlreadyExistException.class, () -> filmController.addLike(1, 1),
                "Метод addLike работает некорректно при попытке поставить несколько лайков одному фильму");
    }

    @Test
    void deleteLikeWithCorrectAttributesTest() {
        filmController.create(film);
        userStorage.create(user);
        user.setEmail("1@1.ru");
        userStorage.create(user);
        filmController.addLike(1, 1);
        filmController.addLike(1, 2);
        assertEquals(2, filmController.getFilmById(1).getLikes().size());
        filmController.deleteLike(1, 2);
        assertEquals(1, filmController.getFilmById(1).getLikes().size(),
                "Метод deleteLike работает некорректно. Размер списка лайков изменился не верно/не изменился");
        assertTrue(filmController.getFilmById(1).getLikes().contains(userStorage.getUserById(1).getId()),
                "Метод deleteLike работает некорректно. Удален неверный лайк");
    }

    @Test
    void deleteLikeWithWrongAttributesTest() {
        filmController.create(film);
        assertThrows(UserNotFoundException.class, () -> filmController.deleteLike(1, 100),
                "Метод deleteLike работает некорректно при попытке поставить лайк повторно");

    }

    @Test
    void findMostPopularFilmsTest() {
        userStorage.create(user);
        user.setEmail("1@1.ru");
        userStorage.create(user);

        filmController.create(film);
        film.setName("Фильм 2");
        filmController.create(film);
        film.setName("Фильм 3");
        filmController.create(film);

        filmController.addLike(2, 1);
        filmController.addLike(3, 1);
        filmController.addLike(3, 2);

        assertEquals(2, filmController.findMostPopularFilms(2).size(),
                "Метод findMostPopularFilms работает некорректно. Неверное кол-во самых популярных фильмов");
        assertEquals(filmController.getFilmById(3), filmController.findMostPopularFilms(1)
                .stream().iterator().next(), "Метод findMostPopularFilms работает некорректно. " +
                "Неверно определен самый популярный фильм");
        assertThrows(IncorrectParameterException.class, () -> filmController.findMostPopularFilms(0),
                "Метод findMostPopularFilms работает некорректно если запрошенное кол-во популярных " +
                        "фильмов меньше или равно 0");
    }
}