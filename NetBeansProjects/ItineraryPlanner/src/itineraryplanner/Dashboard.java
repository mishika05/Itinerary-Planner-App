package itineraryplanner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Dashboard extends JFrame {
    private JPanel mainPanel, sideMenuPanel, contentPanel;
    private JButton profileButton, planButton, budgetButton, packButton, notesButton, reminderButton, logoutButton;
    private String currentUsername;
    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = new ImageIcon(imagePath).getImage();
            } catch (Exception e) {
                e.printStackTrace();
                backgroundImage = null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();

            if (backgroundImage != null) {
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g2d.setColor(new Color(40, 40, 40));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }

            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(0, 0, 0, 180),
                0, 100, new Color(0, 0, 0, 0)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), 100);

            g2d.dispose();
        }
    }

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

    public Dashboard(String username) {
        this.currentUsername = username;
        setTitle("Itinerary Planner - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));

        // SIDE MENU PANEL
        sideMenuPanel = new JPanel(new GridLayout(7, 1, 5, 5));
        sideMenuPanel.setBackground(new Color(50, 50, 50));
        sideMenuPanel.setPreferredSize(new Dimension(300, getHeight()));
        sideMenuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Added padding

        profileButton = createMenuButton("Profile");
        planButton = createMenuButton("Plan");
        budgetButton = createMenuButton("Budget");
        packButton = createMenuButton("Pack");
        notesButton = createMenuButton("Notes");
        reminderButton = createMenuButton("Reminder");
        
        logoutButton = createMenuButton("Logout");

        sideMenuPanel.add(profileButton);
        sideMenuPanel.add(planButton);
        sideMenuPanel.add(budgetButton);
        sideMenuPanel.add(packButton);
        sideMenuPanel.add(notesButton);
        sideMenuPanel.add(reminderButton);
        sideMenuPanel.add(logoutButton);

        // CONTENT PANEL with background image
        contentPanel = new BackgroundPanel("src/image/travel.png");
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(new Color(40, 40, 40));

        // WELCOME LABEL with shadow
        ShadowLabel welcomeLabel = new ShadowLabel("Welcome, " + username + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        contentPanel.add(welcomeLabel, BorderLayout.NORTH);

        mainPanel.add(sideMenuPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Navigation Button Actions
        profileButton.addActionListener(e -> switchPanel(new Profile(username)));
        planButton.addActionListener(e -> switchPanel(new Plan()));
        budgetButton.addActionListener(e -> switchPanel(new Budget()));
        packButton.addActionListener(e -> switchPanel(new Pack()));
        notesButton.addActionListener(e -> switchPanel(new Notes()));
        reminderButton.addActionListener(e -> switchPanel(new Reminder()));

        // Logout Button Action
        logoutButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (response == JOptionPane.YES_OPTION) {
                dispose();
                new Login().setVisible(true);
            }
        });

        setVisible(true);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Times New Roman", Font.PLAIN, 20)); // Modern font
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(60, 60, 60)); // Darker gray
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1, true)); // Rounded border
        button.setPreferredSize(new Dimension(280, 50));
        button.setOpaque(true);
        
        // Hover effect for all buttons
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(80, 80, 80)); // Lighter gray on hover
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(60, 60, 60)); // Revert to default
                button.setCursor(Cursor.getDefaultCursor());
            }
        });
        
        return button;
    }

    private void switchPanel(JPanel newPanel) {
        contentPanel.removeAll();
        contentPanel.add(newPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Dashboard("User").setVisible(true);
        });
    }
}