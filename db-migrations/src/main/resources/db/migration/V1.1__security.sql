--------------------------------------------------------------------------------
-- Filename:  V1.1__security.sql
--------------------------------------------------------------------------------


-----------------------------------------------------------------------------
-- Create this table:  roles
-----------------------------------------------------------------------------
create table roles (
   id   integer     not null,
   name varchar(50) not null,
   primary key(id)
);
comment on table  roles       is 'This table holds all of the application roles used by the web app.';
comment on column roles.id    is 'This number uniquely identifies this role.';
comment on column roles.name  is 'This identifies the name of the role.';



-----------------------------------------------------------------------------
-- Create this table:  users_roles
-----------------------------------------------------------------------------
create table users_roles (
    id                   integer           null,
    user_id              integer           null,
    role_id              integer           null,
    primary key(id),
    constraint ur_uniq unique(user_id, role_id),
    constraint ur_userId FOREIGN KEY(user_id)    references users(id),
    constraint ur_roleId FOREIGN KEY(role_id)    references roles(id)
);
comment on table users_roles       is 'This table holds information about which roles are granted to the user';


-----------------------------------------------------------------------------
-- Create this table:  users_roles_aud
-----------------------------------------------------------------------------
create table users_roles_aud (
    id                   integer           null,
    user_id              integer           null,
    role_id              integer           null,
    timestamp            timestamp     not null,
    username             varchar(100)  not null,
    audit_type           integer       not null,
    transaction_id       integer       not null
);
comment on table users_roles_aud       is 'This table holds audit information regarding changes to roles granted to the user';

-- Define the security roles
insert into roles(id, name)
values (1, 'APP16_SUPERVISOR'),
       (2, 'APP16_SPECIALIST'),
       (3, 'APP16_ADMIN'),
       (4, 'APP16_REVIEWER');


-----------------------------------------------------------------------------
-- Create this table:  ui_controls
-----------------------------------------------------------------------------
create table uicontrols (
    id   integer     not null,
    name varchar(50) not null,
    primary key(id)
);

comment on table  uicontrols       is 'This table holds all of the application roles used by the web app.';
comment on column uicontrols.id   is 'This number uniquely identifies this UI feature.';
comment on column uicontrols.name is 'This identifies the name of the UI feature.';


-----------------------------------------------------------------------------
-- Create this table:  roles_uicontrols
-----------------------------------------------------------------------------
create table roles_uicontrols (
      role_id      integer not null,
      uicontrol_id integer not null
);
comment on table  roles_uicontrols   is 'This table holds the relationships between the roles and uicontrols tables.';


-- Define the page routes
-- NOTE:  These routes MUST match the routes found in constants.ts
insert into uicontrols(id, name) values(3001, 'page/user-settings');
insert into uicontrols(id, name) values(3002, 'page/sample-page');
insert into uicontrols(id, name) values(3003, 'page/sample-page-2');
insert into uicontrols(id, name) values(3004, 'page/admin/list-exceptions');
insert into uicontrols(id, name) values(3005, 'page/admin/user-admin');
insert into uicontrols(id, name) values(5000, 'page/exercise-1a');
insert into uicontrols(id, name) values(5001, 'page/exercise1b');
insert into uicontrols(id, name) values(5002, 'page/registration/approved');
insert into uicontrols(id, name) values(5003, 'page/html-over-image');
insert into uicontrols(id, name) values(5004, 'page/my-settings');
insert into uicontrols(id, name) values(5005, 'page/stock-trades');
insert into uicontrols(id, name) values(5006, 'page/responsive-layout');
insert into uicontrols(id, name) values(5007, 'page/variable-height');
insert into uicontrols(id, name) values(5008, 'page/holy-grail-not');
insert into uicontrols(id, name) values(5009, 'page/holy-grail-real');





-----------------------------------------------------------------------------
-- Grant page url access to individual roles
-----------------------------------------------------------------------------

-- Grant page routes to the APP16_SUPERVISOR role  (role id = 1)
-- NOTE:  This is everything page route *EXCEPT* the specialists queue (3101)  and specialists details page (3102) and the admin pages
insert into roles_uicontrols(role_id, uicontrol_id)
values (1, 3001),
       (1, 3002),
       (1, 3003),
       (1, 3004),
       (1, 3005);



-- Grant page routes to the APP16_SPECIALIST role  (role id = 2)
-- Can see specialist pages and metrics pages  (not admin or supervisor/reviewer pages)
insert into roles_uicontrols(role_id, uicontrol_id)
values (1, 3001),
       (1, 3002),
       (1, 3003),
       (1, 3004),
       (1, 3005);



-- Grant page routes to the APP16_ADMIN role     (role id = 3)
-- NOTE:  This user gets access to *ALL* page routes
insert into roles_uicontrols(role_id, uicontrol_id)
(
    select 3 as role_id, id as uicontrol_id
    from uicontrols
);



-- Grant access to *ALL* pages to the APP16_REVIEWER role  (role id = 4)
insert into roles_uicontrols(role_id, uicontrol_id)
    (
        select 4 as role_id, id as uicontrol_id
        from uicontrols
    );