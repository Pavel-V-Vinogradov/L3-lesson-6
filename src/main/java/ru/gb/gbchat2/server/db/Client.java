package ru.gb.gbchat2.server.db;

/**
 * Пользователь чата
 */
public class Client {

    private Integer id;
    private final String login;
    private final String password;
    private final String nick;

    public Client(String login, String password, String nick) {
        this.login = login;
        this.password = password;
        this.nick = nick;
    }

    public Client(Integer id, String login, String password, String nick) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.nick = nick;
    }

    public Integer getId() { return id; }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getNick() {
        return nick;
    }

}
