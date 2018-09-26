package services;

import constants.DBTables;
import entity.User;
import helpers.DBHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public final class AuthorizationService {
    private static AuthorizationService instance = null;

    private AuthorizationService(){}

    public static AuthorizationService getInstance(){
        if(instance == null) {
            instance = new AuthorizationService();

            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return instance;
    }

    public void addUser(User user) throws SQLException {
        if(isExist(user.getMail())){
            return;
        }
        DBHelper.addUser(user);
    }

    public boolean logOut(String sessionId) throws SQLException{
        Connection connection = DBHelper.getConnection();
        Statement statement = connection.createStatement();
        int res = statement.executeUpdate("DELETE FROM " + DBTables.SESSIONS + " WHERE session_id='" + sessionId + "';");

        connection.close();

        return res > 0;
    };

    public User getUserBySessionId(String id) throws SQLException {
        String userEmail = "";
        String userPassword = "";
        long userRegistrationDate = 0L;
        Connection connection = DBHelper.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + DBTables.USERS +
                " LEFT OUTER JOIN " + DBTables.SESSIONS + " ON (" + DBTables.USERS + ".id = " + DBTables.SESSIONS + ".user_id) " +
                " WHERE session_id='" + id + "';");

        if(resultSet.next()){
            userEmail = resultSet.getString("email");
            userPassword = resultSet.getString("password");
            userRegistrationDate = resultSet.getBigDecimal("registration_date").longValue();
        }

        resultSet.close();
        connection.close();

        if(!userEmail.equals("") && !userPassword.equals("") && userRegistrationDate != 0L){
            return new User(userEmail, userPassword, userRegistrationDate);
        }

        return null;
    }

    private int rememberSession(String sessionId, User user) throws SQLException {
        Connection connection = DBHelper.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT id FROM " + DBTables.USERS + " WHERE email='" + user.getMail() + "';");
        int userId = -1;
        int res = 0;

        if(resultSet.next()){
            userId = resultSet.getInt("id");
        }

        if(userId > 0) {
            res = statement.executeUpdate("INSERT INTO " + DBTables.SESSIONS + " (session_id, user_id) VALUES ('" + sessionId + "', " + userId + ")");
        }

        resultSet.close();
        connection.close();

        return res;
    }

    public boolean isAuthorized(String sessionId) throws SQLException{
        Connection connection = DBHelper.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(session_id) FROM " + DBTables.SESSIONS + " WHERE session_id='" + sessionId + "';");
        boolean authorized = false;
        if(resultSet.next() && resultSet.getBigDecimal(1).intValue() > 0){
            authorized =  true;
        }

        resultSet.close();
        connection.close();

        return authorized;
    }

    public boolean isExist(String email) throws SQLException {
        String sql = "SELECT COUNT(email) FROM " + DBTables.USERS + " WHERE email='" + email + "';";
        Connection connection = DBHelper.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        boolean exist = false;

        if(resultSet.next() && resultSet.getBigDecimal(1).intValue() > 0){
            exist = true;
        }

        resultSet.close();
        connection.close();

        return exist;
    }

    public boolean authorize(String email, String password, String sessionID) throws SQLException{
        User user;

        if(!isExist(email)){
            return false;
        }

        user = DBHelper.getUser(email);
        if(user == null || !user.getPassword().equals(password)){
            return false;
        }

        rememberSession(sessionID, user);

        return true;
    }
}
