-----------------------------------------------------------------------------
-- Filename:  V1.0__baseline.sql
-----------------------------------------------------------------------------

-- Create sequences used for  ids and for transactions
Create sequence seq_table_ids increment by 1 START WITH 5000;
Create sequence seq_transaction_ids increment by 1 START WITH 1000;


-----------------------------------------------------------------------------
-- Create this table:  system_parameters
-----------------------------------------------------------------------------
create table system_parameters
(
    name              varchar(100)    not null,
    value             varchar         not null,
    primary key(name)
);
comment on table system_parameters is 'This table holds certain system parameters and data -- e.g., when the niss data was last refreshed';



-----------------------------------------------------------------------------
-- Create this table:  exceptions
-----------------------------------------------------------------------------
create table exceptions
(
    id               integer      not null,
    user_id          integer          null,
    cert_username    varchar(256) not null,
    app_name         varchar(100) not null,
    app_version      varchar(100) not null,
    url              varchar(200) not null,
    event_date       timestamp    not null,
    message          text             null,
    cause            text             null,
    stack_trace      text             null,
    primary key(id)
);
comment on table exceptions is 'The Exceptions table holds information about exceptions raised during web app operation';

-- Add an index so that filtering on this table by event_date runs faster
create index on exceptions(event_date);




-----------------------------------------------------------------------------
-- Create this table:  users
-----------------------------------------------------------------------------
create table users
(
    id                   integer       not null,
    version              integer       not null default (1),
    cert_username        varchar(100)  not null,              -- comes from cac card
    is_locked            boolean       not null default(false),
    first_name           varchar(100)      null,
    last_name            varchar(100)      null,
    full_name            varchar(200)      null,
    email                varchar(200)      null,
    created_date         timestamp     not null,
    last_login_date         timestamp                    not null,
    last_updated_date       timestamp                    not null,
    acknowledgement_date    timestamp with time zone     null,
    primary key (id)
);
comment on table users is 'The Users table holds information about each user';

-- Add a user record for the SYSTEM user
insert into users(id, version, cert_username, is_locked, first_name, last_name, full_name, email, created_date, last_login_date, last_updated_date, acknowledgement_date)
values(10, 1, 'SYSTEM', true, '', '', 'SYSTEM', null, '2024-03-10 21:46:41.823072', '2024-03-13 21:46:41.823072', '2024-03-13 21:46:41.823072', null);



-----------------------------------------------------------------------------
-- Create this table:  users_aud
-----------------------------------------------------------------------------
create table users_aud
(
    id                   integer           null,
    version              integer           null,
    cert_username        varchar(100)      null,
    is_locked            boolean           null,
    first_name           varchar(100)      null,
    last_name            varchar(100)      null,
    full_name            varchar(200)      null,
    email                varchar(100)      null,
    created_date         timestamp         null,
    last_login_date         timestamp         null,
    last_updated_date       timestamp         null,
    timestamp               timestamp     not null,
    username                varchar(100)  not null,
    audit_type              integer       not null, --0 create, 1 update, 2 delete
    transaction_id          integer       not null,
    acknowledgement_date    timestamp with time zone null

);
comment on table Users_aud is 'The Audit table for the Users table';






-----------------------------------------------------------------------------------------
-- Create this table:  user_preferences
-- NOTE:  This table does not have a unique ID.  Instead the userid, page, name is unique
-----------------------------------------------------------------------------------------
create table user_preferences
(
    id      integer           not null,
    userid  integer           not null,
    page    varchar               null,
    name    varchar(50)       not null,
    value   text              not null,
    constraint userPreferences_userid FOREIGN KEY (userid) references users (id),
    unique (userid, page, name)
);
comment on table user_preferences is 'The user_preferences table holds preferences for each user';

-- Add indexes to the user_preferences table
create index on user_preferences(userid);
create index on user_preferences(userid, name, page);



-----------------------------------------------------------------------------
-- Create this table:  user_preferences_aud
-----------------------------------------------------------------------------
create table user_preferences_aud
(
    id             integer          null,
    userid         integer          null,
    page           varchar          null,
    name           varchar(50)      null,
    value          text             null,
    timestamp      timestamp    not null,
    username       varchar(100) not null,
    audit_type     integer      not null,
    transaction_id integer      not null
);
comment on table user_preferences_aud is 'The Audit table for the User_preferences table';


