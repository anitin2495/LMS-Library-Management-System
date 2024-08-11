import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ReturnItems extends JFrame {
    private JTextField userIdField;
    private JTextField itemIdField;
    private JButton returnButton;
    private JLabel statusLabel;

    public ReturnItems() {
        setTitle("Return Items");
        setSize(400, 200);
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

        returnButton = new JButton("Return Item");
        returnButton.setBounds(10, 80, 150, 25);
        panel.add(returnButton);

        statusLabel = new JLabel("");
        statusLabel.setBounds(10, 110, 350, 25);
        panel.add(statusLabel);
        
        returnButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleReturn();
            }
        });
    }

    private void handleReturn() {
        String userId = userIdField.getText();
        String itemId = itemIdField.getText();

        Connection conn = null;
        try {
            conn = Database.getConnection(); // Ensure you have a method to get a database connection
            conn.setAutoCommit(false); // Begin transaction

            // Update copies count
            String updateCopiesQuery = "UPDATE items SET copies = copies + 1 WHERE item_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateCopiesQuery)) {
                updateStmt.setString(1, itemId);
                updateStmt.executeUpdate();
            }

            // Remove entry from checked_out table
            String deleteCheckedOutQuery = "DELETE FROM checkouts WHERE user_id = ? AND item_id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteCheckedOutQuery)) {
                deleteStmt.setString(1, userId);
                deleteStmt.setString(2, itemId);
                deleteStmt.executeUpdate();
            }

            conn.commit(); // Commit transaction

            statusLabel.setText("Item returned successfully.");
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction in case of error
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            statusLabel.setText("Error returning item: " + ex.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close(); // Close connection
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new ReturnItems();
    }
}
