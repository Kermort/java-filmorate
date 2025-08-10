package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.FilmGenreRowMapper;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class FilmGenreDbStorage implements FilmGenreStorage {
    private final NamedParameterJdbcOperations jdbc;
    private final FilmGenreRowMapper mapper;
    private final GenreRowMapper genreMapper;

    private static final String FIND_ALL_FILM_GENRES_QUERY = "SELECT * FROM FILM_GENRE;";
    private static final String INSERT_FILM_GENRE_QUERY = "INSERT INTO FILM_GENRE (film_id, genre_id) VALUES (:filmId, :genreId);";
    private static final String REMOVE_FILM_GENRE_QUERY = "DELETE FROM FILM_GENRE WHERE film_id = :filmId;";
    private static final String FIND_FILM_GENRES_BY_FILM_ID_QUERY = """
    SELECT g.genre_id, g.name
    FROM GENRE AS g INNER JOIN FILM_GENRE AS fg ON g.genre_id = fg.genre_id
    WHERE fg.film_id = :filmId
    ORDER BY g.genre_id;
    """;

    public List<FilmGenre> findAll() {
        return jdbc.query(FIND_ALL_FILM_GENRES_QUERY, mapper);
    }

    public List<Genre> findByFilmId(Long filmId) {
        return jdbc.query(FIND_FILM_GENRES_BY_FILM_ID_QUERY, Map.of("filmId", filmId), genreMapper);
    }

    public void addFilmGenre(Long filmId, Long genreId) {
        jdbc.update(INSERT_FILM_GENRE_QUERY, Map.of("filmId", filmId, "genreId", genreId));
    }

    public void removeFilmGenreByFilmId(Long filmId) {
        jdbc.update(REMOVE_FILM_GENRE_QUERY, Map.of("filmId", filmId));
    }
}