package server;

import java.sql.*;

public class SQLService implements AuthService {
    private static Connection connection;

    private static PreparedStatement psAdd;
    private static PreparedStatement psUpdate;
    private static PreparedStatement psSelect;

    public SQLService() {
        connect();
    }

    public static boolean connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:main.db");
            psSelect = connection.prepareStatement("SELECT nickname FROM users WHERE (login=?) AND (password=?);");
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }


    public static void disconnect() {
        try {
            psAdd.close();
            psSelect.close();
            psUpdate.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        String nickname = null;
        try {
            psSelect.setString(1, login);
            psSelect.setString(2, password);
            ResultSet rs = psSelect.executeQuery();
            if(rs.next()) {
                nickname = rs.getString(1);
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return nickname;
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        try {
            psAdd = connection.prepareStatement("INSERT INTO users (login, password, nickname) VALUES (?, ?, ?);");
            psAdd.setString(1, login);
            psAdd.setString(2, password);
            psAdd.setString(3, nickname);
            psAdd.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateNick(String oldNick, String newNick) {
        try {
            psUpdate = connection.prepareStatement("UPDATE users SET nickname = ? WHERE nickname = ?;");
            psUpdate.setString(1, newNick);
            psUpdate.setString(2, oldNick);
            psUpdate.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }
}
