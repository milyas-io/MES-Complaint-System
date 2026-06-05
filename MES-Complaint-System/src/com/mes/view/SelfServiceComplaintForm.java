package com.mes.view;

import com.mes.dao.ComplaintDAO;
import com.mes.dao.PersonnelDAO;
import com.mes.model.Complaint;
import com.mes.model.Personnel;
import com.mes.model.User;

import javax.swing.*;
import java.awt.*;

/**
 * Form for defense personnel to submit complaints directly (self-service)
 */
public class SelfServiceComplaintForm extends JFrame {
    private User currentUser;
    private final ComplaintDAO complaintDAO = new ComplaintDAO();
    private final PersonnelDAO personnelDAO = new PersonnelDAO();

    private JTextField serviceNumberField;
    private JTextField rankField;
    private JTextField nameField;
    private JTextField unitField;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<Complaint.Priority> priorityCombo;

    public SelfServiceComplaintForm() {
        setTitle("Self-Service Complaint Form");
        setSize(600, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Personnel Information Section
        JPanel personnelPanel = new JPanel(new GridBagLayout());
        personnelPanel.setBorder(BorderFactory.createTitledBorder("Personnel Information"));
        GridBagConstraints pGbc = new GridBagConstraints();
        pGbc.insets = new Insets(5, 5, 5, 5);
        pGbc.fill = GridBagConstraints.HORIZONTAL;

        pGbc.gridx = 0; pGbc.gridy = 0;
        personnelPanel.add(new JLabel("Service Number:"), pGbc);
        pGbc.gridx = 1;
        serviceNumberField = new JTextField(20);
        personnelPanel.add(serviceNumberField, pGbc);

        pGbc.gridx = 0; pGbc.gridy = 1;
        personnelPanel.add(new JLabel("Rank:"), pGbc);
        pGbc.gridx = 1;
        rankField = new JTextField(20);
        personnelPanel.add(rankField, pGbc);

        pGbc.gridx = 0; pGbc.gridy = 2;
        personnelPanel.add(new JLabel("Name:"), pGbc);
        pGbc.gridx = 1;
        nameField = new JTextField(20);
        personnelPanel.add(nameField, pGbc);

        pGbc.gridx = 0; pGbc.gridy = 3;
        personnelPanel.add(new JLabel("Unit:"), pGbc);
        pGbc.gridx = 1;
        unitField = new JTextField(20);
        personnelPanel.add(unitField, pGbc);

        JButton verifyButton = new JButton("Verify Personnel");
        verifyButton.addActionListener(e -> verifyPersonnel());
        pGbc.gridx = 0; pGbc.gridy = 4; pGbc.gridwidth = 2;
        personnelPanel.add(verifyButton, pGbc);

        // Complaint Information Section
        JPanel complaintPanel = new JPanel(new GridBagLayout());
        complaintPanel.setBorder(BorderFactory.createTitledBorder("Complaint Details"));
        GridBagConstraints cGbc = new GridBagConstraints();
        cGbc.insets = new Insets(5, 5, 5, 5);
        cGbc.fill = GridBagConstraints.HORIZONTAL;

        cGbc.gridx = 0; cGbc.gridy = 0;
        complaintPanel.add(new JLabel("Title:"), cGbc);
        cGbc.gridx = 1;
        titleField = new JTextField(30);
        complaintPanel.add(titleField, cGbc);

        cGbc.gridx = 0; cGbc.gridy = 1;
        complaintPanel.add(new JLabel("Description:"), cGbc);
        cGbc.gridx = 1; cGbc.fill = GridBagConstraints.BOTH;
        descriptionArea = new JTextArea(8, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        complaintPanel.add(new JScrollPane(descriptionArea), cGbc);

        cGbc.gridx = 0; cGbc.gridy = 2; cGbc.fill = GridBagConstraints.HORIZONTAL;
        complaintPanel.add(new JLabel("Priority:"), cGbc);
        cGbc.gridx = 1;
        priorityCombo = new JComboBox<>(Complaint.Priority.values());
        priorityCombo.setSelectedItem(Complaint.Priority.MEDIUM);
        complaintPanel.add(priorityCombo, cGbc);

        // Submit button
        JButton submitButton = new JButton("Submit Complaint");
        submitButton.setBackground(new Color(50, 80, 50));
        submitButton.setForeground(Color.WHITE);
        submitButton.addActionListener(e -> submitComplaint());
        cGbc.gridx = 0; cGbc.gridy = 3; cGbc.gridwidth = 2; cGbc.anchor = GridBagConstraints.CENTER;
        complaintPanel.add(submitButton, cGbc);

        // Add panels to main panel
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(personnelPanel, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(complaintPanel, gbc);

        add(mainPanel);
    }

    private void verifyPersonnel() {
        String serviceNumber = serviceNumberField.getText().trim();
        if (serviceNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a service number", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            var personnelOpt = personnelDAO.findByServiceNumber(serviceNumber);
            if (personnelOpt.isPresent()) {
                Personnel personnel = personnelOpt.get();
                rankField.setText(personnel.getRank());
                nameField.setText(personnel.getName());
                unitField.setText(personnel.getUnit());
                JOptionPane.showMessageDialog(this, "Personnel verified successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Personnel not found. Please check the service number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error verifying personnel: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void submitComplaint() {
        String serviceNumber = serviceNumberField.getText().trim();
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        Complaint.Priority priority = (Complaint.Priority) priorityCombo.getSelectedItem();

        if (serviceNumber.isEmpty() || title.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Verify personnel again before submission
            var personnelOpt = personnelDAO.findByServiceNumber(serviceNumber);
            if (personnelOpt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Personnel verification failed. Please check the service number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create complaint with operator ID set to 0 (self-service)
            Complaint newComplaint = new Complaint(title, description, priority, 0);
            int generatedId = complaintDAO.logNewComplaint(newComplaint);

            if (generatedId > 0) {
                String confirmationMessage = String.format(
                        "Complaint submitted successfully!\nReference ID: SR-%05d\nWe'll notify you about the progress.",
                        generatedId);
                JOptionPane.showMessageDialog(this, confirmationMessage, "Success", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to submit complaint. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}