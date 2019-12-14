DROP TABLE IF EXISTS PASSWORD_RESET_TOKEN;
DROP TABLE IF EXISTS ADDRESS;
DROP TABLE IF EXISTS USER;

CREATE TABLE USER (
  id LONG IDENTITY PRIMARY KEY,
  user_id VARCHAR(50) NOT NULL,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(120) NOT NULL,
  email VARCHAR(250) UNIQUE,
  encrypted_password VARCHAR(250),
  email_verification_token VARCHAR(250),
  email_verification_status CHAR
);

CREATE TABLE ADDRESS (
  id LONG IDENTITY PRIMARY KEY,
  address_id VARCHAR(50) NOT NULL,
  city VARCHAR(50) NOT NULL,
  country VARCHAR(50) NOT NULL,
  street_name VARCHAR(250),
  postal_code VARCHAR(10) NOT NULL,
  type VARCHAR(10) NOT NULL,
  user_id VARCHAR(50) NOT NULL
);

CREATE TABLE PASSWORD_RESET_TOKEN (
  id LONG IDENTITY PRIMARY KEY,
  token VARCHAR(250) NOT NULL,
  user_id VARCHAR(50) NOT NULL
);

INSERT INTO USER (user_id,first_name,last_name,email,encrypted_password,email_verification_token,email_verification_status)	 VALUES(
	'HbuKBuPp9cG2uh09KJDgVjQWfwjsdQgXjRVhfrFQltdGc2Oi', 
	'Manu', 
	'Macroun', 
	'test@test.com', 
	'$2a$10$CHHuY2raACFTG9ZZvFx/F.f5ct1VmlU7hai8yKr9Qnszo3pOAWOjy', --password
	NULL,
	true
);

INSERT INTO USER (user_id,first_name,last_name,email,encrypted_password,email_verification_token,email_verification_status) VALUES(
	'1wMVV0F5jla0ZmtjedfpMwYAJaDajtcGAldtC8IrNZbTVnJi', 
	'Gégé', 
	'Ladébrouille', 
	'test2@test.com', 
	'$2a$10$v8.x/1e4svu/gDvZLY7Rh.2WFgluK0Nsg19VIELxLGnRISWiUshW2', --password
	NULL,
	true
);

INSERT INTO ADDRESS (address_id, city, country, street_name, postal_code, type, user_id) VALUES(
	'dwNYBEDUKEoctW1BHxGoMirJuOjewlQRm6og5abbaRIWN5NW',
	'London',
	'Great britain',
	NULL,
	'123',
	'shipping',
	'1'
);

INSERT INTO ADDRESS (address_id, city, country, street_name, postal_code, type, user_id) VALUES(
	'jz3e6SLWo7vSAKZ7ZqgCdCUr6qGa7SUnjM2jQEU58rcLqizD',
	'Madrid',
	'Espana',
	'Calle corazon',
	'123',
	'billing',
	'1'
);
