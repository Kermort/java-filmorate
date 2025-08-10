package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    @Autowired
    public UserService(UserStorage userStorage, FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        List<User> userAndFriend = userStorage.findByIds(List.of(userId, friendId));
        if (userAndFriend.size() < 2) {
            throw new NotFoundException("Пользователь не найден");
        }

        if (friendStorage.checkFriendship(userId, friendId)) {
            throw new ValidationException("пользователь с id " + friendId + " уже в друзьях у пользователя с id " + userId);
        }

        friendStorage.addFriendToUser(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        List<User> userAndFriend = userStorage.findByIds(List.of(userId, friendId));
        if (userAndFriend.size() < 2) {
            throw new NotFoundException("Пользователь не найден");
        }

        friendStorage.removeFriend(userId, friendId);
    }

    public List<User> findMutualFriends(Long user1Id, Long user2Id) {
        List<User> userAndFriend = userStorage.findByIds(List.of(user1Id, user2Id));
        if (userAndFriend.size() < 2) {
            throw new NotFoundException("Пользователь не найден");
        }

        return friendStorage.findMutualFriends(user1Id, user2Id);
    }

    public User create(User newUser) {
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }

        return userStorage.create(newUser);
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ValidationException("id не может быть пустым");
        }

        Optional<User> userOpt = userStorage.findById(newUser.getId());
        if (userOpt.isEmpty()) {
            throw new NotFoundException("пользователь с id = " + newUser.getId() + " не найден");
        }

        if (!userOpt.get().getEmail().equals(newUser.getEmail())
                && userStorage.findByEmail(newUser.getEmail()).isPresent()) {
            throw new ValidationException("Выбранный email уже используется");
        }

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(userOpt.get().getName());
        }

        if (newUser.getBirthday() == null) {
            newUser.setBirthday(userOpt.get().getBirthday());
        }

        if (newUser.getLogin() == null) {
            newUser.setLogin(userOpt.get().getLogin());
        }

        return userStorage.update(newUser);
    }

    public List<User> findAll() {
        List<User> users = userStorage.findAll();
        List<Friends> friends = friendStorage.findAll();
        Map<Long, Set<Long>> friendsMap = new HashMap<>();
        friends.forEach((f) -> {
                    friendsMap.putIfAbsent(f.getUserId(), new HashSet<>());
                    friendsMap.get(f.getUserId()).add(f.getFriendId());
                });

        users.forEach(u -> {
            if (friendsMap.get(u.getId()) != null) {
                u.setFriends(friendsMap.get(u.getId()));
            }
        });

        return users;
    }

    public User findById(Long id) {
        if (id == null) {
            throw new ValidationException("id не может быть пустым");
        }

        Optional<User> userOpt = userStorage.findById(id);
        if (userOpt.isEmpty()) {
            throw new NotFoundException("пользователь с id " + id + " не найден.");
        }

        return userOpt.get();
    }

    public List<User> findUserFriends(long id) {
        Optional<User> userOpt = userStorage.findById(id);
        if (userOpt.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }

        return friendStorage.findUserFriends(id);
    }
}