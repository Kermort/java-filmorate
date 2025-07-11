package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private int id;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
    @Email(message = "некорректный адрес электронной почты")
    @NotBlank(message = "электронная почта не может быть пустой")
    private String email;
    @NotBlank(message = "логин не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "логин не должен содержать пробелы")
    private String login;
}
