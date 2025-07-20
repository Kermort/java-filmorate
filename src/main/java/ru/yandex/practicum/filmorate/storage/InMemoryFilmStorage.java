package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    public Film create(Film newFilm) {
        if (newFilm.getReleaseDate() != null &&
                newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года или пустой");
        }

        newFilm.setId(getNextId());
        films.put(newFilm.getId(), newFilm);
        log.info("добавлен фильм с id = {}", newFilm.getId());
        return newFilm;
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("id не может быть пустым");
        }
        if (!films.containsKey(newFilm.getId())) {
            throw new NotFoundException("фильм с id = " + newFilm.getId() + " не найден");
        }

        films.put(newFilm.getId(), newFilm);
        log.info("обновлен фильм с id = {}", newFilm.getId());
        return newFilm;
    }

    public List<Film> findAll() {
        return films.values().stream().toList();
    }

    public Film findById(Long id) {
        if (id == null) {
            throw new ValidationException("id не может быть пустым");
        }
        if (!films.containsKey(id)) {
            throw new NotFoundException("фильм с id " + id + " не найден.");
        }
        return films.get(id);
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
