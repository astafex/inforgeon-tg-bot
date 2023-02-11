truncate bot.user_settings cascade;
truncate bot.disliked cascade;

insert into bot.user_settings (username) values ('testuser');

insert into bot.disliked (id, count, tag, topic, username)
    values (1, 5, 'сервер', 'NEWS', 'testuser')