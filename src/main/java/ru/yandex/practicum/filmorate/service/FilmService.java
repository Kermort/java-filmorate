package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final UserService userService;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final LikeStorage likeStorage;
    private final FilmGenreStorage filmGenreStorage;

    @Autowired
    public FilmService(UserService userService,
                       FilmStorage filmStorage,
                       UserStorage userStorage,
                       FilmGenreStorage filmGenreStorage,
                       MpaStorage mpaStorage,
                       GenreStorage genreStorage,
                       LikeStorage likeStorage) {
        this.userService = userService;
        this.filmStorage = filmStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.likeStorage = likeStorage;
        this.userStorage = userStorage;
        this.filmGenreStorage = filmGenreStorage;
    }

    public void addLike(long filmId, long userId) {
        Optional<Film> filmOpt = filmStorage.findById(filmId);
        Optional<User> userOpt = userStorage.findById(userId);

        if (filmOpt.isEmpty() || userOpt.isEmpty()) {
            throw new NotFoundException("пользователь или фильм не найден");
        }

        likeStorage.addLike(filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        User user = userService.findById(userId);
        Film film = findById(filmId);
        film.removeLike(user.getId());
    }

    public List<Film> findTop(long count) {
        return likeStorage.findTop(count);
    }

    public Film create(Film newFilm) {
        if (newFilm.getReleaseDate() != null &&
                newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года или пустой");
        }

        Optional<Mpa> mpaOpt = mpaStorage.findById(newFilm.getMpa().getId());
        if (mpaOpt.isEmpty()) {
            throw new NotFoundException("рейтинг с id " + newFilm.getMpa().getId() + " не существует");
        }

        List<Long> genreIds = newFilm.getGenres().stream().map(Genre::getId).toList();
        List<Genre> genres = genreStorage.findByIds(genreIds);
        if (genreIds.size() > genres.size()) {
            throw new NotFoundException("один или несколько жанров не существует");
        }

        Film createdFilm = filmStorage.create(newFilm);
        genreIds.forEach(gi -> filmGenreStorage.addFilmGenre(createdFilm.getId(), gi));
        return createdFilm;
    }

    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("id не может быть пустым");
        }

        Optional<Film> filmOpt = filmStorage.findById(newFilm.getId());
        if (filmOpt.isEmpty()) {
            throw new NotFoundException("фильм с id = " + newFilm.getId() + " не найден");
        }

        Optional<Mpa> mpaOpt = mpaStorage.findById(newFilm.getMpa().getId());
        if (mpaOpt.isEmpty()) {
            throw new NotFoundException("рейтинг с id " + newFilm.getMpa().getId() + " не найден");
        }

        if (newFilm.getName() == null) {
            newFilm.setName(filmOpt.get().getName());
        }

        if (newFilm.getDescription() == null) {
            newFilm.setDescription(filmOpt.get().getDescription());
        }

        if (newFilm.getMpa() == null) {
            newFilm.setMpa(filmOpt.get().getMpa());
        }

        if (newFilm.getReleaseDate() == null) {
            newFilm.setReleaseDate(filmOpt.get().getReleaseDate());
        }

        if (newFilm.getGenres() != null) {
            filmGenreStorage.removeFilmGenreByFilmId(newFilm.getId());
            newFilm.getGenres().forEach(fg -> filmGenreStorage.addFilmGenre(newFilm.getId(), fg.getId()));
        }

        return filmStorage.update(newFilm);
    }

    public List<Film> findAll() {
        List<Film> films = filmStorage.findAll();
        List<Genre> genres = genreStorage.findAll();
        Map<Long, Genre> genreMap = new HashMap<>();
        genres.forEach(g -> genreMap.put(g.getId(), g));
        List<FilmGenre> filmGenres = filmGenreStorage.findAll();

        Map<Long, Set<Genre>> filmGenresMap = new HashMap<>();
        filmGenres.forEach((fg) -> {
            filmGenresMap.putIfAbsent(fg.getFilmId(), new HashSet<>());
            filmGenresMap.get(fg.getFilmId()).add(genreMap.get(fg.getGenreId()));
        });
        films.forEach(f -> {
            if (filmGenresMap.get(f.getId()) != null) {
                f.setGenres(filmGenresMap.get(f.getId()));
            }
        });

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

        Optional<Mpa> mpaOpt = mpaStorage.findById(filmOpt.get().getMpa().getId());
        if (mpaOpt.isPresent()) {
            filmOpt.get().setMpa(mpaOpt.get());
        }

        List<Genre> filmGenres = filmGenreStorage.findByFilmId(filmOpt.get().getId());
        Set<Genre> s = new LinkedHashSet<>(filmGenres); //в тесте postman проверяет, чтобы жанры были на определенных позициях
        filmOpt.get().setGenres(s);

        return filmOpt.get();
    }
}