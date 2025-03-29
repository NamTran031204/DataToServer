create database Data

create table Temperatures(
    id int auto_increment primary key,
    temp float default null,
    collected_time datetime
)

create table Humidity(
    id int auto_increment primary key,
    humid float default null,
    collected_time datetime
)