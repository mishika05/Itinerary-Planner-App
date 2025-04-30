package itineraryplanner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class Reminder extends JPanel {
    private DefaultListModel<String> reminderListModel;
    private JList<String> reminderList;
    private JTextField reminderField;
    private JComboBox<String> priorityBox;
    private JButton addButton, deleteButton, markDoneButton;
    private int currentUserId = 1;
    private final String PLACEHOLDER_TEXT = "Type your reminder here...";
    private final String PRIORITY_PLACEHOLDER = "Select Priority";

    public Reminder() {
        initializeUI();
        loadReminders();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 40, 60));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(new Color(30, 40, 60));
        JLabel titleLabel = new JLabel("Reminders");
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 32));
        titleLabel.setForeground(new Color(220, 230, 240));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(30, 40, 60));

        // Input Panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        inputPanel.setBackground(new Color(30, 40, 60));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        reminderField = new JTextField(25);
        reminderField.setFont(new Font("Open Sans", Font.PLAIN, 16));
        reminderField.setForeground(new Color(150, 150, 150));
        reminderField.setBackground(new Color(70, 80, 100));
        reminderField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 100, 140), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        reminderField.setText(PLACEHOLDER_TEXT);
        reminderField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (reminderField.getText().equals(PLACEHOLDER_TEXT)) {
                    reminderField.setText("");
                    reminderField.setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (reminderField.getText().isEmpty()) {
                    reminderField.setForeground(new Color(150, 150, 150));
                    reminderField.setText(PLACEHOLDER_TEXT);
                }
            }
        });
        inputPanel.add(reminderField);

        String[] priorityLevels = {PRIORITY_PLACEHOLDER, "High", "Medium", "Low"};
        priorityBox = new JComboBox<>(priorityLevels);
        priorityBox.setFont(new Font("Open Sans", Font.PLAIN, 16));
        priorityBox.setForeground(Color.WHITE);
        priorityBox.setBackground(new Color(70, 80, 100));
        priorityBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 100, 140), 1),
                BorderFactory.createEmptyBorder(8, 25, 8, 25)
        ));
        priorityBox.setPreferredSize(new Dimension(220, 40));
        priorityBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                           int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (value.equals(PRIORITY_PLACEHOLDER)) {
                    setForeground(new Color(180, 180, 180));
                } else {
                    setForeground(Color.WHITE);
                }
                return this;
            }
        });
        priorityBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                priorityBox.setForeground(Color.WHITE);
            }
        });
        inputPanel.add(priorityBox);

        addButton = new JButton("+ Add Reminder");
        addButton.setFont(new Font("Montserrat", Font.BOLD, 16));
        addButton.setForeground(Color.WHITE);
        addButton.setBackground(new Color(100, 200, 150));
        addButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(12, 30, 12, 30)
        ));
        addButton.setFocusPainted(false);
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                addButton.setBackground(new Color(120, 220, 170));
            }
            public void mouseExited(MouseEvent evt) {
                addButton.setBackground(new Color(100, 200, 150));
            }
        });
        addButton.addActionListener(e -> addReminder());
        inputPanel.add(addButton);

        contentPanel.add(inputPanel);

        // Reminder List Panel
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(new Color(50, 60, 80));
        listPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 100, 140), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        reminderListModel = new DefaultListModel<>();
        reminderList = new JList<>(reminderListModel);
        reminderList.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                if (isSelectedIndex(index0)) {
                    super.removeSelectionInterval(index0, index1);
                } else {
                    super.setSelectionInterval(index0, index1);
                }
            }
        });
        reminderList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                           int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                setAlignmentX(Component.LEFT_ALIGNMENT); // Ensure full width
                return c;
            }
        });
        reminderList.setFont(new Font("Open Sans", Font.PLAIN, 16));
        reminderList.setBackground(new Color(70, 80, 100));
        reminderList.setForeground(Color.WHITE);
        reminderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reminderList.setFixedCellHeight(40);

        JScrollPane scrollPane = new JScrollPane(reminderList);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        listPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(listPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonPanel.setBackground(new Color(30, 40, 60));

        markDoneButton = createStyledButton("Mark as Done", new Color(255, 180, 50));
        markDoneButton.addActionListener(e -> markReminderDone());
        buttonPanel.add(markDoneButton);

        deleteButton = createStyledButton("Delete Reminder", new Color(220, 80, 80));
        deleteButton.addActionListener(e -> deleteReminder());
        buttonPanel.add(deleteButton);

        contentPanel.add(buttonPanel);
        add(contentPanel, BorderLayout.CENTER);
    }

    private String formatReminderText(String text, String priority, boolean isCompleted) {
        return String.format(
            "<html><div style='width:100%%; display:flex; justify-content:space-between;'>"
            + "<div>[%s] %s</div>"
            + "<div style='color:%s; margin-left:auto;'>%s</div>"
            + "</div></html>",
            priority.toUpperCase(),
            text,
            isCompleted ? "#00FF00" : "transparent",
            isCompleted ? "<b>DONE</b>" : ""
        );
    }

    private void loadReminders() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                showError("Database connection failed");
                return;
            }

            String createSQL;
            String dbProductName = conn.getMetaData().getDatabaseProductName();
            if (dbProductName.equals("MySQL")) {
                createSQL = "CREATE TABLE IF NOT EXISTS reminders (" +
                        "id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                        "user_id INTEGER NOT NULL," +
                        "reminder_text TEXT NOT NULL," +
                        "priority TEXT NOT NULL," +
                        "is_completed BOOLEAN DEFAULT FALSE)";
            } else {
                createSQL = "CREATE TABLE IF NOT EXISTS reminders (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id INTEGER NOT NULL," +
                        "reminder_text TEXT NOT NULL," +
                        "priority TEXT NOT NULL," +
                        "is_completed BOOLEAN DEFAULT FALSE)";
            }
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createSQL);
            }

            String querySQL = "SELECT * FROM reminders WHERE user_id = ? " +
                    "ORDER BY CASE priority " +
                    "WHEN 'High' THEN 1 " +
                    "WHEN 'Medium' THEN 2 " +
                    "ELSE 3 END";

            try (PreparedStatement stmt = conn.prepareStatement(querySQL)) {
                stmt.setInt(1, currentUserId);
                try (ResultSet rs = stmt.executeQuery()) {
                    reminderListModel.clear();
                    while (rs.next()) {
                        String text = rs.getString("reminder_text");
                        String priority = rs.getString("priority");
                        boolean isCompleted = rs.getBoolean("is_completed");

                        String formattedText = formatReminderText(text, priority, isCompleted);
                        reminderListModel.addElement(formattedText);
                    }
                }
            }
        } catch (SQLException e) {
            showError("Error loading reminders: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addReminder() {
        String text = reminderField.getText().trim();

        if (text.isEmpty() || text.equals(PLACEHOLDER_TEXT)) {
            showWarning("Please enter a reminder before adding");
            return;
        }

        String priority = (String) priorityBox.getSelectedItem();
        if (priority == null || priority.equals(PRIORITY_PLACEHOLDER)) {
            showWarning("Please select a priority from the dropdown");
            return;
        }

        String formattedText = formatReminderText(text, priority, false);

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                showError("Database connection failed");
                return;
            }

            String sql = "INSERT INTO reminders (user_id, reminder_text, priority) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, currentUserId);
                stmt.setString(2, text);
                stmt.setString(3, priority);
                stmt.executeUpdate();

                int insertIndex = 0;
                for (; insertIndex < reminderListModel.size(); insertIndex++) {
                    String existing = reminderListModel.get(insertIndex);
                    String existingPriority = existing.substring(existing.indexOf("[") + 1, existing.indexOf("]"));
                    if (getPriorityValue(existingPriority) > getPriorityValue(priority)) {
                        break;
                    }
                }
                reminderListModel.insertElementAt(formattedText, insertIndex);

                reminderField.setForeground(new Color(150, 150, 150));
                reminderField.setText(PLACEHOLDER_TEXT);
                priorityBox.setSelectedItem(PRIORITY_PLACEHOLDER);
            }
        } catch (SQLException e) {
            showError("Failed to save reminder: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void markReminderDone() {
        int selectedIndex = reminderList.getSelectedIndex();
        if (selectedIndex == -1) {
            showWarning("Please select a reminder");
            return;
        }

        String htmlText = reminderListModel.get(selectedIndex);
        if (htmlText.contains("DONE")) {
            return;
        }

        String priority = htmlText.substring(htmlText.indexOf("[") + 1, htmlText.indexOf("]"));
        String text = htmlText.substring(htmlText.indexOf("]") + 2, htmlText.indexOf("<div", htmlText.indexOf("]")));

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                showError("Database connection failed");
                return;
            }

            String sql = "UPDATE reminders SET is_completed = TRUE WHERE user_id = ? AND reminder_text = ? AND priority = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, currentUserId);
                stmt.setString(2, text.trim());
                stmt.setString(3, priority);
                stmt.executeUpdate();

                String updatedText = formatReminderText(text, priority, true);
                reminderListModel.set(selectedIndex, updatedText);
            }
        } catch (SQLException e) {
            showError("Failed to mark reminder as done: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteReminder() {
        int selectedIndex = reminderList.getSelectedIndex();
        if (selectedIndex == -1) {
            showWarning("Please select a reminder");
            return;
        }

        String htmlText = reminderListModel.get(selectedIndex);
        String priority = htmlText.substring(htmlText.indexOf("[") + 1, htmlText.indexOf("]"));
        String text = htmlText.substring(htmlText.indexOf("]") + 2, htmlText.indexOf("<div", htmlText.indexOf("]")));

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                showError("Database connection failed");
                return;
            }

            String sql = "DELETE FROM reminders WHERE user_id = ? AND reminder_text = ? AND priority = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, currentUserId);
                stmt.setString(2, text.trim());
                stmt.setString(3, priority);
                stmt.executeUpdate();
                reminderListModel.remove(selectedIndex);
            }
        } catch (SQLException e) {
            showError("Failed to delete reminder: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int getPriorityValue(String priority) {
        switch (priority.toLowerCase()) {
            case "high": return 1;
            case "medium": return 2;
            default: return 3;
        }
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

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
}