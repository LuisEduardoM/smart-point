CREATE TABLE employee (
    cpf VARCHAR(11) PRIMARY KEY,
	id VARCHAR(200) NOT NULL,
	name VARCHAR(200) NOT NULL,
	email VARCHAR(200) NOT NULL,
	password VARCHAR(15) NOT NULL,
	idCompany VARCHAR(200) NOT NULL,
	FOREIGN KEY (idCompany) REFERENCES company(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--INSERT INTO employee (name, email, password, cpf, id_company) values ('Luis', 'luis.marques@zup.com.br', '123456', '01234567890', 1);