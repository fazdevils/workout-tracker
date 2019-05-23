drop table workout if exists;
drop table users if exists;

create table users (
    id bigint generated by default as identity primary key,
    login varchar(80) not null,
    password varchar(80) not null,
    bitwiseRole integer default 0 not null,
    active boolean default false not null,
    UNIQUE (login)
);

create table workout (
    id bigint generated by default as identity primary key,
    userId bigint not null,
    dateMs bigint not null,
    distance double not null,
    duration double not null,
    postalCode varchar(10) not null,
    weather varchar(80) not null,
    FOREIGN KEY (userId) REFERENCES users(id) ON DELETE CASCADE
);