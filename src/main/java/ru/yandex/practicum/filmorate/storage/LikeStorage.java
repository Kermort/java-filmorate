package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;

public interface LikeStorage {
    List<Like> findByFilmId(Long filmId);

    List<Film> findTop(Long count);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);
}