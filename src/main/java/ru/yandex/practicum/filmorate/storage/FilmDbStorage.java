package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final NamedParameterJdbcOperations jdbc;
    private final FilmRowMapper mapper;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String UPDATE_FILM_QUERY = "UPDATE FILM SET " +
            "NAME = :name, DESCRIPTION = :description, MPA_ID = :mpaId, RELEASE_DATE = :releaseDate, DURATION = :duration WHERE film_id = :filmId";

    private static final String INSERT_INTO_FILM = "INSERT INTO FILM " +
            "(NAME, DURATION, DESCRIPTION, RELEASE_DATE, MPA_ID) VALUES (:name, :duration, :description, :releaseDate, :mpaId);";

    private static final String FIND_FILM_BY_ID_QUERY = "SELECT * FROM FILM WHERE film_id = :filmId;";
    private static final String FIND_ALL_FILM_QUERY = "SELECT * FROM FILM;";

    @Autowired
    public FilmDbStorage(NamedParameterJdbcOperations jdbc, FilmRowMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public Film create(Film newFilm) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource sps = new MapSqlParameterSource(Map.of(
                "name", newFilm.getName(),
                "duration", newFilm.getDuration(),
                "description", newFilm.getDescription(),
                "mpaId", newFilm.getMpa().getId(),
                "releaseDate", newFilm.getReleaseDate().format(DATE_FORMAT)));
        jdbc.update(INSERT_INTO_FILM, sps, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);
        if (id != null) {
            newFilm.setId(Long.valueOf(id));
            return newFilm;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    @Override
    public Film update(Film newFilm) {
        int rowsUpdated = jdbc.update(UPDATE_FILM_QUERY, Map.of(
                "name", newFilm.getName(),
                "description", newFilm.getDescription(),
                "mpaId", newFilm.getMpa().getId(),
                "releaseDate", newFilm.getReleaseDate().format(DATE_FORMAT),
                "duration", newFilm.getDuration(),
                "filmId", newFilm.getId()
        ));
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
        return newFilm;
    }

    @Override
    public List<Film> findAll() {
        return jdbc.query(FIND_ALL_FILM_QUERY, mapper);
    }

    @Override
    public Optional<Film> findById(Long id) {
        try {
            Film result = jdbc.queryForObject(FIND_FILM_BY_ID_QUERY, Map.of("filmId", id), mapper);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }
}