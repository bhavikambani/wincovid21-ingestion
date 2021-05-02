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


