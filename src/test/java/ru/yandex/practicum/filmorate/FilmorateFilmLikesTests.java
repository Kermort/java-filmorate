package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.mappers.LikeRowMapper;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeDbStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({LikeDbStorage.class, LikeRowMapper.class, FilmRowMapper.class})
public class FilmorateFilmLikesTests {
    private final LikeDbStorage likeStorage;

    @Test
    void contextLoads() {
    }

    @Test
    void findLikeByFilmIdTest() {
        List<Like> likes = likeStorage.findByFilmId(1L);
        assertEquals(2, likes.size());

        assertEquals(2, likes.get(0).getUserId());
    }
}
