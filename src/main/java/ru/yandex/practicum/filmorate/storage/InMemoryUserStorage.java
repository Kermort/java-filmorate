package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    public User create(User newUser) {
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }

        newUser.setId(getNextId());
        users.put(newUser.getId(), newUser);
        log.info("добавлен пользователь с id = {}", newUser.getId());
        return newUser;
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ValidationException("id не может быть пустым");
        }

        if (!users.containsKey(newUser.getId())) {
            throw new NotFoundException("пользователь с id = " + newUser.getId() + " не найден");
        }

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }

        users.put(newUser.getId(), newUser);
        log.info("обновлен пользователь с id = {}", newUser.getId());
        return newUser;
    }

    public List<User> findAll() {
        return users.values().stream().toList();
    }

    public User findById(Long id) {
        if (id == null) {
            throw new ValidationException("id не может быть пустым");
        }
        if (!users.containsKey(id)) {
            throw new NotFoundException("пользователь с id " + id + " не найден.");
        }
        return users.get(id);
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
