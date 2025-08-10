package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mappers.FilmGenreRowMapper;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmGenreDbStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmGenreDbStorage.class, FilmGenreRowMapper.class, GenreRowMapper.class})
public class FilmorateFilmGenreTests {
    private final FilmGenreDbStorage filmGenreStorage;

    @Test
    void contextLoads() {
    }

    @Test
    void findFilmGenreByFilmIdTest() {
        List<Genre> g = filmGenreStorage.findByFilmId(1L);
        assertEquals("Мультфильм",g.get(0).getName());
    }
}
