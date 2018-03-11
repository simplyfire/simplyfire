# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table auth_token (
  id                            bigint auto_increment not null,
  user_id                       bigint,
  token                         varchar(255),
  constraint pk_auth_token primary key (id)
);

create table feedback (
  id                            bigint auto_increment not null,
  theme                         varchar(255),
  content                       varchar(1000),
  user_id                       bigint,
  date_creation                 timestamp,
  constraint pk_feedback primary key (id)
);

create table permissions (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  to_create                     boolean default false not null,
  to_read                       boolean default false not null,
  to_update                     boolean default false not null,
  to_delete                     boolean default false not null,
  is_owner                      boolean default false not null,
  constraint pk_permissions primary key (id)
);

create table project (
  id                            bigint auto_increment not null,
  file_name                     varchar(255),
  name                          varchar(255),
  owner_id                      bigint,
  date                          timestamp,
  constraint pk_project primary key (id)
);

create table project_members (
  id                            bigint auto_increment not null,
  user_id                       bigint,
  project_id                    bigint,
  permissions_id                bigint,
  constraint pk_project_members primary key (id)
);

create table repo_file (
  id                            bigint auto_increment not null,
  type_file_id                  bigint,
  project_id                    bigint,
  path                          varchar(1000),
  name                          varchar(255),
  directory_id                  bigint,
  date                          timestamp,
  constraint pk_repo_file primary key (id)
);

create table type_file (
  id                            bigint auto_increment not null,
  type_name                     varchar(255),
  constraint pk_type_file primary key (id)
);

create table user_group (
  id                            bigint auto_increment not null,
  type_name                     varchar(255),
  constraint pk_user_group primary key (id)
);

create table users (
  id                            bigint auto_increment not null,
  username                      varchar(255),
  email                         varchar(255),
  password_hash                 varchar(255),
  confirmation_token            varchar(255),
  date_creation                 timestamp,
  validated                     boolean,
  user_group_id                 bigint,
  constraint uq_users_username unique (username),
  constraint uq_users_email unique (email),
  constraint pk_users primary key (id)
);

create table version_file (
  id                            bigint auto_increment not null,
  file_id                       bigint,
  old_version_id                bigint,
  file_name                     varchar(255),
  date                          timestamp,
  constraint pk_version_file primary key (id)
);

alter table auth_token add constraint fk_auth_token_user_id foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_auth_token_user_id on auth_token (user_id);

alter table feedback add constraint fk_feedback_user_id foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_feedback_user_id on feedback (user_id);

alter table project add constraint fk_project_owner_id foreign key (owner_id) references users (id) on delete restrict on update restrict;
create index ix_project_owner_id on project (owner_id);

alter table project_members add constraint fk_project_members_user_id foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_project_members_user_id on project_members (user_id);

alter table project_members add constraint fk_project_members_project_id foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_project_members_project_id on project_members (project_id);

alter table project_members add constraint fk_project_members_permissions_id foreign key (permissions_id) references permissions (id) on delete restrict on update restrict;
create index ix_project_members_permissions_id on project_members (permissions_id);

alter table repo_file add constraint fk_repo_file_type_file_id foreign key (type_file_id) references type_file (id) on delete restrict on update restrict;
create index ix_repo_file_type_file_id on repo_file (type_file_id);

alter table repo_file add constraint fk_repo_file_project_id foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_repo_file_project_id on repo_file (project_id);

alter table repo_file add constraint fk_repo_file_directory_id foreign key (directory_id) references repo_file (id) on delete restrict on update restrict;
create index ix_repo_file_directory_id on repo_file (directory_id);

alter table users add constraint fk_users_user_group_id foreign key (user_group_id) references user_group (id) on delete restrict on update restrict;
create index ix_users_user_group_id on users (user_group_id);

alter table version_file add constraint fk_version_file_file_id foreign key (file_id) references repo_file (id) on delete restrict on update restrict;
create index ix_version_file_file_id on version_file (file_id);

alter table version_file add constraint fk_version_file_old_version_id foreign key (old_version_id) references version_file (id) on delete restrict on update restrict;
create index ix_version_file_old_version_id on version_file (old_version_id);


# --- !Downs

alter table auth_token drop constraint if exists fk_auth_token_user_id;
drop index if exists ix_auth_token_user_id;

alter table feedback drop constraint if exists fk_feedback_user_id;
drop index if exists ix_feedback_user_id;

alter table project drop constraint if exists fk_project_owner_id;
drop index if exists ix_project_owner_id;

alter table project_members drop constraint if exists fk_project_members_user_id;
drop index if exists ix_project_members_user_id;

alter table project_members drop constraint if exists fk_project_members_project_id;
drop index if exists ix_project_members_project_id;

alter table project_members drop constraint if exists fk_project_members_permissions_id;
drop index if exists ix_project_members_permissions_id;

alter table repo_file drop constraint if exists fk_repo_file_type_file_id;
drop index if exists ix_repo_file_type_file_id;

alter table repo_file drop constraint if exists fk_repo_file_project_id;
drop index if exists ix_repo_file_project_id;

alter table repo_file drop constraint if exists fk_repo_file_directory_id;
drop index if exists ix_repo_file_directory_id;

alter table users drop constraint if exists fk_users_user_group_id;
drop index if exists ix_users_user_group_id;

alter table version_file drop constraint if exists fk_version_file_file_id;
drop index if exists ix_version_file_file_id;

alter table version_file drop constraint if exists fk_version_file_old_version_id;
drop index if exists ix_version_file_old_version_id;

drop table if exists auth_token;

drop table if exists feedback;

drop table if exists permissions;

drop table if exists project;

drop table if exists project_members;

drop table if exists repo_file;

drop table if exists type_file;

drop table if exists user_group;

drop table if exists users;

drop table if exists version_file;

