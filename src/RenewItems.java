import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RenewItems extends JFrame {
    private JTextField userIdField;
    private JTextField itemIdField;
    private JButton renewButton;

    public RenewItems() {
        setTitle("Renew Items");
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

        renewButton = new JButton("Renew");
        renewButton.setBounds(10, 80, 150, 25);
        panel.add(renewButton);

        renewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleRenew();
            }
        });
    }

    private void handleRenew() {
        String userId = userIdField.getText();
        String itemId = itemIdField.getText();

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false); // Begin transaction

            // Check if the item is checked out by the user
            String checkOutQuery = "SELECT checkout_id, due_date, renewal_count FROM checkouts WHERE user_id = ? AND item_id = ? AND returned = FALSE";
            try (PreparedStatement checkOutStmt = conn.prepareStatement(checkOutQuery)) {
                checkOutStmt.setString(1, userId);
                checkOutStmt.setString(2, itemId);
                ResultSet checkOutRs = checkOutStmt.executeQuery();
                if (!checkOutRs.next()) {
                    JOptionPane.showMessageDialog(null, "Item not found in your checkouts or already returned.");
                    return;
                }

                int checkoutId = checkOutRs.getInt("checkout_id");
                Date dueDate = checkOutRs.getDate("due_date");
                int renewalCount = checkOutRs.getInt("renewal_count");

                // Check if the item has been requested
                String requestQuery = "SELECT COUNT(*) FROM requests WHERE item_id = ? AND fulfilled = FALSE";
                try (PreparedStatement requestStmt = conn.prepareStatement(requestQuery)) {
                    requestStmt.setString(1, itemId);
                    ResultSet requestRs = requestStmt.executeQuery();
                    if (requestRs.next() && requestRs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(null, "Item has been requested by another user. You cannot renew this item.");
                        return;
                    }

                    // Check if renewal is allowed
                    if (renewalCount >= 1) {
                        JOptionPane.showMessageDialog(null, "Item has already been renewed once.");
                        return;
                    }

                    // Extend due date
                    java.util.Date today = new java.util.Date();
                    java.sql.Date currentDate = new java.sql.Date(today.getTime());
                    java.util.Calendar calendar = java.util.Calendar.getInstance();
                    calendar.setTime(currentDate);
                    calendar.add(java.util.Calendar.DAY_OF_MONTH, 21); // Extend by 21 days for books

                    java.sql.Date newDueDate = new java.sql.Date(calendar.getTimeInMillis());

                    // Update checkout record with new due date and increment renewal count
                    String updateQuery = "UPDATE checkouts SET due_date = ?, renewal_count = renewal_count + 1 WHERE checkout_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setDate(1, newDueDate);
                        updateStmt.setInt(2, checkoutId);
                        updateStmt.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Item renewed successfully. New due date: " + newDueDate);
                    }
                }
            }

            conn.commit(); // Commit transaction
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error renewing item: " + ex.getMessage());
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RenewItems());
    }
}
