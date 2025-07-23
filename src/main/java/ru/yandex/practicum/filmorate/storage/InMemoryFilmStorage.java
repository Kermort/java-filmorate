package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    public Film create(Film newFilm) {
        newFilm.setId(getNextId());
        films.put(newFilm.getId(), newFilm);
        log.info("добавлен фильм с id = {}", newFilm.getId());
        return newFilm;
    }

    public Film update(Film newFilm) {
        films.put(newFilm.getId(), newFilm);
        log.info("обновлен фильм с id = {}", newFilm.getId());
        return newFilm;
    }

    public List<Film> findAll() {
        return films.values().stream().toList();
    }

    public Optional<Film> findById(Long id) {
        return Optional.ofNullable(films.get(id));
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
