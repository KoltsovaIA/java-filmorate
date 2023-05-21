package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class Film {
    private int id;
    private String name;
    private List<Genre> genres;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Set<Integer> likes;
    private Mpa mpa;
}