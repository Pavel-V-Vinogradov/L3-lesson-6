package ru.gb.gbchat2.server;

import ru.gb.gbchat2.server.db.Client;
import ru.gb.gbchat2.server.db.DbHandler;

import java.sql.SQLException;

public class DbAuthService implements AuthService {

    DbHandler dbHandler;

    public DbAuthService() {
        this.dbHandler = new DbHandler();
        run();
    }

    @Override
    public String getNickByLoginAndPassword(String login, String password) {
        return dbHandler.search(login)
                .filter(c -> login.equals(c.getLogin()) && password.equals(c.getPassword()))
                .map(Client::getNick)
                .orElse(null);
    }

    @Override
    public void run() {
        try {
            dbHandler.connect();
            dbHandler.initialDataBase(); //TODO: при переходе на liquibase метод не нужен
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            dbHandler.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
