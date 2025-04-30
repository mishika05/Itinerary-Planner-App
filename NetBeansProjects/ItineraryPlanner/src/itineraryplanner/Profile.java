package itineraryplanner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class Profile extends JPanel {
    private JTextField nameField, usernameField, emailField;
    private JSpinner dobSpinner;
    private JRadioButton maleButton, femaleButton;
    private JButton editButton, saveButton;
    private String currentUsername;
    private boolean isEditMode = false;
    private boolean hasDobColumn = true;
    private boolean hasGenderColumn = true;

    // Custom label with text shadow
    private class ShadowLabel extends JLabel {
        public ShadowLabel(String text) {
            super(text);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Get font metrics for proper text positioning
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

            // Draw shadow
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.drawString(getText(), x + 2, y + 2);

            // Draw main text
            g2d.setColor(getForeground());
            g2d.drawString(getText(), x, y);

            g2d.dispose();
            super.paintComponent(g);
        }
    }

    public Profile(String username) {
        this.currentUsername = username;
        setLayout(new GridBagLayout());
        setBackground(new Color(30, 40, 60));
        setPreferredSize(new Dimension(1200, 700));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 25, 15, 25);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;

        ShadowLabel titleLabel = new ShadowLabel("Welcome, " + username + "!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(new Color(220, 230, 240));
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 20, 30, 20);
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        gbc.insets = new Insets(15, 25, 15, 15);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridwidth = 1;
        gbc.gridy++;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 20);
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 18);

        add(createLabel("Name:", labelFont), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        nameField = createTextField(inputFont);
        add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        add(createLabel("Username:", labelFont), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        usernameField = createTextField(inputFont);
        add(usernameField, gbc);

        checkColumnExistence();

        if (hasDobColumn) {
            gbc.gridx = 0;
            gbc.gridy++;
            gbc.anchor = GridBagConstraints.EAST;
            add(createLabel("Date of Birth:", labelFont), gbc);
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            dobSpinner = new JSpinner(new SpinnerDateModel());
            JSpinner.DateEditor editor = new JSpinner.DateEditor(dobSpinner, "dd/MM/yyyy");
            dobSpinner.setEditor(editor);
            dobSpinner.setFont(inputFont);
            dobSpinner.setBackground(new Color(70, 80, 100));
            dobSpinner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 100, 140), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            add(dobSpinner, gbc);
        }

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        add(createLabel("Email Address:", labelFont), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        emailField = createTextField(inputFont);
        add(emailField, gbc);

        if (hasGenderColumn) {
            gbc.gridx = 0;
            gbc.gridy++;
            gbc.anchor = GridBagConstraints.EAST;
            add(createLabel("Gender:", labelFont), gbc);
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            maleButton = new JRadioButton("Male");
            femaleButton = new JRadioButton("Female");
            maleButton.setFont(inputFont);
            femaleButton.setFont(inputFont);
            maleButton.setBackground(new Color(30, 40, 60));
            femaleButton.setBackground(new Color(30, 40, 60));
            maleButton.setForeground(Color.WHITE);
            femaleButton.setForeground(Color.WHITE);
            maleButton.setFocusPainted(false);
            femaleButton.setFocusPainted(false);
            ButtonGroup genderGroup = new ButtonGroup();
            genderGroup.add(maleButton);
            genderGroup.add(femaleButton);
            JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            genderPanel.setBackground(new Color(50, 60, 80));
            genderPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            genderPanel.add(maleButton);
            genderPanel.add(femaleButton);
            add(genderPanel, gbc);
        }

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        editButton = createButton("Edit", new Color(0, 102, 204));
        saveButton = createButton("Save", new Color(72, 191, 145));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(new Color(30, 40, 60));
        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, gbc);

        loadUserData();
        disableFields();

        editButton.addActionListener(e -> enableFields());
        saveButton.addActionListener(e -> saveUserData());
    }

    private void checkColumnExistence() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            
            // Check for dob column
            ResultSet dobColumns = meta.getColumns(null, null, "users", "dob");
            hasDobColumn = dobColumns.next();
            
            // Check for gender column
            ResultSet genderColumns = meta.getColumns(null, null, "users", "gender");
            hasGenderColumn = genderColumns.next();
            
        } catch (SQLException e) {
            hasDobColumn = false;
            hasGenderColumn = false;
            System.err.println("Error checking columns: " + e.getMessage());
        }
    }

    private void loadUserData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Build query based on available columns
            StringBuilder sqlBuilder = new StringBuilder("SELECT full_name, username, email");
            if (hasDobColumn) sqlBuilder.append(", dob");
            if (hasGenderColumn) sqlBuilder.append(", gender");
            sqlBuilder.append(" FROM users WHERE username = ?");
            
            PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString());
            stmt.setString(1, currentUsername);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("full_name"));
                usernameField.setText(rs.getString("username"));
                emailField.setText(rs.getString("email"));
                
                if (hasDobColumn && rs.getObject("dob") != null) {
                    dobSpinner.setValue(rs.getDate("dob"));
                }
                
                if (hasGenderColumn) {
                    String gender = rs.getString("gender");
                    if (gender != null) {
                        if (gender.equals("Male")) {
                            maleButton.setSelected(true);
                        } else if (gender.equals("Female")) {
                            femaleButton.setSelected(true);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading user data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void saveUserData() {
        String fullName = nameField.getText().trim();
        String email = emailField.getText().trim();
        String gender = hasGenderColumn ? 
                       (maleButton != null && maleButton.isSelected() ? "Male" : 
                       (femaleButton != null && femaleButton.isSelected() ? "Female" : null)) : null;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Build update query based on available columns
            StringBuilder sqlBuilder = new StringBuilder("UPDATE users SET full_name = ?, email = ?");
            if (hasDobColumn) sqlBuilder.append(", dob = ?");
            if (hasGenderColumn) sqlBuilder.append(", gender = ?");
            sqlBuilder.append(" WHERE username = ?");
            
            PreparedStatement stmt = conn.prepareStatement(sqlBuilder.toString());
            int paramIndex = 1;
            stmt.setString(paramIndex++, fullName);
            stmt.setString(paramIndex++, email);
            
            if (hasDobColumn) {
                stmt.setDate(paramIndex++, new java.sql.Date(((java.util.Date)dobSpinner.getValue()).getTime()));
            }
            
            if (hasGenderColumn) {
                stmt.setString(paramIndex++, gender);
            }
            
            stmt.setString(paramIndex, currentUsername);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                disableFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(new Color(220, 230, 240));
        return label;
    }

    private JTextField createTextField(Font font) {
        JTextField textField = new JTextField(15); // Reduced from 20 to 15 columns
        textField.setFont(font);
        textField.setBackground(new Color(70, 80, 100));
        textField.setForeground(Color.WHITE);
        textField.setPreferredSize(new Dimension(250, 35)); // Fixed width for compactness
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 100, 140), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return textField;
    }

    private JButton createButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bgColor = getModel().isRollover() ? new Color(
                    Math.min(baseColor.getRed() + 20, 255),
                    Math.min(baseColor.getGreen() + 20, 255),
                    Math.min(baseColor.getBlue() + 20, 255)
                ) : baseColor;
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(
                        Math.min(bgColor.getRed() + 30, 255),
                        Math.min(bgColor.getGreen() + 30, 255),
                        Math.min(bgColor.getBlue() + 30, 255)
                    ),
                    0, getHeight(), bgColor
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                super.paintComponent(g2d);
                g2d.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(80, 100, 140));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2d.dispose();
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(160, 45)); // Slightly larger for prominence
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 100, 140), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Add shadow effect via border
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(2, 2, 5, 2), // Shadow-like offset
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 50), 1),
                BorderFactory.createLineBorder(new Color(80, 100, 140), 1)
            )
        ));
        return button;
    }

    private void enableFields() {
        nameField.setEditable(true);
        emailField.setEditable(true);
        if (hasDobColumn) dobSpinner.setEnabled(true);
        if (hasGenderColumn) {
            maleButton.setEnabled(true);
            femaleButton.setEnabled(true);
        }
        isEditMode = true;
    }

    private void disableFields() {
        nameField.setEditable(false);
        emailField.setEditable(false);
        if (hasDobColumn) dobSpinner.setEnabled(false);
        if (hasGenderColumn) {
            maleButton.setEnabled(false);
            femaleButton.setEnabled(false);
        }
        isEditMode = false;
    }
}