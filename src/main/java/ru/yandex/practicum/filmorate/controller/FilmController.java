package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public ResponseEntity<Film> create(@NotNull @Valid @RequestBody Film newFilm) {
        if (newFilm.getReleaseDate() != null &&
            newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза не может быть раньше 28 декабря 1895 года или пустой");
            return ResponseEntity.badRequest().body(newFilm);
        }

        newFilm.setId(getNextId());
        films.put(newFilm.getId(), newFilm);
        log.info("добавлен фильм с id = {}", newFilm.getId());
        return ResponseEntity.ok(newFilm);
    }

    @PutMapping
    public ResponseEntity<Film> update(@NotNull @Valid @RequestBody Film newFilm) {
        if (!films.containsKey(newFilm.getId())) {
            log.error("фильм с id = " + newFilm.getId() + " не найден");
            return ResponseEntity.status(404).body(newFilm);
        }

        films.put(newFilm.getId(), newFilm);
        log.info("обновлен фильм с id = {}", newFilm.getId());
        return ResponseEntity.ok(newFilm);
    }

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}