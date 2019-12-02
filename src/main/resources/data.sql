DROP TABLE IF EXISTS USER;

CREATE TABLE USER (
  id LONG AUTO_INCREMENT  PRIMARY KEY,
  user_id VARCHAR(50) NOT NULL,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(120) NOT NULL,
  email VARCHAR(250) UNIQUE,
  encrypted_password VARCHAR(250),
  email_verification_token VARCHAR(250),
  email_verification_status CHAR
);

INSERT INTO USER VALUES(
	1,
	'HbuKBuPp9cG2uh09KJDgVjQWfwjsdQgXjRVhfrFQltdGc2Oi', 
	'Manu', 
	'Macroun', 
	'test@test.com', 
	'$2a$10$CHHuY2raACFTG9ZZvFx/F.f5ct1VmlU7hai8yKr9Qnszo3pOAWOjy', --password
	NULL,
	false
);