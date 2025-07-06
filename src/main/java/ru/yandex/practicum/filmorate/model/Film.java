package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @NotNull
    private LocalDate releaseDate;
    @NotBlank
    @Size(max = 200, message = "Описание фильма не может быть больше 200 символов")
    private String description;
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
}
