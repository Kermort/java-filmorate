package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User create(User newUser);

    User update(User newUser);

    List<User> findAll();

    Optional<User> findById(Long id);

    List<User> findByIds(List<Long> ids);

    Optional<User> findByEmail(String email);
}