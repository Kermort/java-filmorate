package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.mappers.LikeRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;
import java.util.Map;

@Repository
public class LikeDbStorage implements LikeStorage {
    private final NamedParameterJdbcOperations jdbc;
    private final LikeRowMapper mapper;
    private final FilmRowMapper filmMapper;

    private static final String FIND_LIKES_BY_FILM_ID_QUERY = "SELECT * FROM FILM_LIKES WHERE film_id = :filmId;";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO FILM_LIKES (film_id, user_id) VALUES (:filmId, :userId);";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM FILM_LIKES WHERE film_id = :filmId AND user_id = :userId;";

    private static final String FIND_TOP_FILMS_QUERY = """
    SELECT l.film_id, f.NAME, f.DESCRIPTION, f.MPA_ID, f.RELEASE_DATE, f.DURATION
    FROM FILM_LIKES AS l INNER JOIN FILM AS f ON l.film_id = f.film_id
    GROUP BY l.film_id
    ORDER BY COUNT(l.film_id) DESC
    LIMIT :count
    """;

    @Autowired
    public LikeDbStorage(NamedParameterJdbcOperations jdbc,
                         LikeRowMapper mapper,
                         FilmRowMapper filmMapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
        this.filmMapper = filmMapper;
    }

    @Override
    public List<Like> findByFilmId(Long filmId) {
        return jdbc.query(FIND_LIKES_BY_FILM_ID_QUERY, Map.of("filmId", filmId), mapper);
    }

    @Override
    public List<Film> findTop(Long count) {
        return jdbc.query(FIND_TOP_FILMS_QUERY, Map.of("count", count), filmMapper);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        jdbc.update(INSERT_LIKE_QUERY, Map.of("filmId", filmId, "userId", userId));
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        jdbc.update(REMOVE_LIKE_QUERY, Map.of("filmId", filmId, "userId", userId));
    }
}