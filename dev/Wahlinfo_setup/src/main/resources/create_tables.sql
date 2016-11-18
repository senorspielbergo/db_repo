create table partei (
    name varchar(40) primary key
);

create table bundesland (
    name varchar(30) primary key,
    wahlberechtigte int(20) not null
);

create table bewerber (
    id int(30) primary key,
    titel varchar(30),
    vorname varchar(50) not null,
    nachname varchar(60) not null,
    partei varchar(40) references partei(name)
);

create table landesliste (
    id int(30) primary key,
    wahljahr int(4) not null,
    partei varchar(40) references partei(name),
    bundesland varchar(30) references bundesland(name)
);

create table listenplaetze (
    listen_id int(30) references landesliste(id),
    bewerber_id int(30) references bewerber(id),
    listenplatz int(4) not null,
    primary key (listen_id, bewerber_id)
);

create table wahlkreis (
    nummer int(4) primary key,
    name varchar(40) not null,
    wahlberechtigte int(20) not null,
    bundesland varchar(30) references bundesland(name)
);

create table wahlbezirk (
    nummer int(4) primary key,
    name varchar(40) not null,
    wahlkreis int(4) references wahlkreis(nummer)
);

create table stimmzettel (
    id int(30) primary key,
    direktkandidat int(30) references bewerber(id),
    landesliste int(30) references landesliste(id),
    wahlbezirk int(4) references wahlbezirk(nummer)
);

create table waehler (
    id int(30) primary key,
    wahlbezirk int(4) references wahlbezirk(nummer),
    _alter int(3) not null,
    isMaennlich tinyint(1)
);