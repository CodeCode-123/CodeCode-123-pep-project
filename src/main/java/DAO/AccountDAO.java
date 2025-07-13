package DAO;

import Model.Account;
import Util.ConnectionUtil;

import java.sql.*;

public class AccountDAO {
    /**
     * 1: Our API should be able to process new User registrations.
     * As a user, I should be able to create a new Account on the endpoint POST localhost:8080/register.
     * The body will contain a representation of a JSON Account, but will not contain an account_id.
     *
     * The registration will be successful if and only if the username is not blank,
     * the password is at least 4 characters long, and an Account with that username does not already exist.
     * If all these conditions are met, the response body should contain a JSON of the Account,
     * including its account_id. The response status should be 200 OK, which is the default.
     * The new account should be persisted to the database.
     * If the registration is not successful, the response status should be 400. (Client error)
     *
     * create table account (
     *     account_id int primary key auto_increment,
     *     username varchar(255) unique,
     *     password varchar(255)
     * );
     */
    public Account createAccount(Account account) {
        // TODO: implementation
        Connection connection = ConnectionUtil.getConnection();
        try {
            String username = account.getUsername();
            String password = account.getPassword();
            // Check if the username existed in the database
            String sqlSearch = "SELECT * FROM account WHERE username=?;";
            PreparedStatement psSearch = connection.prepareStatement(sqlSearch);
            psSearch.setString(1, username);
            ResultSet rsSearch = psSearch.executeQuery();
            if (rsSearch.next() && rsSearch.getString("username").equals(username)) {
                // username existed
                return null;
            }

            // Create a new account
            String sql = "INSERT INTO account (username, password) VALUES (?, ?);";
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (username != null && username.length() > 0 && password != null && password.length() >= 4) {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int generated_account_id = rs.getInt("account_id");
                    return new Account(generated_account_id, account.getUsername(), account.getPassword());
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * 2: Our API should be able to process User logins.
     * As a user, I should be able to verify my login on the endpoint POST localhost:8080/login.
     * The request body will contain a JSON representation of an Account, not containing an account_id.
     * In the future, this action may generate a Session token to allow the user to securely use the site.
     * We will not worry about this for now.
     * <p>
     * The login will be successful if and only if the username and password provided in the request
     * body JSON match a real account existing on the database. If successful,
     * the response body should contain a JSON of the account in the response body,
     * including its account_id. The response status- should be 200 OK, which is the default.
     * If the login is not successful, the response status should be 401. (Unauthorized)
     */
    public Account verifyAccount(Account account) {
        // TODO: implementation
        Connection connection = ConnectionUtil.getConnection();
        try {
            String username = account.getUsername();
            String password = account.getPassword();
            String sql = "SELECT * FROM account WHERE username=? AND password=?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Account retrievedAccount = new Account(rs.getInt("account_id"),
                        rs.getString("username"), rs.getString("password"));
                return retrievedAccount;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}