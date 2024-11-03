package com.nhlstenden.reader2.DataLibrary;

import com.nhlstenden.reader2.controllers.DatabaseController;
import com.nhlstenden.reader2.models.Serie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SerieLibrary {

    private final DatabaseController dbController;

    public SerieLibrary() {
        this.dbController = DatabaseController.getInstance();
    }

    /**
     * Adds a new series to the database.
     *
     * This method inserts a new series record into the Serie table in the database.
     * It sets the series' name, description, completed status, favorite status, current chapter, read status, and genre.
     * If the insertion is successful, it returns the generated ID of the new series.
     * If an error occurs, it prints the stack trace and returns -1.
     *
     * @param serie the Serie object containing the details of the series to be added
     * @return the generated ID of the new series, or -1 if the insertion fails
     */
    public int addSerie(Serie serie) {
        String sql = "INSERT INTO Serie (Name, Description, Completed, Favorite, CurrentChapter, Read, Gerne) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbController.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, serie.getName());
            pstmt.setString(2, serie.getDescription());
            pstmt.setBoolean(3, serie.isCompleted());
            pstmt.setBoolean(4, serie.isFavorite());
            pstmt.setInt(5, serie.getCurrentChapter());
            pstmt.setBoolean(6, serie.isRead());
            pstmt.setString(7, serie.getGerne());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Retrieves a series from the database by its ID.
     *
     * This method queries the Serie table in the database to retrieve a series record by its ID.
     * If the series is found, it returns a Serie object with the retrieved details.
     * If an error occurs or the series is not found, it prints the stack trace and returns null.
     *
     * @param id the ID of the series to be retrieved
     * @return the Serie object if found, or null if not found
     */
    public Serie getSerie(int id) {
        String sql = "SELECT * FROM Serie WHERE ID = ?";
        try (Connection conn = dbController.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {

                Serie temp = new Serie(
                        rs.getString("Name"),
                        rs.getString("Description"),
                        rs.getBoolean("Completed"),
                        rs.getBoolean("Favorite"),
                        rs.getInt("CurrentChapter"),
                        rs.getBoolean("Read"),
                        rs.getString("Gerne")
                );

                temp.setId(rs.getInt("Id"));

                return temp;

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a series from the database by its name.
     *
     * This method queries the Serie table in the database to retrieve a series record by its name.
     * If the series is found, it returns a Serie object with the retrieved details.
     * If an error occurs or the series is not found, it prints the stack trace and returns null.
     *
     * @param name the name of the series to be retrieved
     * @return the Serie object if found, or null if not found
     */
    public Serie getSerieByName(String name) {

        this.dbController.openConnection();

        String sql = "SELECT * FROM Serie WHERE Name = ?";
        try (Connection conn = dbController.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {

                Serie temp = new Serie(
                        rs.getString("Name"),
                        rs.getString("Description"),
                        rs.getBoolean("Completed"),
                        rs.getBoolean("Favorite"),
                        rs.getInt("CurrentChapter"),
                        rs.getBoolean("Read"),
                        rs.getString("Gerne")
                );

                temp.setId(rs.getInt("Id"));

                return temp;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all series from the database.
     *
     * This method queries the Serie table in the database to retrieve all series records.
     * It returns a list of all Serie objects.
     * If an error occurs, it prints the stack trace and returns an empty list.
     *
     * @return a list of all Serie objects
     */
    public List<Serie> getAllSeries() {

        this.dbController.openConnection();

        List<Serie> series = new ArrayList<>();
        String sql = "SELECT * FROM Serie";
        Connection conn = dbController.getConnection();
        try {
            if (conn == null || conn.isClosed()) {
                System.err.println("Database connection is closed or not initialized.");
                return series;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {

                Serie temp = new Serie(
                        rs.getString("Name"),
                        rs.getString("Description"),
                        rs.getBoolean("Completed"),
                        rs.getBoolean("Favorite"),
                        rs.getInt("CurrentChapter"),
                        rs.getBoolean("Read"),
                        rs.getString("Gerne")
                );
                temp.setId(rs.getInt("Id"));

                series.add(temp);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return series;
    }

    /**
     * Updates a series in the database.
     *
     * This method updates the details of a series record in the Serie table in the database.
     * It sets the series' name, description, completed status, favorite status, current chapter, read status, and genre.
     * If the update is successful, it returns true.
     * If an error occurs, it prints the stack trace and returns false.
     *
     * @param serie the Serie object containing the updated details
     * @return true if the update was successful, false otherwise
     */
    public boolean updateSerie(Serie serie) {
        String sql = "UPDATE Serie SET Name = ?, Description = ?, Completed = ?, Favorite = ?, CurrentChapter = ?, Read = ?, Gerne = ? WHERE Id = ?";
        try (Connection conn = dbController.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, serie.getName());
            pstmt.setString(2, serie.getDescription());
            pstmt.setBoolean(3, serie.isCompleted());
            pstmt.setBoolean(4, serie.isFavorite());
            pstmt.setInt(5, serie.getCurrentChapter());
            pstmt.setBoolean(6, serie.isRead());
            pstmt.setString(7, serie.getGerne());
            pstmt.setInt(8, serie.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a series from the database by its ID.
     *
     * This method deletes a series record from the Serie table in the database by its ID.
     * If the deletion is successful, it returns true.
     * If an error occurs, it prints the stack trace and returns false.
     *
     * @param id the ID of the series to be deleted
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteSerie(int id) {

        this.dbController.openConnection();

        String sql = "DELETE FROM Serie WHERE ID = ?";
        try (Connection conn = dbController.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}