package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public void addLike(long filmId, long userId) {
        User user = userService.findById(userId); //работает как проверка наличия пользователя с заданным id и проверка на null
        filmStorage.findById(filmId).addLike(user.getId());
    }

    public void deleteLike(long filmId, long userId) {
        User user = userService.findById(userId); //работает как проверка наличия пользователя с заданным id и проверка на null
        filmStorage.findById(filmId).removeLike(user.getId());
    }

    public List<Film> findTop(long count) {
        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikesCount(), f1.getLikesCount()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film create(Film newFilm) {
        return filmStorage.create(newFilm);
    }

    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(long id) {
        return filmStorage.findById(id);
    }
}
