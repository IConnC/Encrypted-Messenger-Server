SET @db = ?;

CREATE DATABASE @encrypticator_database_name;

SET @q = CONCAT('CREATE TABLE ', @db, '.accounts (
	user_identifier bigint(19) NOT NULL,
	username tinytext NOT NULL,
	hashed_password text(65535) NOT NULL,
	epoch_registered bigint(19) NOT NULL,
	account_type tinyint(3) NOT NULL,
	PRIMARY KEY (user_identifier)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;');



CREATE TABLE @encrypticator_database_name.channel_members (
	channel_identifier bigint(19) DEFAULT NULL,
	user_identifier bigint(19) DEFAULT NULL,
	join_date bigint(19) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE @encrypticator_database_name.channels (
	channel_identifier bigint(19) NOT NULL,
	channel_name text(65535) NOT NULL,
	creation_date bigint(19) NOT NULL,
	PRIMARY KEY (channel_identifier)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE @encrypticator_database_name.messages (
	message_identifier bigint(19) NOT NULL,
	channel_sent_id bigint(19) NOT NULL,
	author_identifier bigint(19) NOT NULL,
	message_contents varchar(4369) NOT NULL,
	date_sent bigint(19) NOT NULL,
	PRIMARY KEY (message_identifier)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

PREPARE stmt FROM @q;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;


ALTER TABLE @encrypticator_database_name.channel_members
	ADD FOREIGN KEY (channel_identifier) 
	REFERENCES Encrypicator_Server_Test_1.channels (channel_identifier) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE @encrypticator_database_name.channel_members
	ADD FOREIGN KEY (user_identifier) 
	REFERENCES Encrypicator_Server_Test_1.accounts (user_identifier) ON DELETE CASCADE ON UPDATE CASCADE;


ALTER TABLE Encrypicator_Server_Test_1.messages
	ADD FOREIGN KEY (channel_sent_id) 
	REFERENCES Encrypicator_Server_Test_1.channels (channel_identifier) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE Encrypicator_Server_Test_1.messages
	ADD FOREIGN KEY (author_identifier) 
	REFERENCES Encrypicator_Server_Test_1.accounts (user_identifier) ON UPDATE CASCADE;


