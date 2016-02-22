CREATE DATABASE IF NOT EXISTS anesis CHARACTER SET utf8 COLLATE utf8_general_ci;;
USE anesis;
CREATE TABLE rooms (
	_id 			INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	room 			CHAR(2) NOT NULL,
	arrival			DATE NOT NULL,
	departure 		DATE NOT NULL,
	quantity		TINYINT NOT NULL,
	contact_name	VARCHAR(30) NOT NULL,
	phone			CHAR(10) NOT NULL	
);

CREATE USER 'anesisdba'@'localhost' IDENTIFIED BY '12345';

GRANT ALL ON anesis.rooms TO anesisdba WITH GRANT OPTION;