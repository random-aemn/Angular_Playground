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

insert into uicontrols(id, name) values(3001, 'page/user-settings-layout');

-- Define the specialists routes
insert into uicontrols(id, name) values(3101, 'page/specialist/my-queue');
insert into uicontrols(id, name) values(3102, 'page/specialist/details/:id');

-- Define the supervisor routes
insert into uicontrols(id, name) values(3201, 'page/supervisor/my-queue');
insert into uicontrols(id, name) values(3202, 'page/supervisor/details/:id');
insert into uicontrols(id, name) values(3203, 'page/supervisor/my-queue/create-alert');

-- Define the chart routes
insert into uicontrols(id, name) values(3301, 'page/charts/all');
insert into uicontrols(id, name) values(3302, 'page/charts/cases-by-state-chart');
insert into uicontrols(id, name) values(3303, 'page/charts/cases-by-priority-chart');
insert into uicontrols(id, name) values(3304, 'page/charts/average-case-processing-time-chart');
insert into uicontrols(id, name) values(3305, 'page/charts/case-age-analysis-chart');
insert into uicontrols(id, name) values(3306, 'page/charts/process-funnel-chart');
insert into uicontrols(id, name) values(3307, 'page/charts/cases-completed-by-user-chart');
insert into uicontrols(id, name) values(3308, 'page/charts/user-activity-trends-chart');
insert into uicontrols(id, name) values(3309, 'page/charts/case-completion-trends-chart');
insert into uicontrols(id, name) values(3310, 'page/charts/alerts-by-alert-priority-chart');
insert into uicontrols(id, name) values(3311, 'page/charts/cases-by-assignment-chart');

-- Define the admin routes
insert into uicontrols(id, name) values(3401, 'page/admin/dashboard');
insert into uicontrols(id, name) values(3402, 'page/admin/manage-banners');
insert into uicontrols(id, name) values(3403, 'page/admin/list-exceptions');
insert into uicontrols(id, name) values(3404, 'page/admin/allowed-file-types');

-- Define the search/dossier route
insert into uicontrols(id, name) values(3501, 'page/search');

-----------------------------------------------------------------------------
-- Grant page url access to individual roles
-----------------------------------------------------------------------------

-- Grant page routes to the APP16_SUPERVISOR role  (role id = 1)
-- NOTE:  This is everything page route *EXCEPT* the specialists queue (3101)  and specialists details page (3102) and the admin pages
insert into roles_uicontrols(role_id, uicontrol_id)
values (1, 3001),
       (1, 3201),
       (1, 3202),
       (1, 3203),
       (1, 3301),
       (1, 3302),
       (1, 3303),
       (1, 3304),
       (1, 3305),
       (1, 3306),
       (1, 3307),
       (1, 3308),
       (1, 3309),
       (1, 3310),
       (1, 3311),
       (1, 3403),
       (1, 3501);

-- Grant page routes to the APP16_SPECIALIST role  (role id = 2)
-- Can see specialist pages and metrics pages  (not admin or supervisor/reviewer pages)
insert into roles_uicontrols(role_id, uicontrol_id)
values (2, 3001),
       (2, 3101),
       (2, 3102),
       (2, 3301),
       (2, 3302),
       (2, 3303),
       (2, 3304),
       (2, 3305),
       (2, 3306),
       (2, 3307),
       (2, 3308),
       (2, 3309),
       (2, 3310),
       (2, 3311),
       (2, 3501);

-- Grant page routes to the APP16_ADMIN role     (role id = 3)
-- NOTE:  This user gets access to *ALL* page routes
insert into roles_uicontrols(role_id, uicontrol_id)
(
    select 3 as role_id, id as uicontrol_id
    from uicontrols
);

-- Grant access to *ALL* pages to the APP16_REVIEWER role  (role id = 4)
-- NOTE:  This is everything page route *EXCEPT* the specialists queue (3101)  and specialists details page (3102) and the admin pages
insert into roles_uicontrols(role_id, uicontrol_id)
values (4, 3001),
       (4, 3201),
       (4, 3202),
       (4, 3203),
       (4, 3301),
       (4, 3302),
       (4, 3303),
       (4, 3304),
       (4, 3305),
       (4, 3306),
       (4, 3307),
       (4, 3308),
       (4, 3309),
       (4, 3403),
       (4, 3501);