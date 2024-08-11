import java.sql.*;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/Library_D";
    private static final String USER = "root";
    private static final String PASSWORD = "admin";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void addUser(String username, String name, String address, String phoneNumber, String libraryCardNumber, int age) throws SQLException {
        String query = "INSERT INTO users (username, name, address, phone_number, library_card_number, age) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, name);
            stmt.setString(3, address);
            stmt.setString(4, phoneNumber);
            stmt.setString(5, libraryCardNumber);
            stmt.setInt(6, age);
            stmt.executeUpdate();
        }
    }

    public static ResultSet getUserByUsername(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, username);
        return stmt.executeQuery();
    }

    public static ResultSet getUserById(int userId) throws SQLException {
        String query = "SELECT * FROM users WHERE id = ?";
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, userId);
        return stmt.executeQuery();
    }

    public static void addBook(String title, String author, String isbn, int yearPublished, boolean bestSeller, boolean referenceOnly) throws SQLException {
        String query = "INSERT INTO books (title, author, isbn, year_published, best_seller, reference_only) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, isbn);
            stmt.setInt(4, yearPublished);
            stmt.setBoolean(5, bestSeller);
            stmt.setBoolean(6, referenceOnly);
            stmt.executeUpdate();
        }
    }

    public static void addAudioVideo(String title, String creator, String type, int yearPublished, boolean referenceOnly) throws SQLException {
        String query = "INSERT INTO audio_video (title, creator, type, year_published, reference_only) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, title);
            stmt.setString(2, creator);
            stmt.setString(3, type);
            stmt.setInt(4, yearPublished);
            stmt.setBoolean(5, referenceOnly);
            stmt.executeUpdate();
        }
    }

    // Other database methods for managing checkouts, requests, fines, etc.
}
