package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mappers.FriendsRowMapper;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class FriendDbStorage implements FriendStorage {
    private static final String FIND_ALL_FRIENDS = "SELECT * FROM FRIENDS;";
    private static final String FIND_USER_FRIENDS = """
    SELECT *
    FROM "USER"
    WHERE user_id IN (
        SELECT FRIEND_ID
        FROM FRIENDS
        WHERE user_id = :userId);;
    """;

    private static final String FIND_FRIENDS_BY_IDS = "SELECT * FROM FRIENDS WHERE user_id IN (:userIds);";
    private static final String INSERT_FRIENDS_QUERY = "INSERT INTO FRIENDS (user_id, friend_id) " +
            "VALUES (:userId, :friendId);";

    private static final String CHECK_FRIENDSHIP_QUERY = "SELECT * FROM FRIENDS WHERE user_id = :userId AND friend_id = :friendId;";
    private static final String REMOVE_FRIEND_QUERY = "DELETE FROM FRIENDS WHERE user_id = :userId AND friend_id = :friendId;";
    private static final String FIND_MUTUAL_FRIENDS_QUERY =
            """
                    SELECT * FROM "USER" WHERE user_id IN
                    (SELECT u1.friend_id
                    FROM (SELECT user_id, friend_id FROM FRIENDS WHERE FRIENDS.USER_ID = :user1Id) AS u1
                    INNER JOIN (SELECT friend_id FROM FRIENDS WHERE FRIENDS.USER_ID = :user2Id) AS u2 ON u1.friend_id = u2.friend_id)""";

    private final NamedParameterJdbcOperations jdbc;
    private final FriendsRowMapper mapper;
    private final UserRowMapper userMapper;

    @Autowired
    public FriendDbStorage(NamedParameterJdbcOperations jdbc,
                           FriendsRowMapper mapper,
                           UserRowMapper userMapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
        this.userMapper = userMapper;
    }

    @Override
    public void addFriendToUser(Long userId, Long friendId) {
        SqlParameterSource sps = new MapSqlParameterSource(Map.of(
                "userId", userId,
                "friendId", friendId
                ));
        jdbc.update(INSERT_FRIENDS_QUERY, sps);
    }

    @Override
    public List<Friends> findAll() {
        return jdbc.query(FIND_ALL_FRIENDS, mapper);
    }

    @Override
    public List<User> findUserFriends(Long id) {
        try {
            return jdbc.query(FIND_USER_FRIENDS, Map.of("userId", id), userMapper);
        } catch (EmptyResultDataAccessException ignored) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Friends> findFriendsByIds(List<Long> ids) {
        return jdbc.query(FIND_FRIENDS_BY_IDS,
                Map.of("userIds", ids.stream()
                        .map(id -> Long.toString(id))
                        .collect(Collectors.joining(","))),
                mapper);
    }

    @Override
    public boolean checkFriendship(Long userId, Long friendId) {
        return jdbc.query(CHECK_FRIENDSHIP_QUERY, Map.of("userId", userId, "friendId", friendId), mapper).size() != 0;
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        jdbc.update(REMOVE_FRIEND_QUERY, Map.of("userId", userId, "friendId", friendId));
    }

    @Override
    public List<User> findMutualFriends(Long user1Id, Long user2Id) {
        return jdbc.query(FIND_MUTUAL_FRIENDS_QUERY, Map.of("user1Id", user1Id, "user2Id", user2Id), userMapper);
    }
}