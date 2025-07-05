package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Logger;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(UserController.class);
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User newUser, BindingResult br) {
        try {
            List<String> errors = new ArrayList<>();
            if (br.hasErrors()) {
                errors.addAll(br.getAllErrors().stream().map(ObjectError::getDefaultMessage).toList());
            }

            if (!errors.isEmpty()) {
                throw new ValidationException(Arrays.toString(errors.toArray()));
            }
        } catch (ValidationException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(newUser);
        }

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
        try {
            List<String> errors = new ArrayList<>();
            if (!users.containsKey(newUser.getId())) {
                throw new NotFoundException("пользователь с id = " + newUser.getId() + " не найден");
            }
            if (br.hasErrors()) {
                errors.addAll(br.getAllErrors().stream().map(ObjectError::getDefaultMessage).toList());
            }
            if (!errors.isEmpty()) {
                throw new ValidationException(Arrays.toString(errors.toArray()));
            }

        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(404).body(newUser);
        } catch (ValidationException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(newUser);
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
