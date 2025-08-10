package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Qualifier("MpaDbStorage")
public class MpaDbStorage implements MpaStorage {
    private final NamedParameterJdbcOperations jdbc;
    private final MpaRowMapper mapper;

    private static final String FIND_ALL_MPA_QUERY = "SELECT * FROM MPA;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM MPA WHERE mpa_id = :mpaId;";

    @Autowired
    public MpaDbStorage(NamedParameterJdbcOperations jdbc, MpaRowMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public List<Mpa> findAll() {
        return jdbc.query(FIND_ALL_MPA_QUERY, mapper);
    }

    @Override
    public Optional<Mpa> findById(Long id) {
        try {
            Mpa result = jdbc.queryForObject(FIND_BY_ID_QUERY, Map.of("mpaId", id), mapper);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }
}