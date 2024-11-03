package com.nhlstenden.reader2.DataLibrary;

import com.nhlstenden.reader2.controllers.DatabaseController;
import com.nhlstenden.reader2.models.Chapter;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChapterLibrary {

    private final DatabaseController dbController;

    public ChapterLibrary() {
        this.dbController = DatabaseController.getInstance();
    }

    /**
     * Adds a new chapter to the database.
     *
     * This method inserts a new chapter record into the Chapter table in the database.
     * It sets the chapter's title, series ID, read status, current page, and last read timestamp.
     * If the insertion is successful, it returns the generated ID of the new chapter.
     * If an error occurs, it prints the stack trace and returns -1.
     *
     * @param chapter the Chapter object containing the details of the chapter to be added
     * @return the generated ID of the new chapter, or -1 if the insertion fails
     */
    public int addChapter(Chapter chapter) {
        this.dbController.openConnection();
        String sql = "INSERT INTO Chapter (Title, SerieID, Read, CurrentPage, LastRead) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbController.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, chapter.getTitle());
            pstmt.setInt(2, chapter.getSerieID());
            pstmt.setBoolean(3, chapter.isRead());
            pstmt.setInt(4, chapter.getCurrentPage());
            pstmt.setTimestamp(5, Timestamp.valueOf(chapter.getLastRead()));
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
     * Retrieves a chapter from the database by its ID.
     *
     * This method queries the Chapter table in the database to retrieve a chapter record by its ID.
     * If the chapter is found, it returns a Chapter object with the retrieved details.
     * If an error occurs or the chapter is not found, it prints the stack trace and returns null.
     *
     * @param id the ID of the chapter to be retrieved
     * @return the Chapter object if found, or null if not found
     */
    public Chapter getChapter(int id) {
        this.dbController.openConnection();
        String sql = "SELECT * FROM Chapter WHERE ID = ?";
        try (Connection conn = dbController.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Chapter temp = new Chapter(
                        rs.getString("Title"),
                        null,
                        rs.getBoolean("Read"),
                        rs.getInt("CurrentPage"),
                        null
                );
                if (rs.getTimestamp("LastRead") != null) {
                    temp.setLastRead(rs.getTimestamp("LastRead").toLocalDateTime());
                }
                temp.setId(rs.getInt("Id"));
                return temp;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a chapter from the database by its title.
     *
     * This method queries the Chapter table in the database to retrieve a chapter record by its title.
     * If the chapter is found, it returns a Chapter object with the retrieved details.
     * If an error occurs or the chapter is not found, it prints the stack trace and returns null.
     *
     * @param name the title of the chapter to be retrieved
     * @return the Chapter object if found, or null if not found
     */
    public Chapter getChapterByName(String name) {
        this.dbController.openConnection();
        String sql = "SELECT * FROM Chapter WHERE Title = ?";
        try (Connection conn = dbController.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Chapter temp = new Chapter(
                        rs.getString("Title"),
                        null,
                        rs.getBoolean("Read"),
                        rs.getInt("CurrentPage"),
                        null
                );
                if (rs.getTimestamp("LastRead") != null) {
                    temp.setLastRead(rs.getTimestamp("LastRead").toLocalDateTime());
                }
                temp.setId(rs.getInt("Id"));
                return temp;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all chapters for a specific series from the database.
     *
     * This method queries the Chapter table in the database to retrieve all chapter records for a specific series.
     * It returns a list of Chapter objects for the specified series.
     * If an error occurs, it prints the stack trace and returns an empty list.
     *
     * @param serieId the ID of the series
     * @return a list of Chapter objects for the specified series
     */
    public List<Chapter> getChapterBySerie(int serieId) {
        this.dbController.openConnection();
        List<Chapter> chapters = new ArrayList<>();
        String sql = "SELECT * FROM Chapter WHERE SerieId = ?";
        try (Connection conn = dbController.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, serieId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Chapter temp = new Chapter(
                        rs.getString("Title"),
                        null,
                        rs.getBoolean("Read"),
                        rs.getInt("CurrentPage"),
                        null
                );
                if (rs.getTimestamp("LastRead") != null) {
                    temp.setLastRead(rs.getTimestamp("LastRead").toLocalDateTime());
                }
                temp.setId(rs.getInt("Id"));
                chapters.add(temp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chapters;
    }

    /**
     * Retrieves all chapters from the database.
     *
     * This method queries the Chapter table in the database to retrieve all chapter records.
     * It returns a list of all Chapter objects.
     * If an error occurs, it prints the stack trace and returns an empty list.
     *
     * @return a list of all Chapter objects
     */
    public List<Chapter> getAllChapters() {
        this.dbController.openConnection();
        List<Chapter> chapters = new ArrayList<>();
        String sql = "SELECT * FROM Chapter";
        try (Connection conn = dbController.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Chapter temp = new Chapter(
                        rs.getString("Title"),
                        null,
                        rs.getBoolean("Read"),
                        rs.getInt("CurrentPage"),
                        null
                );
                if (rs.getTimestamp("LastRead") != null) {
                    temp.setLastRead(rs.getTimestamp("LastRead").toLocalDateTime());
                }
                temp.setId(rs.getInt("Id"));
                chapters.add(temp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chapters;
    }

    /**
     * Updates the last read timestamp of a chapter in the database.
     *
     * This method updates the last read timestamp of a chapter record in the Chapter table in the database.
     * It sets the last read timestamp to the current date and time.
     * If the update is successful, it returns true.
     * If an error occurs, it prints the stack trace and returns false.
     *
     * @param id the ID of the chapter to be updated
     * @return true if the update was successful, false otherwise
     */
    public boolean setLastRead(int id) {
        this.dbController.openConnection();
        String sql = "UPDATE Chapter SET LastRead = ? WHERE ID = ?";
        try (Connection conn = dbController.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates a chapter in the database.
     *
     * This method updates the details of a chapter record in the Chapter table in the database.
     * It sets the chapter's title, series ID, read status, current page, and last read timestamp.
     * If the update is successful, it returns true.
     * If an error occurs, it prints the stack trace and returns false.
     *
     * @param chapter the Chapter object containing the updated details
     * @return true if the update was successful, false otherwise
     */
    public boolean updateChapter(Chapter chapter) {
        this.dbController.openConnection();
        String sql = "UPDATE Chapter SET Title = ?, SerieID = ?, Read = ?, CurrentPage = ?, LastRead = ? WHERE ID = ?";
        try (Connection conn = dbController.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, chapter.getTitle());
            pstmt.setInt(2, chapter.getSerieID());
            pstmt.setBoolean(3, chapter.isRead());
            pstmt.setInt(4, chapter.getCurrentPage());
            pstmt.setTimestamp(5, Timestamp.valueOf(chapter.getLastRead()));
            pstmt.setInt(6, chapter.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a chapter from the database by its ID.
     *
     * This method deletes a chapter record from the Chapter table in the database by its ID.
     * If the deletion is successful, it returns true.
     * If an error occurs, it prints the stack trace and returns false.
     *
     * @param id the ID of the chapter to be deleted
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteChapter(int id) {
        this.dbController.openConnection();
        String sql = "DELETE FROM Chapter WHERE ID = ?";
        try (Connection conn = dbController.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes all chapters for a specific series from the database.
     *
     * This method deletes all chapter records from the Chapter table in the database for a specific series.
     * If the deletion is successful, it returns true.
     * If an error occurs, it prints the stack trace and returns false.
     *
     * @param serieId the ID of the series
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteChapterBySerie(int serieId) {
        this.dbController.openConnection();
        String sql = "DELETE FROM Chapter WHERE SerieId = ?";
        try (Connection conn = dbController.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, serieId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}