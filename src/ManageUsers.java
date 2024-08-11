import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ManageUsers extends JFrame {
    private JTabbedPane tabbedPane;
    private JPanel addUserPanel;
    private JPanel viewUsersPanel;
    private JTextField nameField;
    private JTextField addressField;
    private JTextField phoneNumberField;
    private JTextField ageField;
    private JButton addUserButton;
    private JButton refreshUsersButton;
    private JButton modifyUserButton;
    private JButton deleteUserButton;
    private JTable usersTable;

    public ManageUsers() {
        setTitle("Manage Users");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        // Add User Panel
        addUserPanel = new JPanel();
        addUserPanel.setLayout(null);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(10, 10, 100, 25);
        addUserPanel.add(nameLabel);

        nameField = new JTextField(20);
        nameField.setBounds(120, 10, 160, 25);
        addUserPanel.add(nameField);

        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setBounds(10, 40, 100, 25);
        addUserPanel.add(addressLabel);

        addressField = new JTextField(20);
        addressField.setBounds(120, 40, 160, 25);
        addUserPanel.add(addressField);

        JLabel phoneNumberLabel = new JLabel("Phone Number:");
        phoneNumberLabel.setBounds(10, 70, 100, 25);
        addUserPanel.add(phoneNumberLabel);

        phoneNumberField = new JTextField(20);
        phoneNumberField.setBounds(120, 70, 160, 25);
        addUserPanel.add(phoneNumberField);

        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setBounds(10, 100, 100, 25);
        addUserPanel.add(ageLabel);

        ageField = new JTextField(20);
        ageField.setBounds(120, 100, 160, 25);
        addUserPanel.add(ageField);

        addUserButton = new JButton("Add User");
        addUserButton.setBounds(10, 130, 120, 25);
        addUserPanel.add(addUserButton);

        addUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });

        tabbedPane.addTab("Add User", addUserPanel);

        // View Users Panel
        viewUsersPanel = new JPanel();
        viewUsersPanel.setLayout(new BorderLayout());

        usersTable = new JTable();
        JScrollPane userScrollPane = new JScrollPane(usersTable);
        viewUsersPanel.add(userScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        refreshUsersButton = new JButton("Refresh Users");
        modifyUserButton = new JButton("Modify User");
        deleteUserButton = new JButton("Delete User");

        buttonPanel.add(refreshUsersButton);
        buttonPanel.add(modifyUserButton);
        buttonPanel.add(deleteUserButton);

        viewUsersPanel.add(buttonPanel, BorderLayout.SOUTH);

        refreshUsersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadUsers();
            }
        });

        modifyUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                modifyUser();
            }
        });

        deleteUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteUser();
            }
        });

        tabbedPane.addTab("View Users", viewUsersPanel);

        add(tabbedPane);

        setVisible(true);
        loadUsers(); // Load users initially
    }

    private void addUser() {
        String name = nameField.getText();
        String address = addressField.getText();
        String phoneNumber = phoneNumberField.getText();
        String age = ageField.getText();

        // Generate the user ID and library card number
        int userId = generateUserId();
        String libraryCardNumber = generateLibraryCardNumber();

        try (Connection conn = Database.getConnection()) {
            String query = "INSERT INTO users (user_id, name, address, phone_number, library_card_number, age) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setString(2, name);
                stmt.setString(3, address);
                stmt.setString(4, phoneNumber);
                stmt.setString(5, libraryCardNumber);
                stmt.setInt(6, Integer.parseInt(age)); // Assuming age is numeric
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "User added successfully! User ID: " + userId + ", Library Card Number: " + libraryCardNumber);
                nameField.setText("");
                addressField.setText("");
                phoneNumberField.setText("");
                ageField.setText("");
                loadUsers(); // Reload the users table
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error adding user: " + ex.getMessage());
        }
    }

    private void modifyUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow >= 0) {
            int userId = (int) usersTable.getValueAt(selectedRow, 0);
            String name = JOptionPane.showInputDialog("Enter new name:");
            String address = JOptionPane.showInputDialog("Enter new address:");
            String phoneNumber = JOptionPane.showInputDialog("Enter new phone number:");
            String age = JOptionPane.showInputDialog("Enter new age:");

            try (Connection conn = Database.getConnection()) {
                String query = "UPDATE users SET name = ?, address = ?, phone_number = ?, age = ? WHERE user_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, name);
                    stmt.setString(2, address);
                    stmt.setString(3, phoneNumber);
                    stmt.setInt(4, Integer.parseInt(age));
                    stmt.setInt(5, userId);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, "User modified successfully!");
                    loadUsers(); // Reload the users table
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error modifying user: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a user to modify.");
        }
    }

    private void deleteUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow >= 0) {
            int userId = (int) usersTable.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this user?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = Database.getConnection()) {
                    String query = "DELETE FROM users WHERE user_id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setInt(1, userId);
                        stmt.executeUpdate();
                        JOptionPane.showMessageDialog(null, "User deleted successfully!");
                        loadUsers(); // Reload the users table
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error deleting user: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a user to delete.");
        }
    }

    private int generateUserId() {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT MAX(user_id) AS max_id FROM users";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                if (rs.next()) {
                    int maxId = rs.getInt("max_id");
                    return maxId + 1;
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error generating user ID: " + ex.getMessage());
        }
        return 1; // Default to 1 if there's an issue
    }

    private String generateLibraryCardNumber() {
        // Initial prefix for the library card number
        String prefix = "ALK24";

        try (Connection conn = Database.getConnection()) {
            String query = "SELECT COUNT(*) AS count FROM users";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    // Generate a unique library card number by appending the count of users
                    return prefix + String.format("%04d", count + 1);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error generating library card number: " + ex.getMessage());
        }
        return prefix + "001"; // Default to ALK240001 if there's an issue
    }

    private void loadUsers() {
        try (Connection conn = Database.getConnection()) {
            // Check if the `checked_out_books` table exists
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "checked_out_books", null);
            boolean tableExists = tables.next();

            String query;
            if (tableExists) {
                // Table exists, use a LEFT JOIN to get the count of checked-out books
                query = "SELECT u.user_id, u.name, u.address, u.phone_number, u.library_card_number, u.age " +
                        "FROM users u";
            } else {
                // Table doesn't exist, get user details only
                query = "SELECT user_id, name, address, phone_number, library_card_number, age FROM users";
            }

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                DefaultTableModel model = new DefaultTableModel(
                        new String[]{"User ID", "Name", "Address", "Phone Number", "Library Card Number", "Age"}, 0);

                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String name = rs.getString("name");
                    String address = rs.getString("address");
                    String phoneNumber = rs.getString("phone_number");
                    String libraryCardNumber = rs.getString("library_card_number");
                    int age = rs.getInt("age");

                    model.addRow(new Object[]{
                            userId, name, address, phoneNumber, libraryCardNumber, age
                    });
                }
                usersTable.setModel(model);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading users: " + ex.getMessage());
        }
    }





    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManageUsers());
    }
}
