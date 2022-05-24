package ru.gb.gbchat2.server.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DbHandler {

    private Connection connection;

    public void connect() throws SQLException {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:sqlite.db");
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.printf("Database connected:  %s %s, %s\n",
                    metaData.getDatabaseProductName(),
                    metaData.getDriverVersion(),
                    metaData.getURL()
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создание схемы базы данных (таблицы, индексы, ...)
     */
    public void createTables() {
        try {
            Statement ddlStatement = connection.createStatement();
            ddlStatement.executeUpdate(
                    //@formatter:off
                "CREATE TABLE IF NOT EXISTS client (" +
                   " id        INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT," +
                   " login     TEXT     NOT NULL," +
                   " password  TEXT     NOT NULL," +
                   " nick      TEXT     NOT NULL" +
                ");");
                //@formatter:on
            ddlStatement.executeUpdate("CREATE UNIQUE INDEX IF NOT EXISTS udx_client_login ON client(login);");
            ddlStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Table \"client\" created or exists\n");
    }

    public void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
            System.out.println("database disconnected\n");
        }
    }

    /**
     * Поиск клиента в базе по логину
     *
     * @param login - критерий поиска
     * @return опционально - объект Client
     */
    public Optional<Client> search(String login) {
        Optional<Client> client = Optional.empty();
        try (PreparedStatement query = connection.prepareStatement(
                "SELECT id, login, password, nick" +
                        " FROM client" +
                        " WHERE login = ? " +
                        " LIMIT 1;")) {
            query.setString(1, login);
            try (ResultSet rs = query.executeQuery()) {
                if (rs.next()) {
                    client = Optional.of(new Client(
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return client;
    }

    /**
     * Смена ника пользователя TODO: доработать UI
     * @param login   - для какого логина меняем
     * @param newNick - новый ник
     */
    public void updateNick(String login, String newNick) {
        update(new Client(null, login, null, newNick));
    }

    /**
     * Смена пароля пользователя TODO: доработать UI
     * @param login       - для какого логина меняем
     * @param newPassword - новый пароль
     */
    public void setNewPassword(String login, String newPassword) {
        update(new Client(null, login, newPassword, null));
    }

    /**
     * обновление параметров клиента(пароль, ник)
     *
     * @param client - для идентификации клиента обязательно указать или id иди login
     *               description: если параметр задан, он будет обновлён, если нет - останется прежним
     */
    private void update(Client client) {
        String sql =
                //@formatter:off
                "UPDATE client" +
                  " SET password = IFNULL(?, password)," +
                      " nick     = IFNULL(?, nick)" +
                " WHERE 1 = 1";
               //@formatter:on

        boolean searchById = (client.getId() != null);
        boolean searchByLogin = (client.getLogin() != null);

        if (searchById) {
            sql = sql + " AND id = ?";
        } else if (searchByLogin) {
            sql = sql + " AND login = ?";
        }
        try (PreparedStatement prepareUpdate = connection.prepareStatement(sql)) {
            prepareUpdate.setString(1, client.getPassword());
            prepareUpdate.setString(2, client.getNick());
            if (searchById) {
                prepareUpdate.setInt(3, client.getId());
            } else if (searchByLogin) {
                prepareUpdate.setString(3, client.getLogin());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initialDataBase() {
        createTables();
        try {
            addClient(getClientList());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Регистрация пользователей в базе списком
     *
     * @param client - список подготовленных данных по пользователям
     */
    public void addClient(List<Client> client) throws SQLException {
        try (PreparedStatement prepareInsert = connection.prepareStatement(
                "INSERT INTO client (login, password, nick)" +
                        "SELECT ?, ?, ?" +
                        "WHERE NOT EXISTS(SELECT 1 FROM client WHERE login = ?);")) {
            for (Client c : client) {
                prepareInsert.setString(1, c.getLogin());
                prepareInsert.setString(2, c.getPassword());
                prepareInsert.setString(3, c.getNick());
                prepareInsert.setString(4, c.getLogin());
                prepareInsert.addBatch();
            }
            prepareInsert.executeBatch();
            System.out.println("register clients: " + client.stream()
                    .map(Client::getLogin)
                    .collect(Collectors.toList())
            );
        }
    }

    /**
     * Генерация пользователей
     *
     * @return список зарегистрированных пользователей
     */
    public List<Client> getClientList() {
        List<Client> clients = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            clients.add(new Client("login" + i, "pass" + i, "nick" + i));
        }
        return clients;
    }
}
