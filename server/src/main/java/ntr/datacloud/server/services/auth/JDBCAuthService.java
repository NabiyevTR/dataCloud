package ntr.datacloud.server.services.auth;

import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.sql.*;

@Log4j
public class JDBCAuthService implements AuthService {


    private static final String URL = System.getenv("AUTH_DB_URL");
    private static final String USER = System.getenv("AUTH_DB_USER");
    private static final String PASS = System.getenv("AUTH_DB_PASS");

    private static final String sqlInsertUser = "INSERT INTO users (login, password) VALUES  (?, ?)";
    private static final String sqlSelectLoginByLogin = "SELECT login FROM users WHERE login=?";
    private static final String sqlSelectLoginByLoginAndPass = "SELECT login FROM users WHERE login=? AND password=?";


    private Connection connection;
    private ResultSet resultSet;

    private static AuthService INSTANCE;

    public static AuthService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JDBCAuthService();
        }
        return INSTANCE;
    }

    public JDBCAuthService() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            log.error("Cannot find driver fo database: ", e);
        }
        try {
            connection = DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException throwables) {
            log.error("Cannot connect to database: ", throwables);
        }
        log.info("Auth service started successfully.");

    }

    @Override
    public void terminate() {
        try {
            resultSet.close();
        } catch (SQLException e) {
            log.error("Failed to close ResultSet.", e);
        }
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("Failed to close connection.", e);
        }
        INSTANCE = null;
        log.info("Auth service was terminated.");
    }


    @Override
    public boolean registration(String login, String password) {
        if (containsUser(login)) {
            log.info(String.format("User %s was not added to database. User with this login already exists", login));
            return false;
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertUser)) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            preparedStatement.addBatch();
            preparedStatement.executeBatch();
            log.info(String.format("User %s was added to database successfully.", login));

        } catch (SQLException e) {
            log.error(String.format("Failed to add user %s to database due to error.", login), e);
            return false;
        }
        return true;
    }

    @Override
    public boolean userExists(String login, String password) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlSelectLoginByLoginAndPass)) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
            if (resultSet == null) return false;
            while (resultSet.next()) {
                if (login.equals(resultSet.getString("login"))) return true;
            }
        } catch (SQLException e) {
            log.error(String.format("Failed to find user %s with required password due to error.", login), e);
            return false;
        }
        return false;
    }


    @Override
    public boolean changePass(String login, String oldPassword, String newPassword) {
        log.error("Unsupported operation: change password");
        return false;
    }


    private boolean containsUser(String login) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlSelectLoginByLogin)) {
            preparedStatement.setString(1, login);
            resultSet = preparedStatement.executeQuery();
            if (resultSet == null) return false;
            while (resultSet.next()) {
                if (login.equals(resultSet.getString("login"))) return true;
            }
        } catch (SQLException e) {
            log.error(String.format("Failed to find user %s due to error.", login), e);
            return false;
        }
        return false;
    }




}
