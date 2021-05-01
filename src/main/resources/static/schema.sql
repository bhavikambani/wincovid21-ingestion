create table user_actions_audit (
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    resource_id int NOT NULL,
    feedback_type varchar(512) NOT NULL,
	updated_on DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

create table resource_details (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(512) NOT NULL,
    address VARCHAR(512) NOT NULL,
    pin_code BIGINT(12) NOT NULL,
    description VARCHAR(512),
    phone_1 VARCHAR(20) NOT NULL,
    phone_2 VARCHAR(20),
    email VARCHAR(120),
    district VARCHAR(120) NOT NULL,
    state VARCHAR(120) NOT NULL,
    quantity_available INT,
    price DOUBLE,
    created_by VARCHAR(120),
	created_on BIGINT(20),
	is_verified BOOLEAN
);

create table resource_availability_details (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    resource_id INT NOT NULL,
    category VARCHAR(120) NOT NULL,
    resource_type VARCHAR(120) NOT NULL,
    is_available BOOLEAN,
    CONSTRAINT FK_resource FOREIGN KEY (resource_id)
    REFERENCES resource_details(id)
);


