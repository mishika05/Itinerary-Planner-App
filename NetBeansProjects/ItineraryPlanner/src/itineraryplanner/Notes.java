package itineraryplanner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class Notes extends JPanel {
    private ArrayList<JPanel> notesList;
    private JPanel notesContainer;
    private JTextField titleField;
    private JTextArea noteArea;
    private int currentTripId = -1;
    
    private Color[] stickyColors = {
            new Color(255, 223, 186), // Light Peach
            new Color(255, 255, 153), // Light Yellow
            new Color(204, 255, 204), // Light Green
            new Color(204, 229, 255), // Light Blue
            new Color(255, 204, 229)  // Light Pink
    };
    
    private int colorIndex = 0; // To cycle through different note colors

    public Notes() {
        setLayout(new BorderLayout());
        setBackground(new Color(50, 60, 80)); // Dark background
        setOpaque(true); // Ensure background is applied

        notesList = new ArrayList<>();

        // Header Title
        JLabel titleLabel = new JLabel("Travel Journal", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 42)); // Increased font size
        titleLabel.setForeground(Color.WHITE); 
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(40, 50, 70)); // Subtle background for title
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 90, 110), 1, true),
            BorderFactory.createEmptyBorder(20, 0, 20, 0) // Increased padding
        ));
        add(titleLabel, BorderLayout.NORTH);

        // Notes display box
        JPanel notesDisplayBox = new JPanel(new BorderLayout());
        notesDisplayBox.setBackground(new Color(50, 60, 80));
        notesDisplayBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 90, 110), 2, true), // Thicker, rounded border
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        notesContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20)); // Increased spacing
        notesContainer.setBackground(new Color(50, 60, 80));

        // Scroll pane to handle notes
        JScrollPane scrollPane = new JScrollPane(notesContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBackground(new Color(50, 60, 80));
        notesDisplayBox.add(scrollPane, BorderLayout.CENTER);
        add(notesDisplayBox, BorderLayout.CENTER);

        // Panel for adding new notes
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(50, 60, 80));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Increased padding
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title input field - wider (40 columns)
        JLabel titleInputLabel = new JLabel("Title: ");
        titleInputLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Larger font
        titleInputLabel.setForeground(Color.WHITE);
        titleField = new JTextField(40); // Increased from 20 to 40 columns
        titleField.setFont(new Font("Arial", Font.PLAIN, 20)); // Larger font
        titleField.setBackground(new Color(60, 70, 90));
        titleField.setForeground(Color.WHITE);
        titleField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 90, 110), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Note content input area - wider (40 columns)
        JLabel noteInputLabel = new JLabel("Note: ");
        noteInputLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Larger font
        noteInputLabel.setForeground(Color.WHITE);
        noteArea = new JTextArea(5, 40); // Increased from 20 to 40 columns
        noteArea.setFont(new Font("Arial", Font.PLAIN, 20)); // Larger font
        noteArea.setBackground(new Color(60, 70, 90));
        noteArea.setForeground(Color.WHITE);
        noteArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 90, 110), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Button to add a new note
        JButton addNoteButton = createStyledButton("Add Note");
        addNoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewNote();
            }
        });

        // Arranging components in the input panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(titleInputLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(noteInputLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(new JScrollPane(noteArea), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        inputPanel.add(addNoteButton, gbc);

        add(inputPanel, BorderLayout.SOUTH);

        // Load existing notes
        loadNotes();
    }

    private void loadNotes() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT trip_id FROM trips LIMIT 1";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                currentTripId = rs.getInt("trip_id");
                
                String noteSql = "SELECT * FROM notes WHERE trip_id = ?";
                PreparedStatement noteStmt = conn.prepareStatement(noteSql);
                noteStmt.setInt(1, currentTripId);
                ResultSet noteRs = noteStmt.executeQuery();
                
                while (noteRs.next()) {
                    String title = noteRs.getString("title");
                    String content = noteRs.getString("content");
                    String color = noteRs.getString("color");
                    
                    // Create note panel with saved data
                    createNotePanel(title, content, color);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading notes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addNewNote() {
        String title = titleField.getText().trim();
        String content = noteArea.getText().trim();

        if (title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both title and note!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create a new sticky note panel
        Color noteColor = stickyColors[colorIndex];
        createNotePanel(title, content, colorToString(noteColor));
        
        // Save to database
        if (currentTripId != -1) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "INSERT INTO notes (trip_id, title, content, color) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, currentTripId);
                stmt.setString(2, title);
                stmt.setString(3, content);
                stmt.setString(4, colorToString(noteColor));
                stmt.executeUpdate();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error saving note: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                // Remove the note if saving failed
                notesContainer.remove(notesList.get(notesList.size() - 1));
                notesList.remove(notesList.size() - 1);
                notesContainer.revalidate();
                notesContainer.repaint();
                return;
            }
        }

        // Cycle to next color
        colorIndex = (colorIndex + 1) % stickyColors.length;

        // Clear the input fields after adding a note
        titleField.setText("");
        noteArea.setText("");
    }

    private void createNotePanel(String title, String content, String colorStr) {
        Color color = stringToColor(colorStr);
        
        JPanel notePanel = new JPanel();
        notePanel.setLayout(new BorderLayout());
        notePanel.setPreferredSize(new Dimension(300, 250)); // Larger size
        notePanel.setBackground(color);
        notePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2, true), // Thicker border
            BorderFactory.createEmptyBorder(15, 15, 15, 15) // Increased padding
        ));

        // Title of the note
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Larger font
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Text area for the note content
        JTextArea noteText = new JTextArea(content);
        noteText.setFont(new Font("Arial", Font.PLAIN, 18)); // Larger font
        noteText.setEditable(false);
        noteText.setOpaque(false);
        noteText.setLineWrap(true);
        noteText.setWrapStyleWord(true);
        noteText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Delete button
        JButton deleteButton = new JButton("X");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 14)); // Larger font
        deleteButton.setMargin(new Insets(0, 0, 0, 0));
        deleteButton.setFocusPainted(false);
        deleteButton.setBorderPainted(false);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setForeground(Color.RED);
        deleteButton.addActionListener(e -> deleteNote(notePanel, title));

        // Add button to top-right corner
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(deleteButton, BorderLayout.EAST);
        notePanel.add(topPanel, BorderLayout.NORTH);
        notePanel.add(noteText, BorderLayout.CENTER);

        notesContainer.add(notePanel);
        notesList.add(notePanel);
        notesContainer.revalidate();
        notesContainer.repaint();
    }

    private void deleteNote(JPanel notePanel, String title) {
        notesContainer.remove(notePanel);
        notesList.remove(notePanel);
        notesContainer.revalidate();
        notesContainer.repaint();
        
        // Delete from database
        if (currentTripId != -1) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM notes WHERE trip_id = ? AND title = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, currentTripId);
                stmt.setString(2, title);
                stmt.executeUpdate();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting note: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private String colorToString(Color color) {
        return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
    }

    private Color stringToColor(String colorStr) {
        String[] rgb = colorStr.split(",");
        return new Color(
            Integer.parseInt(rgb[0]),
            Integer.parseInt(rgb[1]),
            Integer.parseInt(rgb[2])
        );
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Tahoma", Font.PLAIN, 20)); // Larger font
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 120, 255)); // Brighter button color
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 90, 110), 1, true),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setOpaque(true);
        return button;
    }
}