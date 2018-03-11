# --- Sample dataset

# --- !Ups

INSERT INTO user_group (type_name, id)
VALUES ('Admin', 1);
INSERT INTO user_group (type_name, id)
VALUES ('User', 2);

INSERT INTO USERS (username, email, password_hash, validated, user_group_id)
VALUES ('Admin', 'admin@admin.ru', '$2a$10$M.4njteQu71zG3A1riguROBBPtw3MKvj5gly1KscPOAxMTzEajGi.', TRUE, 1);

INSERT INTO permissions (name, to_create, to_read, to_update, to_delete, is_owner)
VALUES ('Owner', TRUE, TRUE, TRUE, TRUE, TRUE);
INSERT INTO permissions (name, to_create, to_read, to_update, to_delete, is_owner)
VALUES ('Guest', FALSE, TRUE, FALSE, FALSE, FALSE);

INSERT INTO type_file (type_name)
VALUES ('File');

INSERT INTO type_file (type_name)
VALUES ('Directory');



# --- !Downs

delete from version_file;
delete from repo_file;
delete from project_members;
delete from project;
delete from users;
delete from user_group;
delete from permissions;
delete from type_file;
