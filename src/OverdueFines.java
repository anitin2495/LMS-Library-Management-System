import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class OverdueFines extends JFrame {
    private JTextField userIdField;
    private JButton calculateFinesButton;

    public OverdueFines() {
        setTitle("Calculate Overdue Fines");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel userIdLabel = new JLabel("User ID:");
        userIdLabel.setBounds(10, 10, 100, 25);
        panel.add(userIdLabel);

        userIdField = new JTextField(20);
        userIdField.setBounds(120, 10, 160, 25);
        panel.add(userIdField);

        calculateFinesButton = new JButton("Calculate Fines");
        calculateFinesButton.setBounds(10, 40, 150, 25);
        panel.add(calculateFinesButton);

        calculateFinesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int userId = Integer.parseInt(userIdField.getText());
                double totalFines = 0;

                try (Connection conn = Database.getConnection()) {
                    String query = "SELECT item_id, item_type, due_date, returned FROM checkouts WHERE user_id = ? AND returned = FALSE";
                    try (PreparedStatement stmt = conn.prepareStatement(query)) {
                        stmt.setInt(1, userId);
                        ResultSet rs = stmt.executeQuery();
                        java.util.Date today = new java.util.Date();
                        java.sql.Date currentDate = new java.sql.Date(today.getTime());

                        while (rs.next()) {
                            java.sql.Date dueDate = rs.getDate("due_date");
                            if (dueDate.before(currentDate)) {
                                long overdueDays = (currentDate.getTime() - dueDate.getTime()) / (1000 * 60 * 60 * 24);
                                double fine = overdueDays * 0.10; // Assume $0.10 per overdue day
                                totalFines += fine;
                            }
                        }

                        JOptionPane.showMessageDialog(null, "Total overdue fines for user " + userId + " is: $" + totalFines);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error calculating fines: " + ex.getMessage());
                }
            }
        });

        add(panel);
        setVisible(true);
    }
}
