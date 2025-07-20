package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film create(Film newFilm);

    Film update(Film newFilm);

    List<Film> findAll();

    Film findById(Long id);
}
