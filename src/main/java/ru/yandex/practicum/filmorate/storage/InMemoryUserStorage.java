package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    public User create(User newUser) {
        newUser.setId(getNextId());
        users.put(newUser.getId(), newUser);
        log.info("добавлен пользователь с id = {}", newUser.getId());
        return newUser;
    }

    public User update(User newUser) {
        users.put(newUser.getId(), newUser);
        log.info("обновлен пользователь с id = {}", newUser.getId());
        return newUser;
    }

    public List<User> findAll() {
        return users.values().stream().toList();
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
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
