DROP TABLE IF EXISTS client;

CREATE TABLE client
(
    id       INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    login    TEXT    NOT NULL,
    password TEXT    NOT NULL,
    nick     TEXT    NOT NULL
);

INSERT INTO client (login, password, nick)
  VALUES ('login0', 'pass0', 'nick0'),
         ('login1', 'pass1', 'nick2'),
         ('login2', 'pass2', 'nick2'),
         ('login3', 'pass3', 'nick3'),
         ('login4', 'pass4', 'nick4');

CREATE UNIQUE INDEX udx_client_login ON client(login);