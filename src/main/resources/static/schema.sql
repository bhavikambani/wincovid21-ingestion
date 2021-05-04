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
    category_id INT NOT NULL,
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
	`state_name` varchar(1024) NOT NULL,
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


create table resource_sub_category (
	`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`sub_category_name` varchar(1024) NOT NULL,
	`category_id` int(11) NOT NULL,
	updated_on DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT FOREIGN KEY (category_id)
  REFERENCES resource_category(id)
  ON DELETE CASCADE
  ON UPDATE CASCADE
);


--- USER
create table user_type (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        user_type VARCHAR(128) NOT NULL UNIQUE,
        created_on DATETIME DEFAULT CURRENT_TIMESTAMP,
        updated_on DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


create table user_details (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(128) NOT NULL,
        user_name VARCHAR(128) NOT NULL UNIQUE,
        password VARCHAR(128) NOT NULL,
        user_type INT NOT NULL,
        created_on DATETIME DEFAULT CURRENT_TIMESTAMP,
       	updated_on DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  CONSTRAINT FOREIGN KEY (user_type)
  REFERENCES `user_type`(id)
  ON DELETE CASCADE
  ON UPDATE CASCADE
);


create table user_session (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        token_id VARCHAR(128) NOT NULL UNIQUE,
        user_id INT NOT NULL,
        created_on DATETIME DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT FOREIGN KEY (user_id)
  REFERENCES user_details(id)
  ON DELETE CASCADE
  ON UPDATE CASCADE
);

INSERT INTO user_type (user_type) values ("Visitors");
INSERT INTO user_type (user_type) values ("Volunteer");

INSERT INTO user_details (name, user_name, password,user_type) values ("Bhavik Ambani", "bhavik123", "bhavikp1",1);
INSERT INTO user_details (name, user_name, password,user_type) values ("Myntra CC", "myntracc", "myntracc",1);
INSERT INTO user_details (name, user_name, password,user_type) values ("Admin User", "admin", "admin123",2);



--------

CREATE TABLE `feedback_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `feedback_code` varchar(1024) NOT NULL,
  `feedback_message` varchar(1024) NOT NULL,
  `verification_status` varchar(128) NOT NULL,
  `availability_status` varchar(128) NOT NULL,
  `updated_on` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE INDEX feedback_types_verification_status ON feedback_types(verification_status);
CREATE INDEX feedback_types_availability_status ON feedback_types(availability_status);

CREATE TABLE `user_allowed_feedback_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_type`int(11) NOT NULL ,
  `feedback_type` int(11) NOT NULL ,
  `updated_on` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),

 CONSTRAINT FOREIGN KEY (user_type)
  REFERENCES user_type(id)
  ON DELETE CASCADE
  ON UPDATE CASCADE,

CONSTRAINT FOREIGN KEY (feedback_type)
  REFERENCES `feedback_types`(id)
  ON DELETE CASCADE
  ON UPDATE CASCADE
);

CREATE INDEX user_allowed_feedback_types_user_id ON user_allowed_feedback_types(user_id);
CREATE INDEX user_allowed_feedback_feedback_type ON user_allowed_feedback_types(feedback_type);
ALTER TABLE user_allowed_feedback_types ADD CONSTRAINT user_allowed_feedback_types_uniqueue_mapping UNIQUE (user_type,feedback_type);


insert into resource_category (category_name, icon_name) values  ('Oxygen', 'Oxygen');
insert into resource_category (category_name, icon_name) values  ('hospital', 'Bed');
insert into resource_category (category_name, icon_name) values  ('medicine', 'Injection');
insert into resource_category (category_name, icon_name) values  ('Medical Support', 'Ambulance');


insert into resource_sub_category (sub_category_name, category_id, icon_name) values ('refill', 1 , 'icon1');
insert into resource_sub_category (sub_category_name, category_id, icon_name) values ('cylinder', 1 , 'icon1');
insert into resource_sub_category (sub_category_name, category_id, icon_name) values ('concentrator', 1 , 'icon1');
insert into resource_sub_category (sub_category_name, category_id, icon_name) values ('cannula kits', 1 , 'icon1');


insert into resource_sub_category (sub_category_name, category_id, icon_name) values ('accepts_covid', 5, 'icon1');


insert into resource_sub_category (sub_category_name, category_id, icon_name) values ('Remdesivir', 6 , 'icon1');
insert into resource_sub_category (sub_category_name, category_id, icon_name) values ('Fabiflu', 6 , 'icon1');
insert into resource_sub_category (sub_category_name, category_id, icon_name) values ('Favipiravir', 6 , 'icon1');
insert into resource_sub_category (sub_category_name, category_id, icon_name) values ('Tocilizumab', 6 , 'icon1');
insert into resource_sub_category (sub_category_name, category_id, icon_name) values ('Bevacizumab', 6 , 'icon1');

insert into resource_sub_category (sub_category_name, category_id, icon_name) values ('Fever Clinic ', 7 , 'icon1');
insert into resource_sub_category (sub_category_name, category_id, icon_name) values ('Doctors', 7 , 'icon1');
insert into resource_sub_category (sub_category_name, category_id, icon_name) values ('Pharmacies', 7 , 'icon1');



insert into feedback_types (feedback_code, feedback_message,verification_status,availability_status) values ('C1', 'Verified','VERIFIED','AVAILABLE');
insert into feedback_types (feedback_code, feedback_message,verification_status,availability_status) values ('C2', 'Available','VERIFIED','AVAILABLE');
insert into feedback_types (feedback_code, feedback_message,verification_status,availability_status) values ('C3', 'Unavailable','VERIFIED','UNAVAILABLE');
insert into feedback_types (feedback_code, feedback_message,verification_status,availability_status) values ('C3', 'Unavailable','VERIFIED','UNAVAILABLE');


---- Basic insert queries to setup DB
insert into state_details (state_name, icon_path) values ("Maharastra", "abc.png");
insert into state_details (state_name, icon_path) values ("Gujarat", "abc.png");
insert into state_details (state_name, icon_path) values ("Delhi", "abc.png");

insert into city_details (state_id, city_name, icon_path) values (1, "Ahmeadbad", "abc.png");
insert into city_details (state_id, city_name, icon_path) values (1, "Rajkot", "abc.png");
insert into city_details (state_id, city_name, icon_path) values (2, "Pune", "abc.png");
insert into city_details (state_id, city_name, icon_path) values (2, "Mumbai", "abc.png");
insert into city_details (state_id, city_name, icon_path) values (3, "Delhi", "abc.png");
