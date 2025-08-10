package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserDbStorage implements UserStorage {
    private static final String FIND_ALL_USERS = "SELECT * FROM \"USER\";";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM \"USER\" WHERE email = :email";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM \"USER\" WHERE user_id = :userId";
    private static final String FIND_BY_IDS_QUERY = "SELECT * FROM \"USER\" WHERE user_id IN (:userIds);";
    private static final String INSERT_INTO_USER = "INSERT INTO \"USER\" " +
            "(EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (:email, :login, :name, :birthday);";

    private static final String UPDATE_USER_QUERY = "UPDATE \"USER\" SET " +
            "EMAIL = :email, LOGIN = :login, NAME = :name, BIRTHDAY = :birthday WHERE user_id = :userId";

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final NamedParameterJdbcOperations jdbc;
    private final UserRowMapper mapper;

    @Autowired
    public UserDbStorage(NamedParameterJdbcOperations jdbc, UserRowMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public User create(User newUser) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource sps = new MapSqlParameterSource(Map.of(
                "email", newUser.getEmail(),
                "login", newUser.getLogin(),
                "name", newUser.getName(),
                "birthday", newUser.getBirthday().format(DATE_FORMAT)));
        jdbc.update(INSERT_INTO_USER, sps, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);
        if (id != null) {
            newUser.setId(Long.valueOf(id));
            return newUser;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    @Override
    public User update(User newUser) {
        int rowsUpdated = jdbc.update(UPDATE_USER_QUERY, Map.of(
                "email", newUser.getEmail(),
                "login", newUser.getLogin(),
                "name", newUser.getName(),
                "birthday", newUser.getBirthday().format(DATE_FORMAT),
                "userId", newUser.getId()
        ));
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
        return newUser;
    }

    @Override
    public List<User> findAll() {
        return jdbc.query(FIND_ALL_USERS, mapper);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            User result = jdbc.queryForObject(FIND_BY_EMAIL_QUERY, Map.of("email", email), mapper);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try {
            User result = jdbc.queryForObject(FIND_BY_ID_QUERY, Map.of("userId", id), mapper);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findByIds(List<Long> ids) {
        return jdbc.query(FIND_BY_IDS_QUERY,
                Map.of("userIds", ids),
                mapper);
    }
}