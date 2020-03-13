CREATE TABLE launch (
	id VARCHAR(100) PRIMARY KEY,
	dateOfLaunch DATETIME NOT NULL,
	type VARCHAR(100) NOT NULL,
	location VARCHAR(500) NOT NULL,
	description VARCHAR(100),
	employeeCpf VARCHAR(200) NOT NULL,
	FOREIGN KEY (employeeCpf) REFERENCES employee(cpf)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--INSERT INTO launch (dateOfLaunch, type, location,  description, id_employee) values ('2020-01-16 09:00:00', 'START_WORK', 'Uberlândia, shopping', null, 1);
--INSERT INTO launch (dateOfLaunch, type, location,  description, id_employee) values ('2020-01-16 12:00:00', 'START_LUNCH', 'Uberlândia, shopping', null, 1);
--INSERT INTO launch (dateOfLaunch, type, location,  description, id_employee) values ('2020-01-16 13:00:00', 'END_LUNCH', 'Uberlândia, shopping', null, 1);
--INSERT INTO launch (dateOfLaunch, type, location,  description, id_employee) values ('2020-01-16 15:00:00', 'END_WORK', 'Uberlândia, shopping', null, 1);
--
--INSERT INTO launch (dateOfLaunch, type, location,  description, id_employee) values ('2020-01-17 08:33:00', 'START_WORK', 'Uberlândia, shopping', null, 1);
--INSERT INTO launch (dateOfLaunch, type, location,  description, id_employee) values ('2020-01-17 11:31:00', 'START_LUNCH', 'Uberlândia, shopping', null, 1);
--INSERT INTO launch (dateOfLaunch, type, location,  description, id_employee) values ('2020-01-17 12:15:00', 'END_LUNCH', 'Uberlândia, shopping', null, 1);
--INSERT INTO launch (dateOfLaunch, type, location,  description, id_employee) values ('2020-01-17 18:00:00', 'END_WORK', 'Uberlândia, shopping', null, 1);