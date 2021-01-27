create table user ( idUser varchar(255) primary key, name text, country text, city text, offer text, comment text );

create table offer_host ( idOffer varchar(255) primary key, name text, country text, city text, addressHouse text, houseType text, personalMeeting boolean, idUser text, typeOffer text, response text);

create table offer_guest ( idOffer varchar(255) primary key, name text, country text, city text, weightBaggage int, numberDay int, idUser text, typeOffer text, response text );

create table response ( idResponse varchar(255) primary key, idUser text, idOffer text);

create table comment ( idComment varchar(255) primary key, comment text, idUSerFrom text, idUserTo text, rating text);

