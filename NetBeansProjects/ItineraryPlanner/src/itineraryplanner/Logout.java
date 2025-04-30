package itineraryplanner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Logout extends JFrame implements ActionListener {
    private JButton logoutBtn, cancelBtn;
    
    public Logout() {
        setTitle("Logout Confirmation");
        setSize(350, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 73, 94));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel message = new JLabel("Are you sure you want to logout?", SwingConstants.CENTER);
        message.setForeground(Color.WHITE);
        message.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(message, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(52, 73, 94));
        
        logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(231, 76, 60)); // Red color
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(this);
        
        cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(new Color(52, 152, 219)); // Blue color
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.addActionListener(this);
        
        buttonPanel.add(logoutBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(panel);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == logoutBtn) {
            // Close current window and open login
            this.dispose();
            new Login().setVisible(true);
        } else if (e.getSource() == cancelBtn) {
            this.dispose(); // Just close the logout window
        }
    }
}