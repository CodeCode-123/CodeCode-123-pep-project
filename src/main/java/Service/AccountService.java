package Service;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {
    private AccountDAO accountDAO;

    public AccountService(){
        accountDAO = new AccountDAO();
    }
    
    public AccountService(AccountDAO accountDAO){
        this.accountDAO = accountDAO;
    }

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
     */
    public Account userRegistration(Account account){
        return accountDAO.createAccount(account);
    }

     /**
     * 2: Our API should be able to process User logins.
     * As a user, I should be able to verify my login on the endpoint POST localhost:8080/login.
     * The request body will contain a JSON representation of an Account, not containing an account_id.
     * In the future, this action may generate a Session token to allow the user to securely use the site.
     * We will not worry about this for now.
     *
     * The login will be successful if and only if the username and password provided in the request
     * body JSON match a real account existing on the database. If successful,
     * the response body should contain a JSON of the account in the response body,
     * including its account_id. The response status- should be 200 OK, which is the default.
     * If the login is not successful, the response status should be 401. (Unauthorized)
     */
     public Account userLogin(Account account){
         return accountDAO.verifyAccount(account);
     }
}
