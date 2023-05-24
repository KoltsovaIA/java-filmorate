package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;

    @Override
    public List<Film> getAllFilms() {
        LinkedList<Film> films = new LinkedList<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM films ORDER BY film_id");
        while (filmRows.next()) {
            int filmId = filmRows.getInt("film_id");
            Film film = Film.builder()
                    .id(filmRows.getInt("film_id"))
                    .name(filmRows.getString("film_name"))
                    .genres(genreDbStorage.getFilmGenres(filmId))
                    .description(filmRows.getString("description"))
                    .releaseDate(Objects.requireNonNull(filmRows.getDate("release_date")).toLocalDate())
                    .duration(filmRows.getInt("duration"))
                    .likes(getLikes(filmId))
                    .mpa(mpaDbStorage.getMpaById(filmRows.getInt("mpa_id")))
                    .build();
            films.add(film);
        }
        return films;
    }

    @Override
    public Film create(Film film) {
        checkFilm(film);
        jdbcTemplate.update(
                "INSERT INTO films (film_name, description, release_date, duration, mpa_id)" +
                        " VALUES (?, ?, ?, ?, ?)",
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        if (!film.getGenres().isEmpty()) {
            LinkedHashSet<Genre> genres = new LinkedHashSet<>(film.getGenres());
            for (Genre genre : genres) {
                jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) values (?, ?)",
                        getLastId(), genre.getId());
            }
        }
        return getFilmById(getLastId());
    }

    @Override
    public Film update(Film film) {
        checkFilm(film);
        int filmId = film.getId();
        filmIdIsExist(filmId);
        jdbcTemplate.update("UPDATE films SET film_name = ?, description = ?, release_date = ?," +
                        "duration = ?, mpa_id = ? WHERE film_id = ?", film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), filmId);
        if (film.getLikes() != null) {
            jdbcTemplate.update("DELETE FROM likes WHERE film_id = ?;", filmId);
            for (int userId : film.getLikes()) {
                jdbcTemplate.update("INSERT INTO likes (film_id, user_id ) values (?, ?)", filmId, userId);
            }
        }
        if (film.getGenres() != null) {
            jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?;", filmId);
            LinkedHashSet<Genre> genres = new LinkedHashSet<>(film.getGenres());
            for (Genre genre : genres) {
                jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) values (?, ?)",
                        filmId, genre.getId());
            }
        }
        return getFilmById(filmId);
    }


    @Override
    public Film getFilmById(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM films WHERE film_id = ?", id);
        if (filmRows.next()) {
            return Film.builder()
                    .id(filmRows.getInt("film_id"))
                    .name(filmRows.getString("film_name"))
                    .genres(genreDbStorage.getFilmGenres(id))
                    .description(filmRows.getString("description"))
                    .releaseDate(Objects.requireNonNull(filmRows.getDate("release_date")).toLocalDate())
                    .duration(filmRows.getInt("duration"))
                    .likes(getLikes(id))
                    .mpa(mpaDbStorage.getMpaById(filmRows.getInt("mpa_id")))
                    .build();
        } else {
            log.error("Фильм с идентификатором {} не найден.", id);
            throw new FilmNotFoundException("Фильм с id " + id + " не найден.");
        }
    }

    @Override
    public int getLastId() {
        SqlRowSet filmRow = jdbcTemplate.queryForRowSet("SELECT max(film_id) FROM films");
        if (filmRow.next()) {
            return filmRow.getInt(1);
        }
        return 0;
    }

    @Override
    public Set<Integer> getLikes(int id) {
        Set<Integer> likes = new HashSet<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM likes WHERE film_id = ?", id);
        while (filmRows.next()) {
            likes.add(filmRows.getInt("user_id"));
        }
        return likes;
    }

    @Override
    public void filmIdIsExist(int id) {
        boolean b = Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT FROM films WHERE film_id = ?)", Boolean.class, id));
        if (!b) {
            log.error("Передан некорректный id " + id);
            throw new FilmNotFoundException("Некорректный id " + id);
        }
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
        if (film.getLikes() == null) {
            film.setLikes(new LinkedHashSet<>());
        }
        if (film.getGenres() == null) {
            film.setGenres(new LinkedList<>());
        }
    }
}