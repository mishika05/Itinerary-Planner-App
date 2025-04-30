package itineraryplanner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Login extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, signupButton;
    private JLabel userError, passError, headerLabel, footerLabel;
    private JPanel panel, formPanel, buttonPanel;
    private ImageIcon logoIcon;
    private JLabel backgroundLabel;

    public Login() {
        initializeUI();
        setupBackground();
        setupHeader();
        setupFormFields();
        setupButtons();
        setupFooter();
        addComponentsToPanel();
        setVisible(true);
    }

    private void initializeUI() {
        setTitle("Itinerary Planner - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        setLocationRelativeTo(null);

        try {
            logoIcon = new ImageIcon(getClass().getResource("/images/logo.png"));
            setIconImage(logoIcon.getImage());
        } catch (Exception e) {
            System.out.println("Logo image not found, using default icon");
        }

        panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
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
            add(backgroundLabel);
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
            add(backgroundLabel);
        }
    }

    private void setupHeader() {
        headerLabel = new JLabel("ITINERARY PLANNER", SwingConstants.CENTER);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Montserrat", Font.BOLD, 48));
        
        if (logoIcon != null) {
            headerLabel.setIcon(new ImageIcon(logoIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
            headerLabel.setIconTextGap(20);
            headerLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
            headerLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        }
    }

    private void setupFormFields() {
        formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 10, 50)); 
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10); 
        gbc.anchor = GridBagConstraints.WEST;

        // Username Field
        JLabel userLabel = createFormLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(userLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Open Sans", Font.PLAIN, 16));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        usernameField.setBackground(new Color(255, 255, 255, 220));
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        userError = createErrorLabel();
        gbc.gridy = 1;
        formPanel.add(userError, gbc);

        // Password Field
        JLabel passLabel = createFormLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(passLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Open Sans", Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        passwordField.setBackground(new Color(255, 255, 255, 220));
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        passError = createErrorLabel();
        gbc.gridy = 3;
        formPanel.add(passError, gbc);
    }

    private void setupButtons() {
        buttonPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 150, 0, 150)); // Reduced top padding

        loginButton = createGradientButton("LOGIN", 
            new Color(39, 174, 96), new Color(46, 204, 113));
        signupButton = createGradientButton("SIGN UP", 
            new Color(52, 152, 219), new Color(41, 128, 185));

        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);
    }

    private void setupFooter() {
        footerLabel = new JLabel("Plan your perfect trip with ease!", SwingConstants.CENTER);
        footerLabel.setForeground(Color.WHITE);
        footerLabel.setFont(new Font("Open Sans", Font.ITALIC, 16));
    }

    private void addComponentsToPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 20, 10); 

        panel.add(headerLabel, gbc);
        panel.add(formPanel, gbc);
        panel.add(buttonPanel, gbc);
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(footerLabel, gbc);

        backgroundLabel.add(panel, BorderLayout.CENTER);
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Open Sans", Font.BOLD, 18));
        label.setPreferredSize(new Dimension(120, 30));
        return label;
    }

    private JLabel createErrorLabel() {
        JLabel label = new JLabel(" ");
        label.setForeground(new Color(231, 76, 60));
        label.setFont(new Font("Open Sans", Font.PLAIN, 12));
        label.setPreferredSize(new Dimension(300, 20));
        return label;
    }

    private JButton createGradientButton(String text, Color color1, Color color2) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setPaint(new GradientPaint(0, 0, color2.darker(), 0, getHeight(), color1.darker()));
                } else if (getModel().isRollover()) {
                    g2.setPaint(new GradientPaint(0, 0, color2.brighter(), 0, getHeight(), color1.brighter()));
                } else {
                    g2.setPaint(new GradientPaint(0, 0, color2, 0, getHeight(), color1));
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();

                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
               
            }
        };

        button.setFont(new Font("Montserrat", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addActionListener(this);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            handleLogin();
        } else if (e.getSource() == signupButton) {
            handleSignup();
        }
    }

    private void handleLogin() {
        try {
            System.out.println("handleLogin started");
            userError.setText(" ");
            passError.setText(" ");

            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            System.out.println("Username: " + username);

            boolean hasError = false;

            if (username.isEmpty()) {
                userError.setText("Username is required");
                hasError = true;
            }
            if (password.isEmpty()) {
                passError.setText("Password is required");
                hasError = true;
            }

            if (hasError) {
                System.out.println("Validation errors detected");
                return;
            }

            Connection conn = DatabaseConnection.getConnection();
            System.out.println("Connection attempt: " + (conn != null ? "Success" : "Failed"));
            if (conn == null) {
                showError("Failed to connect to database. Please check your configuration.");
                return;
            }

            try {
                String sql = "SELECT password FROM users WHERE username = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                try {
                    stmt.setString(1, username);
                    ResultSet rs = stmt.executeQuery();
                    try {
                        System.out.println("Query executed for: " + username);
                        if (rs.next()) {
                            String storedPassword = rs.getString("password");
                            System.out.println("User found: " + username);
                            if (storedPassword != null && password.equals(storedPassword)) {
                                System.out.println("Password matched, opening Dashboard");
                                dispose();
                                Dashboard dashboard = new Dashboard(username);
                                dashboard.setVisible(true);
                            } else {
                                System.out.println("Invalid password");
                                showError("Invalid username or password");
                            }
                        } else {
                            System.out.println("User not found: " + username);
                            showError("User not found");
                        }
                    } finally {
                        rs.close();
                    }
                } finally {
                    stmt.close();
                }
            } finally {
                conn.close();
            }
        } catch (Exception ex) {
            System.err.println("Error in handleLogin: " + ex.getMessage());
            ex.printStackTrace();
            showError("An unexpected error occurred: " + ex.getMessage());
        }
    }

    private void handleSignup() {
        SwingUtilities.invokeLater(() -> {
            new Signup().setVisible(true);
            dispose();
        });
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", 
            JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set professional fonts
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, 
                    Login.class.getResourceAsStream("/fonts/Montserrat-Bold.ttf")));
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, 
                    Login.class.getResourceAsStream("/fonts/OpenSans-Regular.ttf")));
                
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Login();
        });
    }
}