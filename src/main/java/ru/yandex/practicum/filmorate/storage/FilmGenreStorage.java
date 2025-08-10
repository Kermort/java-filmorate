package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmGenreStorage {
    List<FilmGenre> findAll();

    List<Genre> findByFilmId(Long filmId);

    void addFilmGenre(Long filmId, Long genreId);
}