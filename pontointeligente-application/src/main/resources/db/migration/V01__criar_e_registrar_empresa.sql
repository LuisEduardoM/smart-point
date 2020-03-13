CREATE TABLE company (
	id VARCHAR(200) PRIMARY KEY,
	corporationName VARCHAR(200) NOT NULL,
	cnpj VARCHAR(14) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--INSERT INTO company (corporationName, cnpj) values ('Zup','01234567891234');