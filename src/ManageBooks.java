import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.*;
import java.text.DecimalFormat;

public class ManageBooks extends JFrame {
    private JTabbedPane tabbedPane;
    private JPanel addBookPanel;
    private JPanel viewBooksPanel;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField valueField;
    private JCheckBox bestSellerCheckBox;
    private JCheckBox referenceOnlyCheckBox;
    private JComboBox<String> itemTypeComboBox;
    private JTextField copiesField;
    private JButton addBookButton;
    private JTable booksTable;

    public ManageBooks() {
        setTitle("Manage Books");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        // Add Book Panel
        addBookPanel = new JPanel();
        addBookPanel.setLayout(null);

        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setBounds(10, 40, 100, 25);
        addBookPanel.add(titleLabel);

        titleField = new JTextField(20);
        titleField.setBounds(120, 40, 160, 25);
        addBookPanel.add(titleField);

        JLabel authorLabel = new JLabel("Author:");
        authorLabel.setBounds(10, 70, 100, 25);
        addBookPanel.add(authorLabel);

        authorField = new JTextField(20);
        authorField.setBounds(120, 70, 160, 25);
        addBookPanel.add(authorField);

        JLabel valueLabel = new JLabel("Value:");
        valueLabel.setBounds(10, 100, 100, 25);
        addBookPanel.add(valueLabel);

        valueField = new JTextField(20);
        valueField.setBounds(120, 100, 160, 25);
        addBookPanel.add(valueField);

        JLabel bestSellerLabel = new JLabel("Best Seller:");
        bestSellerLabel.setBounds(10, 130, 100, 25);
        addBookPanel.add(bestSellerLabel);

        bestSellerCheckBox = new JCheckBox();
        bestSellerCheckBox.setBounds(120, 130, 20, 25);
        addBookPanel.add(bestSellerCheckBox);

        JLabel referenceOnlyLabel = new JLabel("Reference Only:");
        referenceOnlyLabel.setBounds(10, 160, 120, 25);
        addBookPanel.add(referenceOnlyLabel);

        referenceOnlyCheckBox = new JCheckBox();
        referenceOnlyCheckBox.setBounds(120, 160, 120, 25);
        addBookPanel.add(referenceOnlyCheckBox);

        JLabel itemTypeLabel = new JLabel("Item Type:");
        itemTypeLabel.setBounds(10, 190, 100, 25);
        addBookPanel.add(itemTypeLabel);

        itemTypeComboBox = new JComboBox<>(new String[]{"book", "audio", "video", "reference", "magazine"});
        itemTypeComboBox.setBounds(120, 190, 160, 25);
        addBookPanel.add(itemTypeComboBox);

        JLabel copiesLabel = new JLabel("Copies:");
        copiesLabel.setBounds(10, 220, 100, 25);
        addBookPanel.add(copiesLabel);

        copiesField = new JTextField(20);
        copiesField.setBounds(120, 220, 160, 25);
        addBookPanel.add(copiesField);

        addBookButton = new JButton("Add Book");
        addBookButton.setBounds(10, 250, 120, 25);
        addBookPanel.add(addBookButton);

        addBookButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addBook();
            }
        });

        tabbedPane.addTab("Add Book", addBookPanel);

        // View Books Panel
        viewBooksPanel = new JPanel();
        viewBooksPanel.setLayout(new BorderLayout());

        booksTable = new JTable();
        JScrollPane bookScrollPane = new JScrollPane(booksTable);
        viewBooksPanel.add(bookScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton refreshBooksButton = new JButton("Refresh Books");
        buttonPanel.add(refreshBooksButton);

        JButton modifyButton = new JButton("Modify Book");
        buttonPanel.add(modifyButton);

        JButton deleteButton = new JButton("Delete Book");
        buttonPanel.add(deleteButton);

        viewBooksPanel.add(buttonPanel, BorderLayout.SOUTH);

        refreshBooksButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadBooks();
            }
        });

        modifyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                modifyBook();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteBook();
            }
        });

        tabbedPane.addTab("View Books", viewBooksPanel);

        add(tabbedPane);

        setVisible(true);
        loadBooks(); // Load books initially
    }

    private String generateItemId() {
        String prefix = "ITM";
        int maxId = 0;

        try (Connection conn = Database.getConnection()) {
            String query = "SELECT MAX(CAST(SUBSTRING(item_id, 4) AS UNSIGNED)) FROM items WHERE item_id LIKE 'ITM%'";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    maxId = rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error generating item ID: " + ex.getMessage());
        }

        maxId++;
        DecimalFormat df = new DecimalFormat("000");
        return prefix + df.format(maxId);
    }

    private void addBook() {
        String title = titleField.getText();
        String author = authorField.getText();
        String value = valueField.getText();
        boolean bestSeller = bestSellerCheckBox.isSelected();
        boolean referenceOnly = referenceOnlyCheckBox.isSelected();
        String itemType = (String) itemTypeComboBox.getSelectedItem();
        String copies = copiesField.getText();

        try (Connection conn = Database.getConnection()) {
            String itemId = generateItemId();

            String query = "INSERT INTO items (item_id, title, author, value, best_seller, reference_only, item_type, copies) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, itemId);
                stmt.setString(2, title);
                stmt.setString(3, author);
                stmt.setBigDecimal(4, new BigDecimal(value)); // Assuming value is numeric
                stmt.setBoolean(5, bestSeller);
                stmt.setBoolean(6, referenceOnly);
                stmt.setString(7, itemType);
                stmt.setInt(8, Integer.parseInt(copies)); // Assuming copies is numeric
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Book added successfully with ID: " + itemId);
                titleField.setText("");
                authorField.setText("");
                valueField.setText("");
                bestSellerCheckBox.setSelected(false);
                referenceOnlyCheckBox.setSelected(false);
                itemTypeComboBox.setSelectedIndex(0);
                copiesField.setText("");
                loadBooks(); // Reload the books table
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error adding book: " + ex.getMessage());
        }
    }

    private void loadBooks() {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT item_id, title, author, value, best_seller, reference_only, item_type, copies FROM items";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                DefaultTableModel model = new DefaultTableModel(new String[]{"Item ID", "Title", "Author", "Value", "Best Seller", "Reference Only", "Item Type", "Copies"}, 0);
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("item_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getBigDecimal("value"),
                        rs.getBoolean("best_seller"),
                        rs.getBoolean("reference_only"),
                        rs.getString("item_type"),
                        rs.getInt("copies")
                    });
                }
                booksTable.setModel(model);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading books: " + ex.getMessage());
        }
    }

    private void modifyBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a book to modify.");
            return;
        }

        String itemId = (String) booksTable.getValueAt(selectedRow, 0);
        String title = JOptionPane.showInputDialog("Enter new title:", booksTable.getValueAt(selectedRow, 1));
        String author = JOptionPane.showInputDialog("Enter new author:", booksTable.getValueAt(selectedRow, 2));
        String value = JOptionPane.showInputDialog("Enter new value:", booksTable.getValueAt(selectedRow, 3));
        String itemType = JOptionPane.showInputDialog("Enter new item type:", booksTable.getValueAt(selectedRow, 6));
        String copies = JOptionPane.showInputDialog("Enter new number of copies:", booksTable.getValueAt(selectedRow, 7));

        try (Connection conn = Database.getConnection()) {
            String query = "UPDATE items SET title = ?, author = ?, value = ?, item_type = ?, copies = ? WHERE item_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, title);
                stmt.setString(2, author);
                stmt.setBigDecimal(3, new BigDecimal(value));
                stmt.setString(4, itemType);
                stmt.setInt(5, Integer.parseInt(copies));
                stmt.setString(6, itemId);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Book modified successfully!");
                loadBooks(); // Reload the books table
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error modifying book: " + ex.getMessage());
        }
    }

    private void deleteBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a book to delete.");
            return;
        }

        String itemId = (String) booksTable.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this book?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = Database.getConnection()) {
                String query = "DELETE FROM items WHERE item_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, itemId);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Book deleted successfully!");
                    loadBooks(); // Reload the books table
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error deleting book: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new ManageBooks();
    }
}
