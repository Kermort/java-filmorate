package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
public class User {
    private Long id;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
    @Email(message = "некорректный адрес электронной почты")
    @NotBlank(message = "электронная почта не может быть пустой")
    private String email;
    @NotBlank(message = "логин не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "логин не должен содержать пробелы")
    private String login;
    private Set<Long> friends = new HashSet<>();

    public void addFriend(long otherUserId) {
        friends.add(otherUserId);
    }

    public void removeFriend(long otherUserId) {
        friends.remove(otherUserId);
    }
}
