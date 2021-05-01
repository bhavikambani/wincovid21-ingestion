create table covid_ingestion.user_actions (
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    resourceId int NOT NULL,
    feedback_type varchar(512) NOT NULL,
	updated_on DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

