package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public void addLike(long filmId, long userId) {
        User user = userService.findById(userId);
        Film film = findById(filmId);
        film.addLike(user.getId());
    }

    public void deleteLike(long filmId, long userId) {
        User user = userService.findById(userId);
        Film film = findById(filmId);
        film.removeLike(user.getId());
    }

    public List<Film> findTop(long count) {
        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikesCount(), f1.getLikesCount()))
                .limit(count)
                .toList();
    }

    public Film create(Film newFilm) {
        if (newFilm.getReleaseDate() != null &&
                newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года или пустой");
        }
        return filmStorage.create(newFilm);
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("id не может быть пустым");
        }

        Optional<Film> filmOpt = filmStorage.findById(newFilm.getId());
        if (filmOpt.isEmpty()) {
            throw new NotFoundException("фильм с id = " + newFilm.getId() + " не найден");
        }
        return filmStorage.update(newFilm);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Long id) {
        if (id == null) {
            throw new ValidationException("id не может быть пустым");
        }

        Optional<Film> filmOpt = filmStorage.findById(id);
        if (filmOpt.isEmpty()) {
            throw new NotFoundException("фильм с id " + id + " не найден.");
        }
        return filmOpt.get();
    }
}
