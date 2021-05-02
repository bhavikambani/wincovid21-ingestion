create table user_actions_audit (
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    resource_id int NOT NULL,
    feedback_type varchar(512) NOT NULL,
	updated_on DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

create table resource_details (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(1024) NOT NULL,
    address VARCHAR(1024),
    category VARCHAR(120) NOT NULL,
    resource_type VARCHAR(120) NOT NULL,
    pin_code BIGINT(12),
    description VARCHAR(1024),
    phone_1 VARCHAR(40) NOT NULL,
    phone_2 VARCHAR(40),
    email VARCHAR(120),
    city VARCHAR(120) NOT NULL,
    state VARCHAR(120) NOT NULL,
    quantity_available VARCHAR(120) NOT NULL,
    price VARCHAR(512),
    created_by VARCHAR(120),
	created_on BIGINT(20),
	updated_on BIGINT(20),
	is_verified BOOLEAN NOT NULL
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

create table resource_category (
	`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`category_name` varchar(1024) NOT NULL,
	updated_on DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


create table re3source_sub_category (
	`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`sub_category_name` varchar(1024) NOT NULL,
	`category_id` int(11) NOT NULL,
	updated_on DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT FOREIGN KEY (category_id)
  REFERENCES resource_category(id)
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
