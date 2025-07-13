package DAO;

import Model.Message;
import Util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MessageDAO {
    /**
     * 3: Our API should be able to process the creation of new messages.
     * As a user, I should be able to submit a new post on the endpoint POST localhost:8080/messages. The request body
     * will contain a JSON representation of a message, which should be persisted to the database, but will not contain
     * a message_id.
     * •	The creation of the message will be successful if and only if the message_text is not blank, is not over
     * 255 characters, and posted_by refers to a real, existing user. If successful, the response body should contain
     * a JSON of the message, including its message_id. The response status should be 200, which is the default. The
     * new message should be persisted to the database.
     * •	If the creation of the message is not successful, the response status should be 400. (Client error)
     *
     * create table message (
     *     message_id int primary key auto_increment,
     *     posted_by int,
     *     message_text varchar(255),
     *     time_posted_epoch bigint,
     *     foreign key (posted_by) references  account(account_id)
     * );
     * @param message
     * @return
     */
    public Message createMessage(Message message) {
        // TODO: implementation
        Connection connection = ConnectionUtil.getConnection();
        try {
            int posted_by = message.getPosted_by();
            String message_text = message.getMessage_text();
            long time_posted_epoch = message.getTime_posted_epoch();

            String sqlSearch = "SELECT * FROM account WHERE account_id=?;";
            PreparedStatement psSearch = connection.prepareStatement(sqlSearch);
            psSearch.setInt(1, posted_by);
            ResultSet rsSearch = psSearch.executeQuery();
            while (rsSearch.next()) {
                if (message_text != null && message_text.length() > 0 && message_text.length() <= 255) {
                    String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?);";
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, message.getPosted_by());
                    ps.setString(2, message.getMessage_text());
                    ps.setLong(3, message.getTime_posted_epoch());
                    ps.executeUpdate();
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        int generated_message_id = rs.getInt("message_id");
                        return new Message(generated_message_id, message.getPosted_by(), message.getMessage_text(),
                                message.getTime_posted_epoch());
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * 4: Our API should be able to retrieve all messages.
     * As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/messages.
     * •	The response body should contain a JSON representation of a list containing all messages retrieved from
     * the database. It is expected for the list to simply be empty if there are no messages. The response status
     * should always be 200, which is the default.
     * @return
     */
    public List<Message> getAllMessages() {
        // TODO: implementation
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
        try {
            String sql = "SELECT * FROM message";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Message message = new Message(rs.getInt("message_id"), rs.getInt("posted_by"),
                        rs.getString("message_text"), rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return messages;
    }

    /**
     * 5: Our API should be able to retrieve a message by its ID.
     * As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/messages/{message_id}.
     * •	The response body should contain a JSON representation of the message identified by the message_id. It is
     * expected for the response body to simply be empty if there is no such message. The response status should
     * always be 200, which is the default.
     * @param message_id
     * @return
     */
    public Message getMessageById(int message_id) {
        // TODO: implementation
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "SELECT * FROM message WHERE message_id=?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, message_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return new Message(rs.getInt("message_id"), rs.getInt("posted_by"),
                        rs.getString("message_text"), rs.getLong("time_posted_epoch"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * 6: Our API should be able to delete a message identified by a message ID.
     * As a User, I should be able to submit a DELETE request on the endpoint
     * DELETE localhost:8080/messages/{message_id}.
     * •	The deletion of an existing message should remove an existing message from the database. If the message
     * existed, the response body should contain the now-deleted message. The response status should be 200, which is
     * the default.
     * •	If the message did not exist, the response status should be 200, but the response body should be empty.
     * This is because the DELETE verb is intended to be idempotent, ie, multiple calls to the DELETE endpoint should
     * respond with the same type of response.
     * @param message_id
     * @return
     */
    public Message deleteMessageById(int message_id) {
        // TODO: implementation
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sqlSearch = "SELECT * FROM message WHERE message_id=?;";
            PreparedStatement psSearch = connection.prepareStatement(sqlSearch);
            psSearch.setInt(1, message_id);
            ResultSet rs = psSearch.executeQuery();
            while (rs.next()) {
                String sql = "DELETE FROM message WHERE message_id=?;";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setInt(1, message_id);
                int n = ps.executeUpdate();
                if (n > 0) {
                    return new Message(rs.getInt("message_id"), rs.getInt("posted_by"), 
                    rs.getString("message_text"), rs.getLong("time_posted_epoch"));
                }
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * 7: Our API should be able to update a message text identified by a message ID.
     * As a user, I should be able to submit a PATCH request on the endpoint PATCH
     * localhost:8080/messages/{message_id}. The request body should contain a new message_text values to replace the
     * message identified by message_id. The request body can not be guaranteed to contain any other information.
     * •	The update of a message should be successful if and only if the message id already exists and the new
     * message_text is not blank and is not over 255 characters. If the update is successful, the response body should
     * contain the full updated message (including message_id, posted_by, message_text, and time_posted_epoch), and
     * the response status should be 200, which is the default. The message existing on the database should have the
     * updated message_text.
     * •	If the update of the message is not successful for any reason, the response status should be 400.
     * (Client error)
     * @param message_id
     * @param message_text
     * @return
     */
    public Message updateMessageText(int message_id, Message message) {
        // TODO: implementation
        Connection connection = ConnectionUtil.getConnection();
        try {
            String message_text = message.getMessage_text();
            if (message_text != null && message_text.length() > 0 && message_text.length() <= 255) {
                String sql = "UPDATE message SET message_text=? WHERE message_id=?;";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, message_text);
                ps.setInt(2, message_id);
                ps.executeUpdate();
                return getMessageById(message_id);
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * 8: Our API should be able to retrieve all messages written by a particular user.
     * As a user, I should be able to submit a GET request on the endpoint GET
     * localhost:8080/accounts/{account_id}/messages.
     * •	The response body should contain a JSON representation of a list containing all messages posted by a
     * particular user, which is retrieved from the database. It is expected for the list to simply be empty if there
     * are no messages. The response status should always be 200, which is the default.
     * @param account_id
     * @return
     */
    public List<Message> getAllMessagesForUser(int account_id) {
        // TODO: implementation
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
        try {
            String sql = "SELECT * FROM message WHERE posted_by=?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, account_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Message message = new Message(rs.getInt("message_id"), 
                rs.getInt("posted_by"), rs.getString("message_text"), 
                rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return messages;
    }
}
