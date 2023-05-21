package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

@Slf4j
@Component
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    List<Genre> getFilmGenres(int filmId) {
        filmIdIsExist(filmId);
        List<Genre> genres = new LinkedList<>();
        SqlRowSet genreRow = jdbcTemplate.queryForRowSet("SELECT * FROM genres WHERE genre_id IN " +
                "(SELECT genre_id FROM film_genres WHERE film_id = ?)", filmId);
        while (genreRow.next()) {
            genres.add(Genre.builder()
                    .id(genreRow.getInt("genre_id"))
                    .name(genreRow.getString("genre_name"))
                    .build());
        }
        return genres;
    }

    public List<Genre> getAllGenres() {
        List<Genre> genres = new LinkedList<>();
        SqlRowSet genreRow = jdbcTemplate.queryForRowSet("SELECT * FROM genres ORDER BY genre_id");
        while (genreRow.next()) {
            genres.add(Genre.builder()
                    .id(genreRow.getInt("genre_id"))
                    .name(genreRow.getString("genre_name"))
                    .build());
        }
        return genres;
    }

    public Genre getGenreById(int id) {
        Genre genre = new Genre(null, null);
        SqlRowSet genreRow = jdbcTemplate.queryForRowSet("SELECT * FROM genres WHERE genre_id = ?", id);
        if (genreRow.next()) {
            genre.setId(genreRow.getInt("genre_id"));
            genre.setName(genreRow.getString("genre_name"));
        } else {
            throw new IdNotFoundException("id не найден");
        }
        return genre;
    }

    public void filmIdIsExist(int id) {
        boolean b = Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT * FROM films WHERE film_id = ?)", Boolean.class, id));
        if (!b) {
            log.error("Передан некорректный id " + id);
            throw new IdNotFoundException("Некорректный id " + id);
        }
    }
}