package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User newUser) {
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }

        newUser.setId(getNextId());
        users.put(newUser.getId(), newUser);
        log.info("добавлен пользователь с id = {}", newUser.getId());
        return ResponseEntity.ok(newUser);
    }

    @PutMapping
    public ResponseEntity<User> update(@NotNull @Valid @RequestBody User newUser, BindingResult br) {
        if (!users.containsKey(newUser.getId())) {
            log.error("пользователь с id = " + newUser.getId() + " не найден");
            return ResponseEntity.status(404).body(newUser);
        }
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        users.put(newUser.getId(), newUser);
        log.info("обновлен пользователь с id = {}", newUser.getId());
        return ResponseEntity.ok(newUser);
    }

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
