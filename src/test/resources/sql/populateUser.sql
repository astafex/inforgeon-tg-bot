truncate bot.user_settings cascade;
truncate bot.disliked cascade;

insert into bot.user_settings (username) values ('testuser');