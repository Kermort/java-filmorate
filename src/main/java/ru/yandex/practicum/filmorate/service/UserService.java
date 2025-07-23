package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long user1Id, Long user2Id) {
        User user1 = findById(user1Id);
        User user2 = findById(user2Id);
        user1.addFriend(user2.getId());
        user2.addFriend(user1.getId());
    }

    public void removeFriend(Long user1Id, Long user2Id) {
        User user1 = findById(user1Id);
        User user2 = findById(user2Id);
        user1.removeFriend(user2.getId());
        user2.removeFriend(user1.getId());
    }

    public List<User> findMutualFriends(Long user1Id, Long user2Id) {
        Set<Long> user1FriendsIds = findById(user1Id).getFriends();
        Set<Long> user2FriendsIds = findById(user2Id).getFriends();

        List<Long> mutualIds = user1FriendsIds.stream()
                .filter(user2FriendsIds::contains)
                .toList();

        return mutualIds.stream()
                .map(this::findById)
                .toList();
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

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        return userStorage.update(newUser);
    }

    public List<User> findAll() {
        return userStorage.findAll();
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
        return findById(id).getFriends().stream()
                .map(this::findById)
                .toList();
    }
}
