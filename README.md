# java-filmorate

## Схема базы данных

![Ссылка](/schema.png)

## Примеры запросов к базе данных
*Для указания переменных значений используется формат типа :userId*

Получение всех фильмов
```
SELECT * FROM FILM;
```

Получение фильма по id
```
SELECT * FROM FILM WHERE film_id = :film_id;
```

Добавление фильма
```
INSERT INTO FILM (name, duration, description, release_date, mpa_id) 
VALUES (:name, :duration, :description, :releaseDate, :mpaId);
```

Обновление фильма
```
UPDATE FILM 
SET name = :name, description = :description, mpa_id = :mpaId, release_date = :releaseDate, duration = :duration 
WHERE film_id = :filmId;
```

Добавление лайка фильму
```
INSERT INTO FILM_LIKES (film_id, user_id) 
VALUES (:filmId, :userId);
```

Удаления лайка у фильма
```
DELETE 
FROM FILM_LIKES 
WHERE film_id = :filmId AND user_id = :userId;
```

Получение 10 фильмов с наибольшим количеством лайков
```
SELECT l.film_id, f.NAME, f.DESCRIPTION, f.MPA_ID, f.RELEASE_DATE, f.DURATION
FROM FILM_LIKES AS l INNER JOIN FILM AS f ON l.film_id = f.film_id
GROUP BY l.film_id
ORDER BY COUNT(l.film_id) DESC
LIMIT 10;
```

Получение жанров фильма
```
SELECT g.genre_id, g.name
FROM GENRE AS g INNER JOIN FILM_GENRE AS fg ON g.genre_id = fg.genre_id
WHERE fg.film_id = :filmId
ORDER BY g.genre_id;
```

Получение списка рейтингов
```
SELECT * FROM MPA;
```

Получение рейтинга по id
```
SELECT * FROM MPA 
WHERE mpa_id = :mpaId;
```

Получение всех пользователей
```
SELECT * FROM "USER";
```

Получение пользователя по id
```
SELECT * FROM "USER" WHERE user_id IN (:userIds);
```

Добавление пользователя
```
INSERT INTO "USER" (email, login, name, birthday) 
VALUES (:email, :login, :name, :birthday);
```

Получение пользователя по email
```
SELECT * FROM "USER" WHERE email = :email
```

Получение общих друзей пользователей с id 1 и 2
```
SELECT * 
  FROM "USER" 
  WHERE user_id IN
    (SELECT u1.friend_id
      FROM (SELECT id, user_id, friend_id FROM FRIENDS WHERE FRIENDS.USER_ID = :userId1) AS u1
      INNER JOIN (SELECT friend_id FROM FRIENDS WHERE FRIENDS.USER_ID = :userId2) AS u2 ON u1.friend_id = u2.friend_id);
```