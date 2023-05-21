package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Builder
public class Genre {
    Integer id;
    String name;

    public Genre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}