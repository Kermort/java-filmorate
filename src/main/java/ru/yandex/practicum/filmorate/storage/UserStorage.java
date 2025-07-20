package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User create(User newUser);

    User update(User newUser);

    List<User> findAll();

    User findById(Long id);

}
