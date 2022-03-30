create table accounts
(
    user_identifier             bigint           not null
        primary key,
    username                    tinytext         not null,
    hashed_password             text             not null,
    epoch_registered            bigint           not null,
    last_message_received_epoch bigint default 0 not null,
    constraint accounts2_hashed_password_uindex
        unique (hashed_password) using hash,
    constraint accounts2_user_id_uindex
        unique (user_identifier)
);

create table channels
(
    channel_identifier bigint not null
        primary key,
    channel_name       text   not null,
    creation_date      bigint not null,
    constraint channels_channel_id_uindex
        unique (channel_identifier)
);

create table channel_members
(
    channel_identifier bigint null,
    user_identifier    bigint null,
    join_date          bigint not null,
    constraint channel_members_accounts_user_identifier_fk
        foreign key (user_identifier) references accounts (user_identifier)
            on update cascade on delete cascade,
    constraint channel_members_channels_channel_id_fk
        foreign key (channel_identifier) references channels (channel_identifier)
            on update cascade on delete cascade
);

create table messages
(
    message_identifier bigint        not null
        primary key,
    channel_sent_id    bigint        not null,
    author_identifier  bigint        not null,
    message_contents   varchar(4369) not null,
    date_sent          bigint        not null,
    constraint messages_message_id_uindex
        unique (message_identifier),
    constraint messages_accounts_user_identifier_fk
        foreign key (author_identifier) references accounts (user_identifier)
            on update cascade,
    constraint messages_channels_channel_id_fk
        foreign key (channel_sent_id) references channels (channel_identifier)
            on update cascade on delete cascade
);

