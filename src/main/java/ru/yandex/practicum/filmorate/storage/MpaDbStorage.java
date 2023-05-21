package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Component
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Mpa> getAllMpa(){
        List<Mpa> allMpa = new LinkedList<>();
        SqlRowSet mpaRow = jdbcTemplate.queryForRowSet("SELECT * FROM mpa ORDER BY mpa_id");
        while(mpaRow.next()){
           Mpa mpa = Mpa.builder()
                    .id(mpaRow.getInt("mpa_id"))
                    .name(mpaRow.getString("mpa_name"))
                    .build();
            allMpa.add(mpa);
        }
        return allMpa;
    }

    public Mpa getMpaById(int id){
        mpaIdIsExist(id);
        Mpa mpa = new Mpa(null, null);
        SqlRowSet mpaRow = jdbcTemplate.queryForRowSet("SELECT * FROM mpa WHERE mpa_id = ?", id);
        if (mpaRow.next()){
            mpa.setId(mpaRow.getInt("mpa_id"));
            mpa.setName(mpaRow.getString("mpa_name"));
        } else {
            throw new IncorrectParameterException("id не найден");
        }
        return mpa;
    }

    public void mpaIdIsExist(int id) {
        boolean b = Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT * FROM mpa WHERE mpa_id = ?)", Boolean.class, id));
        if (!b) {
            log.error("Передан некорректный id " + id);
            throw new IdNotFoundException("Некорректный id " + id);
        }
    }
}