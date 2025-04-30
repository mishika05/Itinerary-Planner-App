package itineraryplanner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.regex.Pattern;

public class Signup extends JFrame implements ActionListener {
    private JTextField fullNameField, usernameField, emailField;
    private JPasswordField passwordField;
    private JButton createButton, backButton;
    private JLabel nameError, userError, passError, emailError, titleLabel, backgroundLabel;
    private JPanel panel, formPanel, buttonPanel;

    public Signup() {
        initializeUI();
        setupBackground();
        setupTitle();
        setupFormFields();
        setupButtons();
        addComponentsToPanel();
        setVisible(true);
    }

    private void initializeUI() {
        setTitle("Itinerary Planner - Sign Up");
        setSize(800, 650);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
    }

    private void setupBackground() {
        try {
            ImageIcon bgIcon = new ImageIcon(getClass().getResource("/images/login_bg.jpg"));
            Image bgImage = bgIcon.getImage().getScaledInstance(
                Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height,
                Image.SCALE_SMOOTH);
            backgroundLabel = new JLabel(new ImageIcon(bgImage));
            backgroundLabel.setLayout(new BorderLayout());
            setContentPane(backgroundLabel);
        } catch (Exception e) {
            backgroundLabel = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    Color color1 = new Color(44, 62, 80);
                    Color color2 = new Color(52, 73, 94);
                    GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            backgroundLabel.setLayout(new BorderLayout());
            setContentPane(backgroundLabel);
        }
    }

    private void setupTitle() {
        titleLabel = new JLabel("CREATE ACCOUNT", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 36));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
    }

    private void setupFormFields() {
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 255, 200)); // Semi-transparent white
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 220, 240), 2),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createFormLabel("Full Name:"), gbc);

        fullNameField = createTextField();
        gbc.gridx = 1;
        formPanel.add(fullNameField, gbc);

        nameError = createErrorLabel();
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(nameError, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(createFormLabel("Username:"), gbc);

        usernameField = createTextField();
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        userError = createErrorLabel();
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(userError, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(createFormLabel("Password:"), gbc);

        passwordField = createPasswordField();
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        passError = createErrorLabel();
        gbc.gridx = 1;
        gbc.gridy = 5;
        formPanel.add(passError, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(createFormLabel("Email:"), gbc);

        emailField = createTextField();
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        emailError = createErrorLabel();
        gbc.gridx = 1;
        gbc.gridy = 7;
        formPanel.add(emailError, gbc);
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(60, 80, 100)); // Dark blue-gray
        label.setFont(new Font("Open Sans", Font.BOLD, 16));
        label.setPreferredSize(new Dimension(120, 30));
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Open Sans", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 220)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setBackground(Color.WHITE);
        field.setForeground(new Color(50, 70, 90)); // Dark text
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Open Sans", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 220)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setBackground(Color.WHITE);
        field.setForeground(new Color(50, 70, 90)); // Dark text
        field.setEchoChar('•');
        return field;
    }

    private JLabel createErrorLabel() {
        JLabel label = new JLabel(" ");
        label.setForeground(new Color(220, 50, 50)); // Bright red
        label.setFont(new Font("Open Sans", Font.PLAIN, 13));
        label.setPreferredSize(new Dimension(300, 20));
        return label;
    }

    private void setupButtons() {
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));

        createButton = createSolidButton("CREATE ACCOUNT", new Color(52, 152, 219)); // Blue
        backButton = createSolidButton("BACK TO LOGIN", new Color(100, 149, 237)); // Cornflower blue

        buttonPanel.add(createButton);
        buttonPanel.add(backButton);
    }

    private JButton createSolidButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Montserrat", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(12, 35, 12, 35)
        ));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(color);
            }
        });

        button.addActionListener(this);
        return button;
    }

    private void addComponentsToPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);

        panel.add(titleLabel, gbc);
        panel.add(formPanel, gbc);
        panel.add(buttonPanel, gbc);

        backgroundLabel.add(panel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createButton) {
            handleCreateAccount();
        } else if (e.getSource() == backButton) {
            handleBackButton();
        }
    }

    private void handleCreateAccount() {
        nameError.setText(" ");
        userError.setText(" ");
        passError.setText(" ");
        emailError.setText(" ");

        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String email = emailField.getText().trim();
        
        if (!validateInputs(fullName, username, password, email)) {
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                showDatabaseError("Failed to connect to database");
                return;
            }

            if (checkExistingUser(conn, username, email)) {
                return;
            }

            if (createNewUser(conn, fullName, username, password, email)) {
                JOptionPane.showMessageDialog(this, 
                    "Account created successfully!\n\nYou can now login with your credentials.", 
                    "Registration Complete", 
                    JOptionPane.INFORMATION_MESSAGE);
                openLoginScreen();
            }
        } catch (SQLException ex) {
            showDatabaseError("Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private boolean validateInputs(String fullName, String username, String password, String email) {
        boolean isValid = true;
        
        if (fullName.isEmpty()) {
            nameError.setText("Please enter your full name");
            isValid = false;
        } else if (fullName.length() < 3) {
            nameError.setText("Name should be at least 3 characters");
            isValid = false;
        }
        
        if (username.isEmpty()) {
            userError.setText("Please choose a username");
            isValid = false;
        } else if (!username.matches("^[A-Za-z][A-Za-z0-9_]{3,}$")) {
            userError.setText("4+ chars starting with letter (a-z, 0-9, _)");
            isValid = false;
        }
        
        if (password.isEmpty()) {
            passError.setText("Please create a password");
            isValid = false;
        } else if (!password.matches("^(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}$")) {
            passError.setText("8+ chars with 1 number and 1 special character");
            isValid = false;
        }
        
        if (email.isEmpty()) {
            emailError.setText("Please enter your email");
            isValid = false;
        } else if (!Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$").matcher(email).matches()) {
            emailError.setText("Please enter a valid email address");
            isValid = false;
        }
        
        return isValid;
    }

    private boolean checkExistingUser(Connection conn, String username, String email) throws SQLException {
        String checkSql = "SELECT * FROM users WHERE username = ? OR email = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, username);
            checkStmt.setString(2, email);
            
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    if (rs.getString("username").equalsIgnoreCase(username)) {
                        userError.setText("Username already taken - try another");
                    }
                    if (rs.getString("email").equalsIgnoreCase(email)) {
                        emailError.setText("Email already registered");
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private boolean createNewUser(Connection conn, String fullName, String username, String password, String email) throws SQLException {
        String insertSql = "INSERT INTO users (full_name, username, password, email) VALUES (?, ?, ?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setString(1, fullName);
            insertStmt.setString(2, username);
            insertStmt.setString(3, password);
            insertStmt.setString(4, email);

            int rowsAffected = insertStmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    private void showDatabaseError(String message) {
        JOptionPane.showMessageDialog(this, 
            "System Error: " + message + "\n\n" +
            "Please check:\n" +
            "1. Database server is running\n" +
            "2. Your network connection\n" +
            "3. Contact support if problem persists", 
            "Database Error", 
            JOptionPane.ERROR_MESSAGE);
    }

    private void handleBackButton() {
        openLoginScreen();
    }

    private void openLoginScreen() {
        this.dispose();
        new Login().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, 
                    Signup.class.getResourceAsStream("/fonts/Montserrat-Bold.ttf")));
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, 
                    Signup.class.getResourceAsStream("/fonts/OpenSans-Regular.ttf")));
                
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            if (DatabaseConnection.testConnection()) {
                new Signup().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, 
                    "Cannot connect to database.\nPlease check your configuration.", 
                    "Connection Error", 
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}