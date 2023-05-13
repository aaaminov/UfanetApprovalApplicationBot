create schema `tg_bot`;
use `tg_bot`;

create table `subscribers`(
	id bigint not null unique primary key,
    date datetime not null default current_timestamp
);