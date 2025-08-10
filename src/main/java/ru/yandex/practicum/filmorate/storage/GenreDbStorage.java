package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class GenreDbStorage implements GenreStorage {
    private final NamedParameterJdbcOperations jdbc;
    private final GenreRowMapper mapper;

    private static final String FIND_GENRE_BY_IDS_QUERY = "SELECT * FROM GENRE WHERE genre_id IN (:genreIds);";
    private static final String FIND_ALL_QUERY = "SELECT * FROM GENRE;";
    private static final String FIND_GENRE_BY_ID_QUERY = "SELECT * FROM GENRE WHERE genre_id = :genreId";

    @Autowired
    public GenreDbStorage(NamedParameterJdbcOperations jdbc, GenreRowMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public List<Genre> findByIds(List<Long> ids) {
        return jdbc.query(FIND_GENRE_BY_IDS_QUERY, Map.of("genreIds", ids), mapper);
    }

    @Override
    public List<Genre> findAll() {
        return jdbc.query(FIND_ALL_QUERY, mapper);
    }

    @Override
    public Optional<Genre> findById(Long genreId) {
        try {
            Genre result = jdbc.queryForObject(FIND_GENRE_BY_ID_QUERY, Map.of("genreId", genreId), mapper);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }
}