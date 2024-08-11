import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private static final String LIBRARIAN_USERNAME = "librarian";
    private static final String LIBRARIAN_PASSWORD = "password";

    public LoginForm() {
        setTitle("Library System");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(10, 10, 100, 25);
        panel.add(usernameLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(120, 10, 160, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 40, 100, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(120, 40, 160, 25);
        panel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(10, 80, 80, 25);
        panel.add(loginButton);

        add(panel);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        setVisible(true);
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.equals(LIBRARIAN_USERNAME) && password.equals(LIBRARIAN_PASSWORD)) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            new LibrarianDashboard();
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.");
        }
    }

    public static void main(String[] args) {
        new LoginForm();
    }
}
