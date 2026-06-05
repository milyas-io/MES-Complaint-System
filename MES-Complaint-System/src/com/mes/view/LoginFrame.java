package com.mes.view;

import com.mes.dao.UserDAO;
import com.mes.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Traceability:
 * - FR-1 / UC-1: User Authentication (login with credentials, role-based dashboard access)
 * - UC-1 AF1.1: Invalid credentials → error message
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private final UserDAO userDAO = new UserDAO();

    private static final Color MES_GREEN = new Color(25,45,25);
    private static final Color MES_GOLD = new Color(210,170,50);
    private static final Color HOVER_GREEN = new Color(45,85,45);
    private static final Color SCREEN_BG = new Color(240,242,240);

    public LoginFrame() {
        setTitle("MES Central Command | Personnel Authentication");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(900, 700));
        setLocationRelativeTo(null);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(SCREEN_BG);
        setContentPane(mainContainer);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(MES_GREEN);
        header.setPreferredSize(new Dimension(getWidth(), 140));
        header.setBorder(new MatteBorder(0,0,6,0,MES_GOLD));

        JLabel titleLabel = new JLabel("MILITARY ENGINEERING SERVICES (MES)");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 35));
        titleLabel.setForeground(Color.WHITE);

        JLabel securityLabel = new JLabel("SECURE SYSTEM ACCESS. AUTHORIZED PERSONNEL ONLY");
        securityLabel.setFont(new Font("Monospaced", Font.BOLD, 15));
        securityLabel.setForeground(MES_GOLD);

        JPanel titleGroup = new JPanel(new GridLayout(2, 1));
        titleGroup.setOpaque(false);
        titleGroup.add(titleLabel);
        titleGroup.add(securityLabel);
        titleGroup.setBorder(new EmptyBorder(25,60,20,60));

        header.add(titleGroup, BorderLayout.WEST);
        mainContainer.add(header, BorderLayout.NORTH);

        // Center login card
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel loginCard = new JPanel(new GridBagLayout());
        loginCard.setBackground(Color.WHITE);
        loginCard.setPreferredSize(new Dimension(500, 650));
        loginCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200), 1),
                new EmptyBorder(40, 60, 40, 60)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Pakistan Army Logo
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        ImageIcon armyLogo = new ImageIcon(
                "D:\\Projects\\MES-Complaint-System\\src\\resources\\pak-army-logo.png"
        );
        Image scaledLogo = armyLogo.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        loginCard.add(logoLabel, gbc);

        // System Title
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 30, 0);
        JLabel systemLabel = new JLabel("MES COMMAND", SwingConstants.CENTER);
        systemLabel.setFont(new Font("Serif", Font.BOLD, 26));
        systemLabel.setForeground(MES_GREEN);
        loginCard.add(systemLabel, gbc);

        // Username
        gbc.gridy = 2;
        gbc.insets = new Insets(5,0,5,0);
        JLabel uLabel = new JLabel("PERSONNEL ID");
        uLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        uLabel.setForeground(new Color(80,80,80));
        loginCard.add(uLabel, gbc);

        gbc.gridy = 3;
        usernameField = new JTextField();
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        usernameField.setPreferredSize(new Dimension(320, 45));
        usernameField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        loginCard.add(usernameField, gbc);

        // Password
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 0, 5, 0);
        JLabel pLabel = new JLabel("SECURITY ACCESS KEY");
        pLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        pLabel.setForeground(new Color(80,80,80));
        loginCard.add(pLabel, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(5,0,5,0);
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        passwordField.setPreferredSize(new Dimension(320, 45));
        passwordField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        loginCard.add(passwordField, gbc);

        // Login options panel
        gbc.gridy = 6;
        gbc.insets = new Insets(20, 0, 20, 0);
        loginCard.add(createLoginOptionsPanel(), gbc);

        // Login button
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 10, 0);
        loginButton = new JButton("AUTHENTICATE IDENTITY");
        styleLoginButton(loginButton);
        loginCard.add(loginButton, gbc);

        centerWrapper.add(loginCard);
        mainContainer.add(centerWrapper, BorderLayout.CENTER);

        // Footer warning
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(new Color(215,220,215));
        footer.setBorder(new MatteBorder(1,0,0,0, Color.GRAY));
        JLabel warningLabel = new JLabel(
                "A CLASSIFIED SYSTEM: Unauthorized access attempts are subject to military investigation."
        );
        warningLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        footer.add(warningLabel);
        mainContainer.add(footer, BorderLayout.SOUTH);
    }

    private JPanel createLoginOptionsPanel() {
        JPanel optionsPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        optionsPanel.setOpaque(false);

        JButton personnelLoginButton = new JButton("DEFENSE PERSONNEL LOGIN");
        styleLoginButton(personnelLoginButton);
        personnelLoginButton.addActionListener(e -> openPersonnelLogin());

        JButton selfServiceButton = new JButton("SELF-SERVICE COMPLAINT");
        styleLoginButton(selfServiceButton);
        selfServiceButton.addActionListener(e -> openSelfService());

        optionsPanel.add(personnelLoginButton);
        optionsPanel.add(selfServiceButton);

        return optionsPanel;
    }

    private void openPersonnelLogin() {
        usernameField.setEnabled(true);
        passwordField.setEnabled(true);
        loginButton.setEnabled(true);
    }

    private void openSelfService() {
        new SelfServiceComplaintForm().setVisible(true);
    }

    private void styleLoginButton(JButton btn) {
        btn.setBackground(MES_GREEN);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setPreferredSize(new Dimension(320, 60));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(HOVER_GREEN);
                btn.setBorder(BorderFactory.createLineBorder(MES_GOLD, 2));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(MES_GREEN);
                btn.setBorder(BorderFactory.createLineBorder(MES_GREEN.darker(), 1));
            }
        });
        btn.addActionListener(this::handleLogin);
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Identity fields cannot be empty.",
                    "Entry Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            Optional<User> auth = userDAO.authenticateUser(username, password);
            if (auth.isPresent()) {
                User u = auth.get();
                JOptionPane.showMessageDialog(this,
                        "Welcome, " + u.getName() + ". System ready.");
                this.dispose();
                openDashboard(u);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Access Denied: Credentials not recognized.",
                        "Security Alert", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "System Connection Failure: " + ex.getMessage());
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void openDashboard(User user) {
        switch (user.getRole()) {
            case "Operator":
                new OperatorDashboard(user).setVisible(true);
                break;
            case "Engineer":
                new TechnicianDashboard(user).setVisible(true);
                break;
            case "Admin":
                new AdminDashboard(user).setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(this,
                        "Unknown role: " + user.getRole());
        }
    }
}
