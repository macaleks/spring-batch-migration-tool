--
create table genres(id number primary key,
                    name varchar2(255 char) not null);

create table authors(id number primary key,
                     first_name varchar2(255 char) not null,
                     second_name varchar2(255 char) not null);

create table books(id number primary key,
                   name varchar2(255 char) not null,
                   id_author number not null references authors(id),
                   id_genre number not null references genres(id));

create sequence book_seq start with 1;