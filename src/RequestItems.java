import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RequestItems extends JFrame {
    private JTextField userIdField;
    private JTextField itemIdField;
    private JComboBox<String> itemTypeComboBox;
    private JButton requestButton;

    public RequestItems() {
        setTitle("Request Items");
        setSize(400, 300);
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
        userIdLabel.setBounds(10, 10, 100, 25);
        panel.add(userIdLabel);

        userIdField = new JTextField(20);
        userIdField.setBounds(120, 10, 160, 25);
        panel.add(userIdField);

        JLabel itemIdLabel = new JLabel("Item ID:");
        itemIdLabel.setBounds(10, 40, 100, 25);
        panel.add(itemIdLabel);

        itemIdField = new JTextField(20);
        itemIdField.setBounds(120, 40, 160, 25);
        panel.add(itemIdField);

        JLabel itemTypeLabel = new JLabel("Item Type:");
        itemTypeLabel.setBounds(10, 70, 100, 25);
        panel.add(itemTypeLabel);

        itemTypeComboBox = new JComboBox<>(new String[]{"book", "audio_video"});
        itemTypeComboBox.setBounds(120, 70, 160, 25);
        panel.add(itemTypeComboBox);

        requestButton = new JButton("Request");
        requestButton.setBounds(10, 100, 100, 25);
        panel.add(requestButton);

        requestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleRequest();
            }
        });
    }

    private void handleRequest() {
        String userId = userIdField.getText();
        String itemId = itemIdField.getText();

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false); // Begin transaction

            // Check if item copies are zero
            String checkCopiesQuery = "SELECT copies FROM items WHERE item_id = ?";
            try (PreparedStatement checkCopiesStmt = conn.prepareStatement(checkCopiesQuery)) {
                checkCopiesStmt.setString(1, itemId);
                ResultSet copiesRs = checkCopiesStmt.executeQuery();
                if (copiesRs.next()) {
                    int copies = copiesRs.getInt("copies");
                    if (copies > 0) {
                        JOptionPane.showMessageDialog(null, "Item has available copies. No need to request.");
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Item not found.");
                    return;
                }
            }

            // Check if item is currently checked out
            String checkAvailabilityQuery = "SELECT COUNT(*) FROM checkouts WHERE item_id = ? AND returned = FALSE";
            try (PreparedStatement stmt1 = conn.prepareStatement(checkAvailabilityQuery)) {
                stmt1.setString(1, itemId);
                ResultSet rs = stmt1.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    // Item is checked out, proceed to request
                    // Check if there is already a request for this item
                    String checkRequestQuery = "SELECT COUNT(*) FROM requests WHERE item_id = ? AND fulfilled = FALSE";
                    try (PreparedStatement stmt2 = conn.prepareStatement(checkRequestQuery)) {
                        stmt2.setString(1, itemId);
                        ResultSet requestRs = stmt2.executeQuery();
                        if (requestRs.next() && requestRs.getInt(1) > 0) {
                            JOptionPane.showMessageDialog(null, "Item is already requested and is not available.");
                            return;
                        }

                        // Add request to the database with request_date
                        String requestQuery = "INSERT INTO requests (user_id, item_id, request_date) VALUES (?, ?, ?)";
                        try (PreparedStatement stmt3 = conn.prepareStatement(requestQuery)) {
                            stmt3.setString(1, userId);
                            stmt3.setString(2, itemId);
                            stmt3.setDate(3, new java.sql.Date(System.currentTimeMillis())); // Set the current date as request_date
                            stmt3.executeUpdate();
                            
                            JOptionPane.showMessageDialog(null, "Request added successfully. The item will be available soon.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Item is not currently checked out and cannot be requested.");
                }
            }

            conn.commit(); // Commit transaction
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error requesting item: " + ex.getMessage());
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RequestItems());
    }
}
