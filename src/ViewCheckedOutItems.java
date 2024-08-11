import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ViewCheckedOutItems extends JFrame {
    private JTextField userIdField;
    private JButton searchButton;
    private JTable itemsTable;
    private DefaultTableModel tableModel;

    public ViewCheckedOutItems() {
        setTitle("View Checked Out Items");
        setSize(600, 400);
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

        searchButton = new JButton("Search");
        searchButton.setBounds(10, 50, 150, 25);
        panel.add(searchButton);

        // Table setup
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{"User Name", "Book ID", "Book Title", "Checkout Date", "Due Date", "Fines"});
        itemsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(itemsTable);
        scrollPane.setBounds(10, 80, 560, 270);
        panel.add(scrollPane);

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadCheckedOutItems();
            }
        });
    }

    private void loadCheckedOutItems() {
        String userId = userIdField.getText();

        try (Connection conn = Database.getConnection()) {
            // Updated query with correct column names
            String query = "SELECT u.name AS user_name, i.item_id, i.title AS book_title, c.checkout_date, c.due_date, " +
                    "CASE " +
                    "   WHEN c.returned = FALSE AND c.due_date < CURDATE() THEN " +
                    "       LEAST(DATEDIFF(CURDATE(), c.due_date) * 0.10, i.value) " +
                    "   ELSE 0 " +
                    "END AS fines " +
                    "FROM checkouts c " +
                    "JOIN users u ON c.user_id = u.user_id " +
                    "JOIN items i ON c.item_id = i.item_id " +
                    "WHERE c.user_id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, userId);
                ResultSet rs = stmt.executeQuery();

                tableModel.setRowCount(0); // Clear existing rows

                while (rs.next()) {
                    String userName = rs.getString("user_name");
                    String itemId = rs.getString("item_id");
                    String bookTitle = rs.getString("book_title");
                    Date checkoutDate = rs.getDate("checkout_date");
                    Date dueDate = rs.getDate("due_date");
                    double fines = rs.getDouble("fines");

                    tableModel.addRow(new Object[]{
                            userName,
                            itemId,
                            bookTitle,
                            checkoutDate,
                            dueDate,
                            fines
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading checked out items: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new ViewCheckedOutItems();
    }
}
