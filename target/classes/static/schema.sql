create table user_actions_audit (
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    resource_id int NOT NULL,
    feedback_type varchar(512) NOT NULL,
	updated_on DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

