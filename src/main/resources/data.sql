INSERT INTO PUBLIC.GENRE (NAME) VALUES
    ('Комедия'),
    ('Драма'),
    ('Мультфильм'),
    ('Триллер'),
    ('Документальный'),
    ('Боевик');

INSERT INTO PUBLIC.MPA (NAME) VALUES
    ('G'),
    ('PG'),
    ('PG-13'),
    ('R'),
    ('NC-17');

INSERT INTO PUBLIC."USER" (EMAIL, LOGIN, "NAME", BIRTHDAY) VALUES
    ('pavel@mail.ru', 'pavel', 'pavel', '2000-01-01'),
    ('olga@mail.ru', 'olga', 'olga', '2000-10-10'),
    ('sasha@mail.ru', 'sasha', 'sahsa', '2001-11-11'),
    ('vika@mail.ru', 'vika', 'vika', '2000-05-05');

INSERT INTO PUBLIC.FRIENDS (USER_ID, FRIEND_ID) VALUES
    ((SELECT USER_ID FROM "USER" WHERE EMAIL = 'pavel@mail.ru'), (SELECT USER_ID FROM "USER" WHERE EMAIL = 'olga@mail.ru')),
    ((SELECT USER_ID FROM "USER" WHERE EMAIL = 'olga@mail.ru'), (SELECT USER_ID FROM "USER" WHERE EMAIL = 'pavel@mail.ru'));

INSERT INTO PUBLIC.FILM (NAME,DESCRIPTION,MPA_ID,RELEASE_DATE,DURATION) VALUES
	 ('film_name','film_description',1,'2022-11-11',60),
	 ('film_name2','film_description2',2,'2022-11-11',70),
	 ('film_name3','film_description3',3,'2022-11-11',80),
	 ('film_name4','film_description4',4,'2022-11-11',90),
	 ('film_name5','film_description5',5,'2022-11-11',100);

INSERT INTO PUBLIC.FILM_GENRE (FILM_ID,GENRE_ID) VALUES
	 (1,3),
	 (2,1),
	 (3,5);

INSERT INTO PUBLIC.FILM_LIKES (FILM_ID,USER_ID) VALUES
	 (1,2),
	 (1,1),
	 (2,1),
	 (3,2);
