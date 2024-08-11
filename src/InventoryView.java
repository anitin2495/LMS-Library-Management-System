import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class InventoryView extends JFrame {
    private JTable inventoryTable;
    private DefaultTableModel tableModel;

    public InventoryView() {
        setTitle("Inventory View");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        add(panel);

        // Table setup
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new Object[]{"Item ID", "Title", "Author", "Available Copies", "Checked Out User IDs"});
        inventoryTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        loadInventoryData();

        setVisible(true);
    }

    private void loadInventoryData() {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT i.item_id, i.title, i.author, i.copies, " +
                           "GROUP_CONCAT(c.user_id) AS checked_out_users " +
                           "FROM items i " +
                           "LEFT JOIN checkouts c ON i.item_id = c.item_id AND c.returned = FALSE " +
                           "GROUP BY i.item_id " +
                           "ORDER BY i.item_id";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();

                tableModel.setRowCount(0); // Clear existing rows

                while (rs.next()) {
                    String itemId = rs.getString("item_id");
                    String title = rs.getString("title");
                    String author = rs.getString("author");
                    int availableCopies = rs.getInt("copies");
                    String checkedOutUsers = rs.getString("checked_out_users");

                    tableModel.addRow(new Object[]{
                        itemId,
                        title,
                        author,
                        availableCopies,
                        checkedOutUsers
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading inventory: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InventoryView());
    }
}
