package org.tgbot.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.User;

public class DbConnection {

    public static DbConnection instance = new DbConnection();

    private Connection conn = null;

    DbConnection() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/tg_bot", "root", "2232");
            if (conn != null) {
                System.out.println("Connected to the database");
            } else {
                System.out.println("Failed to make connection");
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveSubscriber(User user) {
        String query = "INSERT INTO `tg_bot`.`subscribers` (`id`, `date`) VALUES ('" + user.getId()
                + "', CURRENT_TIMESTAMP);";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            int rows = preparedStatement.executeUpdate();

            System.out.printf("%d rows (users) added", rows);

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Long> getSubscribersId() {
        String query = "SELECT `id` FROM `tg_bot`.`subscribers`"; // WHERE `id` = '" + id + "'
        List<Long> ids = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ids.add(resultSet.getLong("id"));
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ids;
    }

}
