package com.mes.view;

import com.mes.dao.ComplaintDAO;
import com.mes.dao.PersonnelDAO;
import com.mes.model.Complaint;
import com.mes.model.Operator;
import com.mes.model.User;

import javax.swing.*;
import java.awt.*;

/**
 * Traceability:
 * - FR-2 / UC-2: Operator-assisted complaint logging
 * - UC-4: Verify Personnel (service number check)
 * - UC-8: Track Complaint Status (reference ID displayed after logging)
 */
public class LogComplaintForm extends JFrame {
    private OperatorDashboard parentDashboard; // Reference to the parent dashboard
    private Operator currentUser;
    private final ComplaintDAO complaintDAO = new ComplaintDAO();
    private final PersonnelDAO personnelDAO = new PersonnelDAO();

    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<Complaint.Priority> priorityCombo;
    private JTextField serviceNumberField;

    /**
     * Constructor now accepts the parent dashboard to enable refreshing the table.
     */
    public LogComplaintForm(OperatorDashboard parentDashboard, User user) {
        if (!"Operator".equalsIgnoreCase(user.getRole())) {
            throw new IllegalArgumentException("Permission Required: Operator only.");
        }
        this.parentDashboard = parentDashboard; // Save the reference
        this.currentUser = new Operator(user.getUserId(), user.getUsername(), "", user.getName(), user.getEmail());

        setTitle("New Help Request - Logged by: " + currentUser.getName());
        setSize(550, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setupUI();
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Personnel Service Number
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Personnel Service Number :"), gbc);
        gbc.gridx = 1;
        serviceNumberField = new JTextField(20);
        serviceNumberField.setToolTipText("Enter valid service number for verification.");
        mainPanel.add(serviceNumberField, gbc);

        // Complaint summary
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Short Summary : "), gbc);
        gbc.gridx = 1;
        titleField = new JTextField(30);
        titleField.setToolTipText("Summarize the issue in a few words.");
        mainPanel.add(titleField, gbc);

        // Complaint description
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Detailed Description :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        descriptionArea = new JTextArea(8, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        mainPanel.add(new JScrollPane(descriptionArea), gbc);

        // Priority
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(new JLabel("Urgency Level : "), gbc);
        gbc.gridx = 1;
        priorityCombo = new JComboBox<>(Complaint.Priority.values());
        priorityCombo.setSelectedItem(Complaint.Priority.MEDIUM);
        mainPanel.add(priorityCombo, gbc);

        // Submit button
        JButton submitButton = new JButton("FINISH & SEND REQUEST");
        submitButton.setBackground(new Color(50, 80, 50));
        submitButton.setForeground(Color.WHITE);
        submitButton.addActionListener(e -> submitComplaint());
        // BUG FIX: Changed gbc.gridwidth to gbc.gridwidth
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(submitButton, gbc);

        add(mainPanel);
    }

    private void submitComplaint() {
        String sn = serviceNumberField.getText().trim();
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        Complaint.Priority priority = (Complaint.Priority) priorityCombo.getSelectedItem();

        if (sn.isEmpty() || title.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Service number, summary, and description are required.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            var pOpt = personnelDAO.findByServiceNumber(sn);
            if (pOpt.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Personnel not found. Verify service number.",
                        "Verification", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Complaint newComplaint = new Complaint(title, description, priority, currentUser.getUserId());
            int generatedId = complaintDAO.logNewComplaint(newComplaint);

            if (generatedId > 0) {
                String confirmationMessage = String.format(
                        "Success! The request has been saved.\nReference ID: SR-%05d\nWe've notified the team.",
                        generatedId);
                JOptionPane.showMessageDialog(this, confirmationMessage,
                        "Request Sent", JOptionPane.INFORMATION_MESSAGE);
                
                // KEY FIX: Tell the parent dashboard to refresh its table
                if (parentDashboard != null) {
                    parentDashboard.refreshHistoryTable();
                }
                
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "We couldn't save that request right now. Please try again.",
                        "Connection Issue", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "An unexpected error occurred. Please contact system support.\n" + ex.getMessage(),
                    "System Note", JOptionPane.ERROR_MESSAGE);
        }
    }
}