package itineraryplanner;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class Budget extends JPanel {
    private JTextField budgetField, expenseField, descriptionField;
    private JComboBox<String> categoryBox;
    private DefaultTableModel tableModel;
    private JLabel totalBudgetLabel, remainingBudgetLabel;
    private double totalBudget = 0, remainingBudget = 0;
    private int currentTripId = -1;

    public Budget() {
        setLayout(null);
        setBackground(new Color(30, 40, 60));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Budget Planner", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(220, 230, 240));
        titleLabel.setBounds(0, 20, getWidth(), 40);
        add(titleLabel);

        int xLabel = 50, xField = 200, widthField = 300, height = 30, gap = 20;
        int yPosition = 100;

        // Total Budget
        addLabel("Total Budget (₹):", xLabel, yPosition);
        budgetField = addTextField(xField, yPosition, widthField);
        JButton setBudgetButton = createStyledButton("Set Budget");
        setBudgetButton.setBounds(xField + widthField + 10, yPosition, 120, height);
        add(setBudgetButton);
        yPosition += height + gap;

        // Expense Amount
        addLabel("Expense (₹):", xLabel, yPosition);
        expenseField = addTextField(xField, yPosition, widthField);
        yPosition += height + gap;

        // Category
        addLabel("Category:", xLabel, yPosition);
        String[] categories = {"Transport", "Accommodation", "Food", "Activities", "Shopping", "Miscellaneous"};
        categoryBox = new JComboBox<>(categories);
        categoryBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        categoryBox.setForeground(Color.BLACK);
        categoryBox.setBackground(new Color(240, 240, 240));
        categoryBox.setOpaque(true);

        categoryBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setForeground(Color.BLACK);
                if (isSelected) {
                    setBackground(new Color(180, 180, 255));
                } else {
                    setBackground(Color.WHITE);
                }
                return this;
            }
        });

        Component editor = categoryBox.getEditor().getEditorComponent();
        if (editor instanceof JTextField) {
            JTextField editorField = (JTextField) editor;
            editorField.setForeground(Color.BLACK);
            editorField.setBackground(new Color(240, 240, 240));
            editorField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        }

        categoryBox.setBounds(xField, yPosition, widthField, height);
        add(categoryBox);
        yPosition += height + gap;

        // Description
        addLabel("Description:", xLabel, yPosition);
        descriptionField = addTextField(xField, yPosition, widthField);
        yPosition += height + gap;

        // Buttons
        JButton addExpenseButton = createStyledButton("Add Expense");
        addExpenseButton.setBounds(xLabel, yPosition, 220, height);
        add(addExpenseButton);

        JButton deleteExpenseButton = createStyledButton("Delete Expense");
        deleteExpenseButton.setBounds(xLabel + 220 + 10, yPosition, 220, height);
        add(deleteExpenseButton);
        yPosition += height + gap * 2;

        // Expense Table
        String[] columns = {"Amount", "Category", "Description", "Date", "ID"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setBackground(new Color(50, 60, 80));
        table.setForeground(new Color(220, 230, 240));
        table.setRowHeight(30);
        table.setGridColor(new Color(70, 80, 100));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        table.getTableHeader().setForeground(new Color(220, 230, 240));
        table.getTableHeader().setBackground(new Color(40, 50, 70));

        table.getColumnModel().getColumn(4).setMinWidth(0);
        table.getColumnModel().getColumn(4).setMaxWidth(0);
        table.getColumnModel().getColumn(4).setWidth(0);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(row % 2 == 0 ? new Color(50, 60, 80) : new Color(60, 70, 90));
                if (isSelected) {
                    c.setBackground(new Color(70, 120, 255));
                }
                c.setForeground(new Color(220, 230, 240));
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        int tableWidth = getWidth() - 2 * xLabel;
        scrollPane.setBounds(xLabel, yPosition, tableWidth > 650 ? tableWidth : 650, 200);
        scrollPane.setBackground(new Color(50, 60, 80));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(70, 80, 100)));
        add(scrollPane);
        yPosition += 220;

        // Budget Summary
        totalBudgetLabel = addLabel("Total Budget: ₹0.00", xLabel, yPosition);
        totalBudgetLabel.setBounds(xLabel, yPosition, 300, 30);
        remainingBudgetLabel = addLabel("Remaining Budget: ₹0.00", xLabel + 350, yPosition);
        remainingBudgetLabel.setBounds(xLabel + 350, yPosition, 300, 30);

        // Button Actions
        setBudgetButton.addActionListener(e -> setBudget());
        addExpenseButton.addActionListener(e -> addExpense());
        deleteExpenseButton.addActionListener(e -> deleteExpense(table));

        loadTripBudget();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                int panelWidth = getWidth() - 2 * xLabel;
                scrollPane.setBounds(xLabel, scrollPane.getY(), Math.max(650, panelWidth), 200);
                titleLabel.setBounds(0, 20, getWidth(), 40);
            }
        });
    }

    private JLabel addLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        label.setForeground(new Color(220, 230, 240));
        label.setBounds(x, y, 300, 30);
        add(label);
        return label;
    }

    private JTextField addTextField(int x, int y, int width) {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textField.setForeground(new Color(220, 230, 240));
        textField.setBackground(new Color(50, 60, 80));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 80, 100), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        textField.setBounds(x, y, width, 30);
        add(textField);
        return textField;
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

    private void loadTripBudget() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String tripSql = "SELECT trip_id FROM trips LIMIT 1";
            Statement tripStmt = conn.createStatement();
            ResultSet tripRs = tripStmt.executeQuery(tripSql);
            
            if (tripRs.next()) {
                currentTripId = tripRs.getInt("trip_id");
                
                String budgetSql = "SELECT * FROM budgets WHERE trip_id = ?";
                PreparedStatement budgetStmt = conn.prepareStatement(budgetSql);
                budgetStmt.setInt(1, currentTripId);
                ResultSet budgetRs = budgetStmt.executeQuery();
                
                if (budgetRs.next()) {
                    totalBudget = budgetRs.getDouble("total_budget");
                    remainingBudget = budgetRs.getDouble("remaining_budget");
                    budgetField.setText(String.format("%.2f", totalBudget));
                    updateBudgetLabels();
                    
                    String expenseSql = "SELECT * FROM expenses WHERE budget_id = ?";
                    PreparedStatement expenseStmt = conn.prepareStatement(expenseSql);
                    expenseStmt.setInt(1, budgetRs.getInt("budget_id"));
                    ResultSet expenseRs = expenseStmt.executeQuery();
                    
                    while (expenseRs.next()) {
                        tableModel.addRow(new Object[]{
                            String.format("₹%.2f", expenseRs.getDouble("amount")),
                            expenseRs.getString("category"),
                            expenseRs.getString("description"),
                            expenseRs.getDate("expense_date"),
                            expenseRs.getInt("expense_id")
                        });
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading budget data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void setBudget() {
        try {
            totalBudget = Double.parseDouble(budgetField.getText());
            remainingBudget = totalBudget;
            updateBudgetLabels();
            
            if (currentTripId == -1) {
                JOptionPane.showMessageDialog(this, "No trip selected. Please create a trip first.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (Connection conn = DatabaseConnection.getConnection()) {
                String checkSql = "SELECT budget_id FROM budgets WHERE trip_id = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setInt(1, currentTripId);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    String updateSql = "UPDATE budgets SET total_budget = ?, remaining_budget = ? WHERE budget_id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                    updateStmt.setDouble(1, totalBudget);
                    updateStmt.setDouble(2, remainingBudget);
                    updateStmt.setInt(3, rs.getInt("budget_id"));
                    updateStmt.executeUpdate();
                } else {
                    String insertSql = "INSERT INTO budgets (trip_id, total_budget, remaining_budget) VALUES (?, ?, ?)";
                    PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                    insertStmt.setInt(1, currentTripId);
                    insertStmt.setDouble(2, totalBudget);
                    insertStmt.setDouble(3, remainingBudget);
                    insertStmt.executeUpdate();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error saving budget: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid budget amount.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addExpense() {
        try {
            if (budgetField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the total budget first!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            totalBudget = Double.parseDouble(budgetField.getText());
            if (totalBudget <= 0) {
                JOptionPane.showMessageDialog(this, "Total budget must be greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (remainingBudget == 0) {
                remainingBudget = totalBudget;
            }

            if (expenseField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an expense amount.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double expenseAmount = Double.parseDouble(expenseField.getText());
            if (expenseAmount <= 0) {
                JOptionPane.showMessageDialog(this, "Expense amount must be greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (expenseAmount > remainingBudget) {
                JOptionPane.showMessageDialog(this, "Not enough budget left! Available: ₹" + String.format("%.2f", remainingBudget), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String category = categoryBox.getSelectedItem().toString();
            String description = descriptionField.getText().isEmpty() ? "No description" : descriptionField.getText();
            Date currentDate = Date.valueOf(LocalDate.now());

            remainingBudget -= expenseAmount;
            int expenseId = -1;

            if (currentTripId != -1) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String budgetSql = "SELECT budget_id FROM budgets WHERE trip_id = ?";
                    PreparedStatement budgetStmt = conn.prepareStatement(budgetSql);
                    budgetStmt.setInt(1, currentTripId);
                    ResultSet rs = budgetStmt.executeQuery();
                    
                    if (rs.next()) {
                        int budgetId = rs.getInt("budget_id");
                        
                        String expenseSql = "INSERT INTO expenses (budget_id, amount, category, description, expense_date) VALUES (?, ?, ?, ?, ?)";
                        PreparedStatement expenseStmt = conn.prepareStatement(expenseSql, Statement.RETURN_GENERATED_KEYS);
                        expenseStmt.setInt(1, budgetId);
                        expenseStmt.setDouble(2, expenseAmount);
                        expenseStmt.setString(3, category);
                        expenseStmt.setString(4, description);
                        expenseStmt.setDate(5, currentDate);
                        int rowsAffected = expenseStmt.executeUpdate();
                        
                        if (rowsAffected == 0) {
                            throw new SQLException("Failed to insert expense.");
                        }
                        
                        ResultSet generatedKeys = expenseStmt.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            expenseId = generatedKeys.getInt(1);
                        }
                        
                        String updateSql = "UPDATE budgets SET remaining_budget = ? WHERE budget_id = ?";
                        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                        updateStmt.setDouble(1, remainingBudget);
                        updateStmt.setInt(2, budgetId);
                        updateStmt.executeUpdate();
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error saving expense: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                    remainingBudget += expenseAmount;
                    updateBudgetLabels();
                    return;
                }
            }

            tableModel.addRow(new Object[]{
                String.format("₹%.2f", expenseAmount), 
                category, 
                description,
                currentDate,
                expenseId
            });
            updateBudgetLabels();

            expenseField.setText("");
            descriptionField.setText("");
            categoryBox.setSelectedIndex(0);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount for expense.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteExpense(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an expense to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this expense?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            double expenseAmount = Double.parseDouble(((String) tableModel.getValueAt(selectedRow, 0)).replace("₹", ""));
            int expenseId = (Integer) tableModel.getValueAt(selectedRow, 4);

            remainingBudget += expenseAmount;
            tableModel.removeRow(selectedRow);
            updateBudgetLabels();

            if (currentTripId != -1) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String budgetSql = "SELECT budget_id FROM budgets WHERE trip_id = ?";
                    PreparedStatement budgetStmt = conn.prepareStatement(budgetSql);
                    budgetStmt.setInt(1, currentTripId);
                    ResultSet rs = budgetStmt.executeQuery();
                    
                    if (rs.next()) {
                        int budgetId = rs.getInt("budget_id");
                        
                        String deleteSql = "DELETE FROM expenses WHERE expense_id = ?";
                        PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                        deleteStmt.setInt(1, expenseId);
                        deleteStmt.executeUpdate();
                        
                        String updateSql = "UPDATE budgets SET remaining_budget = ? WHERE budget_id = ?";
                        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                        updateStmt.setDouble(1, remainingBudget);
                        updateStmt.setInt(2, budgetId);
                        updateStmt.executeUpdate();
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error deleting expense: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                    String category = (String) tableModel.getValueAt(selectedRow, 1);
                    String description = (String) tableModel.getValueAt(selectedRow, 2);
                    Date date = (Date) tableModel.getValueAt(selectedRow, 3);
                    remainingBudget -= expenseAmount;
                    tableModel.insertRow(selectedRow, new Object[]{
                        String.format("₹%.2f", expenseAmount), 
                        category, 
                        description,
                        date,
                        expenseId
                    });
                    updateBudgetLabels();
                    return;
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error parsing expense amount.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBudgetLabels() {
        totalBudgetLabel.setText("Total Budget: ₹" + String.format("%.2f", totalBudget));
        remainingBudgetLabel.setText("Remaining Budget: ₹" + String.format("%.2f", remainingBudget));
    }
}