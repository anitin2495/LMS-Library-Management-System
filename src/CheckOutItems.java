import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CheckOutItems extends JFrame {
    private JTextField userIdField;
    private JTextField itemIdField;
    private JLabel issueDateLabel;
    private JLabel dueDateLabel;
    private JButton checkOutButton;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CheckOutItems() {
        setTitle("Check Out Items");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        add(panel);
        placeComponents(panel);

        setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userIdLabel = new JLabel("User ID:");
        userIdLabel.setBounds(10, 20, 80, 25);
        panel.add(userIdLabel);

        userIdField = new JTextField(20);
        userIdField.setBounds(100, 20, 165, 25);
        panel.add(userIdField);

        JLabel itemIdLabel = new JLabel("Item ID:");
        itemIdLabel.setBounds(10, 50, 80, 25);
        panel.add(itemIdLabel);

        itemIdField = new JTextField(20);
        itemIdField.setBounds(100, 50, 165, 25);
        panel.add(itemIdField);

        checkOutButton = new JButton("Check Out");
        checkOutButton.setBounds(10, 80, 150, 25);
        panel.add(checkOutButton);

        // Labels for issue date and due date
        JLabel issueDateStaticLabel = new JLabel("Issue Date:");
        issueDateStaticLabel.setBounds(10, 110, 80, 25);
        panel.add(issueDateStaticLabel);

        issueDateLabel = new JLabel("");
        issueDateLabel.setBounds(100, 110, 165, 25);
        panel.add(issueDateLabel);

        JLabel dueDateStaticLabel = new JLabel("Due Date:");
        dueDateStaticLabel.setBounds(10, 140, 80, 25);
        panel.add(dueDateStaticLabel);

        dueDateLabel = new JLabel("");
        dueDateLabel.setBounds(100, 140, 165, 25);
        panel.add(dueDateLabel);
        
        checkOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkOutItem();
            }
        });
        
    }

    private void checkOutItem() {
        String userId = userIdField.getText();
        String itemId = itemIdField.getText();

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false); // Begin transaction

            // Check if the item exists and get its details
            String itemQuery = "SELECT item_id, best_seller, item_type, copies FROM items WHERE item_id = ?";
            try (PreparedStatement itemStmt = conn.prepareStatement(itemQuery)) {
                itemStmt.setString(1, itemId);
                ResultSet itemRs = itemStmt.executeQuery();
                
                if (itemRs.next()) {
                    boolean bestSeller = itemRs.getBoolean("best_seller");
                    String itemType = itemRs.getString("item_type");
                    int availableCopies = itemRs.getInt("copies");

                    // Check if there are available copies
                    if (availableCopies <= 0) {
                        JOptionPane.showMessageDialog(null, "No copies available for checkout.");
                        return;
                    }

                    // Determine the checkout period based on item type and best-seller status
                    int daysToAdd;
                    if (itemType.equals("book")) {
                        daysToAdd = bestSeller ? 14 : 21; // 2 weeks for best sellers, 3 weeks for others
                    } else if (itemType.equals("audio") || itemType.equals("video")) {
                        daysToAdd = 14; // 2 weeks for audio/video
                    } else {
                        JOptionPane.showMessageDialog(null, "This item type cannot be checked out.");
                        return;
                    }

                    // Check user’s age and limit the number of items checked out
                    int userAge = getUserAge(conn, userId);
                    int maxItems = (userAge <= 12) ? 5 : Integer.MAX_VALUE;

                    // Count current checked-out items
                    int currentCheckedOut = countCheckedOutItems(conn, userId);
                    if (currentCheckedOut >= maxItems) {
                        JOptionPane.showMessageDialog(null, "Checkout limit reached for this user.");
                        return;
                    }

                    LocalDate checkoutDate = LocalDate.now();
                    LocalDate dueDate = checkoutDate.plusDays(daysToAdd);
                    String formattedIssueDate = checkoutDate.format(formatter);
                    String formattedDueDate = dueDate.format(formatter);

                    // Update labels to show issue date and due date
                    issueDateLabel.setText(formattedIssueDate);
                    dueDateLabel.setText(formattedDueDate);

                    // Insert checkout record into the database
                    String checkoutQuery = "INSERT INTO checkouts (user_id, item_id, checkout_date, due_date, returned) VALUES (?, ?, ?, ?, FALSE)";
                    try (PreparedStatement checkoutStmt = conn.prepareStatement(checkoutQuery)) {
                        checkoutStmt.setString(1, userId);
                        checkoutStmt.setString(2, itemId);
                        checkoutStmt.setDate(3, java.sql.Date.valueOf(checkoutDate));
                        checkoutStmt.setDate(4, java.sql.Date.valueOf(dueDate));
                        checkoutStmt.executeUpdate();
                    }

                    // Update the number of available copies in the items table
                    String updateCopiesQuery = "UPDATE items SET copies = copies - 1 WHERE item_id = ?";
                    try (PreparedStatement updateCopiesStmt = conn.prepareStatement(updateCopiesQuery)) {
                        updateCopiesStmt.setString(1, itemId);
                        updateCopiesStmt.executeUpdate();
                    }

                    conn.commit(); // Commit transaction

                    JOptionPane.showMessageDialog(null, "Item checked out successfully! Due date: " + formattedDueDate);
                } else {
                    JOptionPane.showMessageDialog(null, "Item not found.");
                }
            } catch (SQLException ex) {
                conn.rollback(); // Rollback transaction in case of error
                JOptionPane.showMessageDialog(null, "Error checking out item: " + ex.getMessage());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database connection error: " + ex.getMessage());
        }
    }

    private int getUserAge(Connection conn, String userId) throws SQLException {
        String query = "SELECT age FROM users WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("age");
            }
            return 0; // Default to 0 if user not found
        }
    }

    private int countCheckedOutItems(Connection conn, String userId) throws SQLException {
        String query = "SELECT COUNT(*) FROM checkouts WHERE user_id = ? AND returned = FALSE";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0; // Default to 0 if no records found
        }
    }

    public static void main(String[] args) {
        new CheckOutItems();
    }
}
