package itineraryplanner;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Pack extends JPanel {
    private DefaultListModel<String> packingListModel;
    private JList<String> packingList;
    private JTextField itemField;
    private JButton addButton, removeButton, clearButton;
    private int currentTripId = -1;
    private final String PLACEHOLDER_TEXT = "Write your items here..."; // Placeholder text

    public Pack() {
        setLayout(null); // Use null layout for precise control
        setBackground(new Color(30, 40, 60)); // Match Budget background
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Add padding

        // Title
        JLabel titleLabel = new JLabel("Packing List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(220, 230, 240)); // Light gray text
        titleLabel.setBounds(0, 20, getWidth(), 40);
        add(titleLabel);

        // List to store packing items
        packingListModel = new DefaultListModel<>();
        packingList = new JList<>(packingListModel);
        packingList.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        packingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        packingList.setBackground(new Color(50, 60, 80)); // Match Budget table background
        packingList.setForeground(new Color(220, 230, 240)); // Light gray text
        packingList.setFixedCellHeight(30); // Increase row height for readability

        // Custom cell renderer for alternating row colors and padding
        packingList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setBackground(index % 2 == 0 ? new Color(50, 60, 80) : new Color(60, 70, 90)); // Alternating colors
                if (isSelected) {
                    c.setBackground(new Color(70, 120, 255)); // Highlight color
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // Padding
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(packingList);
        int xLabel = 50;
        scrollPane.setBounds(xLabel, 80, getWidth() - 2 * xLabel, 400); // Fit panel width minus margins
        scrollPane.setBackground(new Color(50, 60, 80));
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 80, 100), 1, true),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        add(scrollPane);

        // Input panel for adding items
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0)); // Center buttons with 10px gap
        inputPanel.setBackground(new Color(30, 40, 60));

        itemField = new JTextField();
        itemField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        itemField.setForeground(new Color(150, 160, 170)); // Lighter gray for placeholder
        itemField.setBackground(new Color(50, 60, 80));
        itemField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 80, 100), 1, true), // Rounded border
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        itemField.setPreferredSize(new Dimension(300, 40)); // Increased height to 40px to accommodate font and padding
        itemField.setText(PLACEHOLDER_TEXT); // Set placeholder text

        // Add FocusListener to handle placeholder text
        itemField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (itemField.getText().equals(PLACEHOLDER_TEXT)) {
                    itemField.setText("");
                    itemField.setForeground(new Color(220, 230, 240)); // Normal text color
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (itemField.getText().isEmpty()) {
                    itemField.setText(PLACEHOLDER_TEXT);
                    itemField.setForeground(new Color(150, 160, 170)); // Lighter gray for placeholder
                }
            }
        });

        inputPanel.add(itemField);

        addButton = createStyledButton("Add Item");
        addButton.setToolTipText("Add a new item to the packing list");
        inputPanel.add(addButton);

        removeButton = createStyledButton("Remove Item");
        removeButton.setToolTipText("Remove the selected item from the list");
        inputPanel.add(removeButton);

        clearButton = createStyledButton("Clear List");
        clearButton.setToolTipText("Clear all items from the packing list");
        inputPanel.add(clearButton);

        // Calculate the width of the inputPanel based on its components
        int inputPanelWidth = 300 + 10 + 120 + 10 + 120 + 10 + 120 + 10; // itemField + gap + buttons + gaps
        inputPanel.setBounds((getWidth() - inputPanelWidth) / 2, 500, inputPanelWidth, 60); // Center the panel
        add(inputPanel);

        // Add button functionality
        addButton.addActionListener(e -> {
            String item = itemField.getText().trim();
            if (!item.isEmpty() && !item.equals(PLACEHOLDER_TEXT)) { // Ignore placeholder text
                packingListModel.addElement("❌ " + item); // Add item with ❌
                savePackingItem(item, false);
                itemField.setText("");
                itemField.setForeground(new Color(150, 160, 170)); // Reset to placeholder color
                itemField.setText(PLACEHOLDER_TEXT); // Reset placeholder
            }
        });

        // Remove selected item
        removeButton.addActionListener(e -> {
            int selectedIndex = packingList.getSelectedIndex();
            if (selectedIndex != -1) {
                String item = packingListModel.getElementAt(selectedIndex).substring(2);
                packingListModel.remove(selectedIndex);
                deletePackingItem(item);
            }
        });

        // Clear all items with confirmation
        clearButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear all items from the packing list?",
                "Confirm Clear",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                packingListModel.clear();
                clearPackingItems();
            }
        });

        // Double click to mark as packed ✅
        packingList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Double click
                    int index = packingList.locationToIndex(evt.getPoint());
                    if (index != -1) {
                        String item = packingListModel.getElementAt(index);
                        if (item.startsWith("❌")) {
                            packingListModel.set(index, "✅ " + item.substring(2)); // Mark as packed
                            updatePackingItem(item.substring(2), true);
                        } else if (item.startsWith("✅")) {
                            packingListModel.set(index, "❌ " + item.substring(2)); // Mark as unpacked
                            updatePackingItem(item.substring(2), false);
                        }
                    }
                }
            }
        });

        // Load existing packing items
        loadPackingItems();

        // Resize listener to adjust components dynamically
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evt) {
                int panelWidth = getWidth() - 2 * xLabel;
                titleLabel.setBounds(0, 20, getWidth(), 40);
                scrollPane.setBounds(xLabel, 80, Math.max(700, panelWidth), 400);
                // Recalculate inputPanel position to keep it centered
                inputPanel.setBounds((getWidth() - inputPanelWidth) / 2, 500, inputPanelWidth, 60);
            }
        });
    }

    private void loadPackingItems() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT trip_id FROM trips LIMIT 1";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                currentTripId = rs.getInt("trip_id");
                
                String itemSql = "SELECT * FROM packing_items WHERE trip_id = ?";
                PreparedStatement itemStmt = conn.prepareStatement(itemSql);
                itemStmt.setInt(1, currentTripId);
                ResultSet itemRs = itemStmt.executeQuery();
                
                while (itemRs.next()) {
                    String item = itemRs.getString("item_name");
                    boolean isPacked = itemRs.getBoolean("is_packed");
                    packingListModel.addElement((isPacked ? "✅ " : "❌ ") + item);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading packing items: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void savePackingItem(String itemName, boolean isPacked) {
        if (currentTripId == -1) return;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO packing_items (trip_id, item_name, is_packed) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, currentTripId);
            stmt.setString(2, itemName);
            stmt.setBoolean(3, isPacked);
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving packing item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updatePackingItem(String itemName, boolean isPacked) {
        if (currentTripId == -1) return;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE packing_items SET is_packed = ? WHERE trip_id = ? AND item_name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setBoolean(1, isPacked);
            stmt.setInt(2, currentTripId);
            stmt.setString(3, itemName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating packing item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deletePackingItem(String itemName) {
        if (currentTripId == -1) return;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM packing_items WHERE trip_id = ? AND item_name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, currentTripId);
            stmt.setString(2, itemName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting packing item: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void clearPackingItems() {
        if (currentTripId == -1) return;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM packing_items WHERE trip_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, currentTripId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error clearing packing items: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(100, 150, 255), 0, getHeight(), new Color(50, 100, 200));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(new Color(220, 230, 240));
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setFocusPainted(false);
        return button;
    }
}