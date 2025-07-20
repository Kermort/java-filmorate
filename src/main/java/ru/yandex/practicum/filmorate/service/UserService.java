package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long user1Id, Long user2Id) {
        User user1 = userStorage.findById(user1Id); //работает как проверка наличия пользователя с заданным id ->
        User user2 = userStorage.findById(user2Id); //-> и проверка на null
        user1.addFriend(user2.getId());
        user2.addFriend(user1.getId());
    }

    public void removeFriend(Long user1Id, Long user2Id) {
        User user1 = userStorage.findById(user1Id); //работает как проверка наличия пользователя с заданным id ->
        User user2 = userStorage.findById(user2Id); //-> и проверка на null
        user1.removeFriend(user2.getId());
        user2.removeFriend(user1.getId());
    }

    public List<User> findMutualFriends(Long user1Id, Long user2Id) {
        List<User> friends1 = findUserFriends(user1Id);
        List<User> friends2 = findUserFriends(user2Id);

        return friends1.stream()
                .filter(friends2::contains)
                .collect(Collectors.toList());
    }

    public User create(User newUser) {
        return userStorage.create(newUser);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(long id) {
        return userStorage.findById(id);
    }

    public List<User> findUserFriends(long id) {
        return userStorage.findById(id).getFriends().stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }
}
