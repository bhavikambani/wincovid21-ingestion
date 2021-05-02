create table user_actions_audit (
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    resource_id int NOT NULL,
    feedback_type varchar(512) NOT NULL,
	updated_on DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

create table state_details (
	`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`city_name` varchar(1024) NOT NULL,
	`icon_path` varchar(1024) NOT NULL,
	updated_on DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

create table city_details (
	`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`city_name` varchar(1024) NOT NULL,
	`state_id` int(11) NOT NULL,
	`icon_path` varchar(1024) NOT NULL,
	updated_on DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT FOREIGN KEY (state_id)
  REFERENCES state_details(id)
  ON DELETE CASCADE
  ON UPDATE CASCADE
);




---- Basic insert queries to setup DB
insert into state_details (state_name, icon_path) values ("Maharastra", "abc.png");
insert into state_details (state_name, icon_path) values ("Gujarat", "abc.png");
insert into state_details (state_name, icon_path) values ("Delhi", "abc.png");

insert into city_details (state_id, city_name, icon_path) values (1, "Ahmeadbad", "abc.png");
insert into city_details (state_id, city_name, icon_path) values (1, "Rajkot", "abc.png");
insert into city_details (state_id, city_name, icon_path) values (2, "Pune", "abc.png");
insert into city_details (state_id, city_name, icon_path) values (2, "Mumbai", "abc.png");
insert into city_details (state_id, city_name, icon_path) values (3, "Delhi", "abc.png");