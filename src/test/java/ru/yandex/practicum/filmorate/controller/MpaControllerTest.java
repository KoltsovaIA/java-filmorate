package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaControllerTest {
    static final int WRONGID = 999999;
    private final MpaController mpaController;

    @Test
    void getAllMpaTest() {
        System.out.println(mpaController.getAllMpa());
        List<Mpa> allMpa = new LinkedList<>(mpaController.getAllMpa());
        assertEquals(5, allMpa.size(),
                "Метод getAllMpa работает некорректно");
        assertEquals(new Mpa(1, "G"), allMpa.get(0),
                "Метод getAllMpa работает некорректно");
        assertEquals(new Mpa(2, "PG"), allMpa.get(1),
                "Метод getAllMpa работает некорректно");
        assertEquals(new Mpa(3, "PG-13"), allMpa.get(2),
                "Метод getAllMpa работает некорректно");
        assertEquals(new Mpa(4, "R"), allMpa.get(3),
                "Метод getAllMpa работает некорректно");
        assertEquals(new Mpa(5, "NC-17"), allMpa.get(4),
                "Метод getAllMpa работает некорректно");
    }

    @Test
    void getMpaByIdTest() {
        assertEquals(new Mpa(1, "G"), mpaController.getMpaById(1),
                "Метод getMpaById работает некорректно");
        assertThrows(IdNotFoundException.class, () -> mpaController.getMpaById(WRONGID),
                "Метод getMpaById работает некорректно при неверном id");
    }
}