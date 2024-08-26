--------------------------------------------------------------------------------
-- Filename:  V1.2__sample_data.sql
--------------------------------------------------------------------------------


-- Insert a fake USERS record
insert into users(id, cert_username, first_name, last_name, full_name, email, created_date, last_login_date, last_updated_date)
values(666, 'indiana.jones.12345', 'Indiana', 'Jones', 'Indiana Jones', 'indy@zztop.com', now() - interval '5 days', now(), now() );

insert into users(id, cert_username, first_name, last_name, full_name, email, created_date, last_login_date, last_updated_date)
values(777, 'luke.skywalker.22222', 'Luke', 'Skywalker', 'Luke Skywalker', 'luke@zztop.com', now() - interval '5 days', now(), now() );

insert into users(id, cert_username, first_name, last_name, full_name, email, created_date, last_login_date, last_updated_date)
values(888, 'ben.kinobi.33333', 'Ben', 'Kenobi', 'Ben Kenobi', 'ben@zztop.com', now() - interval '5 days', now(), now() );


-- Insert some fake exceptions records
insert into exceptions(id, user_id, cert_username, app_name, app_version, url, event_date, message, cause, stack_trace)
values
    (1001, 666, 'indiana.jones.12345', 'APP16 Web App', '1.0.1', '/api/reports/add',    now() - interval '25 days', 'message is here', 'cause is here', 'Here is hte long stack trace'),
    (1002, 666, 'indiana.jones.12345', 'APP16 Web App', '1.0.1', '/api/reports/add',    now() - interval '20 days', 'message is here', 'cause is here', 'Here is hte long stack trace'),
    (1003, 666, 'indiana.jones.12345', 'APP16 Web App', '1.0.1', '/api/reports/edit',   now() - interval '15 days', 'message is here', 'cause is here', 'Here is hte long stack trace'),
    (1004, 666, 'indiana.jones.12345', 'APP16 Web App', '1.0.1', '/api/reports/edit',   now() - interval '14 days', 'message is here', 'cause is here', 'Here is hte long stack trace'),
    (1005, 666, 'indiana.jones.12345', 'APP16 Web App', '1.0.1', '/api/reports/delete', now() - interval '10 days', 'message is here', 'cause is here', 'Here is hte long stack trace');
