package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class Film {
    private int id;
    private String name;
    private List<Genre> genres = new LinkedList<>();
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Set<Integer> likes = new LinkedHashSet<>();
    private Mpa mpa;
}