package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendStorage {
    List<Friends> findAll();

    List<Friends> findUserFriends(Long id);

    List<Friends> findFriendsByIds(List<Long> ids);

    void addFriendToUser(Long userId, Long friendId);

    boolean checkFriendship(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<User> findMutualFriends(Long user1, Long user2);

}