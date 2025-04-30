package itineraryplanner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Date;

public class Plan extends JPanel {
    private JTextField destinationField;
    private JSpinner startDateSpinner, endDateSpinner;
    private DefaultListModel<String> itineraryModel;
    private JList<String> itineraryList;
    private JButton addActivityButton, editActivityButton, deleteActivityButton, createTripButton;
    private int currentTripId = -1;

    public Plan() {
        setLayout(new BorderLayout(0, 25));
        setBackground(new Color(30, 40, 60));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(new Color(30, 40, 60));
        JLabel titleLabel = new JLabel("Plan Your Trip");
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 32));
        titleLabel.setForeground(new Color(220, 230, 240));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(30, 40, 60));
        contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align content to left

        // Trip Details Panel - Left aligned
        JPanel tripPanel = new JPanel();
        tripPanel.setLayout(new GridBagLayout());
        tripPanel.setBackground(new Color(50, 60, 80));
        tripPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 100, 140), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        tripPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Left align this panel
        tripPanel.setMaximumSize(new Dimension(1200, Integer.MAX_VALUE));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Destination
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel destinationLabel = new JLabel("Destination:");
        destinationLabel.setFont(new Font("Open Sans", Font.BOLD, 16));
        destinationLabel.setForeground(new Color(200, 210, 230));
        tripPanel.add(destinationLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        destinationField = new JTextField();
        destinationField.setFont(new Font("Open Sans", Font.PLAIN, 16));
        destinationField.setForeground(Color.WHITE);
        destinationField.setBackground(new Color(70, 80, 100));
        destinationField.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        destinationField.setPreferredSize(new Dimension(400, 40));
        tripPanel.add(destinationField, gbc);

        // Start Date
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel startDateLabel = new JLabel("Start Date:");
        startDateLabel.setFont(new Font("Open Sans", Font.BOLD, 16));
        startDateLabel.setForeground(new Color(200, 210, 230));
        tripPanel.add(startDateLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        startDateSpinner = new JSpinner(new SpinnerDateModel());
        startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner, "dd/MM/yyyy"));
        JSpinner.DateEditor startEditor = (JSpinner.DateEditor) startDateSpinner.getEditor();
        startEditor.getTextField().setFont(new Font("Open Sans", Font.PLAIN, 16));
        startEditor.getTextField().setForeground(Color.WHITE);
        startEditor.getTextField().setBackground(new Color(70, 80, 100));
        startEditor.getTextField().setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        startEditor.getTextField().setPreferredSize(new Dimension(400, 40));
        tripPanel.add(startDateSpinner, gbc);

        // End Date
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel endDateLabel = new JLabel("End Date:");
        endDateLabel.setFont(new Font("Open Sans", Font.BOLD, 16));
        endDateLabel.setForeground(new Color(200, 210, 230));
        tripPanel.add(endDateLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        endDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "dd/MM/yyyy"));
        JSpinner.DateEditor endEditor = (JSpinner.DateEditor) endDateSpinner.getEditor();
        endEditor.getTextField().setFont(new Font("Open Sans", Font.PLAIN, 16));
        endEditor.getTextField().setForeground(Color.WHITE);
        endEditor.getTextField().setBackground(new Color(70, 80, 100));
        endEditor.getTextField().setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        endEditor.getTextField().setPreferredSize(new Dimension(400, 40));
        tripPanel.add(endDateSpinner, gbc);

        // Create Trip Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 0, 0);
        
        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonContainer.setBackground(new Color(50, 60, 80));
        
        createTripButton = createStyledButton("CREATE TRIP", new Color(70, 130, 180));
        createTripButton.addActionListener(e -> createTrip());
        
        buttonContainer.add(createTripButton);
        tripPanel.add(buttonContainer, gbc);

        contentPanel.add(tripPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Itinerary Panel - Left aligned
        JPanel itineraryPanel = new JPanel();
        itineraryPanel.setLayout(new BoxLayout(itineraryPanel, BoxLayout.Y_AXIS));
        itineraryPanel.setBackground(new Color(50, 60, 80));
        itineraryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 100, 140), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        itineraryPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Left align this panel
        itineraryPanel.setMaximumSize(new Dimension(1200, Integer.MAX_VALUE));

        JLabel itineraryLabel = new JLabel("Itinerary:");
        itineraryLabel.setFont(new Font("Montserrat", Font.BOLD, 24));
        itineraryLabel.setForeground(new Color(220, 230, 240));
        itineraryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        itineraryPanel.add(itineraryLabel);
        itineraryPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        itineraryModel = new DefaultListModel<>();
        itineraryList = new JList<>(itineraryModel);
        itineraryList.setFont(new Font("Open Sans", Font.PLAIN, 16));
        itineraryList.setBackground(new Color(70, 80, 100));
        itineraryList.setForeground(Color.WHITE);
        itineraryList.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        itineraryList.setFixedCellHeight(40);

        JScrollPane scrollPane = new JScrollPane(itineraryList);
        scrollPane.setPreferredSize(new Dimension(1100, 200));
        scrollPane.setMaximumSize(new Dimension(1100, 200));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        itineraryPanel.add(scrollPane);
        itineraryPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Activity Buttons Panel - Centered within the left-aligned itinerary panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10));
        buttonPanel.setBackground(new Color(50, 60, 80));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(1200, 80));

        addActivityButton = createStyledButton("Add Activity", new Color(70, 180, 120));
        editActivityButton = createStyledButton("Edit Activity", new Color(255, 180, 50));
        deleteActivityButton = createStyledButton("Delete Activity", new Color(220, 80, 80));

        buttonPanel.add(addActivityButton);
        buttonPanel.add(editActivityButton);
        buttonPanel.add(deleteActivityButton);
        itineraryPanel.add(buttonPanel);

        contentPanel.add(itineraryPanel);
        add(contentPanel, BorderLayout.CENTER);

        // Button listeners
        addActivityButton.addActionListener(e -> addActivity());
        editActivityButton.addActionListener(e -> editActivity());
        deleteActivityButton.addActionListener(e -> deleteActivity());

        loadExistingTrip();
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Montserrat", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(12, 30, 12, 30)
        ));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Removed fixed size to allow text to determine button width
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(bgColor.brighter());
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createRaisedBevelBorder(),
                    BorderFactory.createEmptyBorder(12, 30, 12, 30)
                ));
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(bgColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createRaisedBevelBorder(),
                    BorderFactory.createEmptyBorder(12, 30, 12, 30)
                ));
            }
        });
        
        return button;
    }

    private void loadExistingTrip() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM trips ORDER BY trip_id DESC LIMIT 1";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                currentTripId = rs.getInt("trip_id");
                destinationField.setText(rs.getString("destination"));
                startDateSpinner.setValue(rs.getDate("start_date"));
                endDateSpinner.setValue(rs.getDate("end_date"));
                
                String activitySql = "SELECT * FROM itinerary_activities WHERE trip_id = ?";
                PreparedStatement activityStmt = conn.prepareStatement(activitySql);
                activityStmt.setInt(1, currentTripId);
                ResultSet activityRs = activityStmt.executeQuery();
                
                while (activityRs.next()) {
                    itineraryModel.addElement(activityRs.getString("activity_name"));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading trip data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void createTrip() {
        String destination = destinationField.getText().trim();
        Date startDate = (Date) startDateSpinner.getValue();
        Date endDate = (Date) endDateSpinner.getValue();
        
        if (destination.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a destination.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (startDate.after(endDate)) {
            JOptionPane.showMessageDialog(this, "End date must be after start date.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Could not connect to database", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (currentTripId == -1) {
                String sql = "INSERT INTO trips (user_id, destination, start_date, end_date) VALUES (1, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, destination);
                stmt.setDate(2, new java.sql.Date(startDate.getTime()));
                stmt.setDate(3, new java.sql.Date(endDate.getTime()));
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        currentTripId = rs.getInt(1);
                    }
                }
            } else {
                String sql = "UPDATE trips SET destination = ?, start_date = ?, end_date = ? WHERE trip_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, destination);
                stmt.setDate(2, new java.sql.Date(startDate.getTime()));
                stmt.setDate(3, new java.sql.Date(endDate.getTime()));
                stmt.setInt(4, currentTripId);
                stmt.executeUpdate();
            }
            
            JOptionPane.showMessageDialog(this, "Trip " + (currentTripId == -1 ? "Created" : "Updated") + " Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving trip: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addActivity() {
        String activity = JOptionPane.showInputDialog(this, "Enter Activity:");
        if (activity == null || activity.trim().isEmpty()) {
            System.out.println("Activity input cancelled or empty");
            return;
        }

        if (currentTripId == -1) {
            JOptionPane.showMessageDialog(this, "Please create a trip first before adding activities.", "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("No trip created (currentTripId = -1)");
            return;
        }

        itineraryModel.addElement(activity);
        System.out.println("Activity added to UI: " + activity);

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Could not connect to database.", "Error", JOptionPane.ERROR_MESSAGE);
                itineraryModel.removeElement(activity);
                System.out.println("Database connection failed");
                return;
            }

            String sql = "INSERT INTO itinerary_activities (trip_id, activity_name, activity_date) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, currentTripId);
                stmt.setString(2, activity);
                stmt.setDate(3, new java.sql.Date(System.currentTimeMillis())); // Set current date as default
                int rowsAffected = stmt.executeUpdate();
                System.out.println("Database insert successful, rows affected: " + rowsAffected);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving activity to database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            itineraryModel.removeElement(activity);
            System.err.println("SQL Error in addActivity: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void editActivity() {
        int selectedIndex = itineraryList.getSelectedIndex();
        if (selectedIndex != -1) {
            String currentActivity = itineraryModel.getElementAt(selectedIndex);
            String updatedActivity = JOptionPane.showInputDialog(this, "Edit Activity:", currentActivity);
            
            if (updatedActivity != null && !updatedActivity.trim().isEmpty()) {
                itineraryModel.setElementAt(updatedActivity, selectedIndex);
                
                if (currentTripId != -1) {
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        String sql = "UPDATE itinerary_activities SET activity_name = ? WHERE trip_id = ? AND activity_name = ?";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setString(1, updatedActivity);
                        stmt.setInt(2, currentTripId);
                        stmt.setString(3, currentActivity);
                        stmt.executeUpdate();
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(this, "Error updating activity: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        e.printStackTrace();
                        itineraryModel.setElementAt(currentActivity, selectedIndex);
                    }
                }
            }
        }
    }

    private void deleteActivity() {
        int selectedIndex = itineraryList.getSelectedIndex();
        if (selectedIndex != -1) {
            String activity = itineraryModel.getElementAt(selectedIndex);
            itineraryModel.remove(selectedIndex);
            
            if (currentTripId != -1) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "DELETE FROM itinerary_activities WHERE trip_id = ? AND activity_name = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, currentTripId);
                    stmt.setString(2, activity);
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error deleting activity: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                    itineraryModel.add(selectedIndex, activity);
                }
            }
        }
    }
}