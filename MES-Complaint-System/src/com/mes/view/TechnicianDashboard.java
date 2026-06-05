package com.mes.view;

import com.mes.dao.ComplaintDAO;
import com.mes.model.Complaint;
import com.mes.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

/**
 * - FR-6 / UC-6: Technician views assigned complaints
 * - FR-7 / UC-6, UC-7: Technician updates complaint status
 * - FR-4 / UC-8: Track Complaint Status
 */
public class TechnicianDashboard extends JFrame {
    private User currentUser;
    private ComplaintDAO complaintDAO = new ComplaintDAO();
    private JTable taskTable;
    private DefaultTableModel tableModel;

    private final Color MES_GREEN = new Color(25,45,25);
    private final Color ACCENT_GOLD = new Color(210,170,50);
    private final Color SCREEN_BG = new Color(240,242,240);
    private final Color HOVER_GREEN = new Color(45,85,45);

    public TechnicianDashboard(User user) {
        this.currentUser = user;
        setupUI();
        refreshTaskList();
    }

    private void setupUI() {
        setTitle("MES Technical Workstation | Personnel: " + currentUser.getName());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(SCREEN_BG);
        setContentPane(mainContainer);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(MES_GREEN);
        topBar.setPreferredSize(new Dimension(0, 90));
        topBar.setBorder(new MatteBorder(0, 0, 4, 0, ACCENT_GOLD));

        JLabel title = new JLabel(" MILITARY ENGINEERING SERVICES | FIELD OPERATIONS");
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("SECURE LOGOUT");
        btnLogout.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnLogout.setBackground(new Color(100, 30, 30));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });

        JPanel logoutWrapper = new JPanel(new GridBagLayout());
        logoutWrapper.setOpaque(false);
        logoutWrapper.setBorder(new EmptyBorder(0, 0, 0, 30));
        logoutWrapper.add(btnLogout);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(logoutWrapper, BorderLayout.EAST);
        mainContainer.add(topBar, BorderLayout.NORTH);

        // Workspace
        JPanel workspace = new JPanel(new BorderLayout());
        workspace.setOpaque(false);
        workspace.setBorder(new EmptyBorder(30, 50, 30, 50));

        String[] cols = {"ID", "REFERENCE ID", "URGENCY", "STATUS", "SERVICE TITLE"};
        tableModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        taskTable = new JTable(tableModel);
        taskTable.setRowHeight(50);
        taskTable.setFont(new Font("SansSerif", Font.PLAIN, 15));
        taskTable.getColumnModel().getColumn(0).setMinWidth(0);
        taskTable.getColumnModel().getColumn(0).setMaxWidth(0);
        taskTable.getColumnModel().getColumn(0).setWidth(0);

        JTableHeader tableHeader = taskTable.getTableHeader();
        tableHeader.setBackground(MES_GREEN);
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setPreferredSize(new Dimension(0, 50));

        workspace.add(new JScrollPane(taskTable), BorderLayout.CENTER);
        mainContainer.add(workspace, BorderLayout.CENTER);

        // Footer with actions
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 30));
        footer.setOpaque(false);

        JButton btnUpdate = createStyledButton("UPDATE TASK PROGRESS");
        btnUpdate.addActionListener(e -> openUpdateStatusForm());

        JButton btnReload = createStyledButton("RELOAD SYSTEM DATA");
        btnReload.addActionListener(e -> refreshTaskList());

        footer.add(btnUpdate);
        footer.add(btnReload);
        mainContainer.add(footer, BorderLayout.SOUTH);
    }

    private void openUpdateStatusForm() {
        int row = taskTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a mission record from the ledger.");
            return;
        }
        final int complaintId = (Integer) taskTable.getModel().getValueAt(row, 0);
        final int engineerId = currentUser.getUserId();

        JDialog dlg = new JDialog(this, "Mission Update: " + taskTable.getValueAt(row, 1), true);
        dlg.setSize(400, 300);
        dlg.setLayout(new GridBagLayout());
        dlg.getContentPane().setBackground(SCREEN_BG);
        dlg.setLocationRelativeTo(this);

        JComboBox<Complaint.Status> statusBox = new JComboBox<>(new Complaint.Status[] {
                Complaint.Status.IN_PROGRESS, Complaint.Status.RESOLVED, Complaint.Status.CLOSED
        });

        JTextField remarksField = new JTextField(20);
        JButton btnSubmit = new JButton("COMMIT TO HQ");
        btnSubmit.setBackground(MES_GREEN);
        btnSubmit.setForeground(Color.WHITE);

        btnSubmit.addActionListener(e -> {
            try {
                Complaint.Status sel = (Complaint.Status) statusBox.getSelectedItem();
                if ((sel == Complaint.Status.RESOLVED || sel == Complaint.Status.CLOSED) && remarksField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dlg, "Resolution notes are required.");
                    return;
                }
                if (complaintDAO.updateComplaintStatus(complaintId, engineerId, sel, remarksField.getText())) {
                    JOptionPane.showMessageDialog(dlg, "Field Record Updated.");
                    refreshTaskList();
                    dlg.dispose();
                } else {
                    JOptionPane.showMessageDialog(dlg, "Update failed. Check assignment and status.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Sync Error: " + ex.getMessage());
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.gridy = 0; dlg.add(new JLabel("Mission Status : "), gbc);
        gbc.gridy = 1; dlg.add(statusBox, gbc);
        gbc.gridy = 2; dlg.add(new JLabel("Field Remarks : "), gbc);
        gbc.gridy = 3; dlg.add(remarksField, gbc);
        gbc.gridy = 4; dlg.add(btnSubmit, gbc);

        dlg.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(240, 55));
        btn.setBackground(MES_GREEN);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(HOVER_GREEN);
                btn.setBorder(BorderFactory.createLineBorder(ACCENT_GOLD, 1));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(MES_GREEN);
                btn.setBorder(null);
            }
        });
        return btn;
    }

    public void refreshTaskList() {
        try {
            tableModel.setRowCount(0);
            List<Complaint> list = complaintDAO.getComplaintsByEngineerId(currentUser.getUserId());
            if (list != null) {
                for (Complaint c : list) {
                    tableModel.addRow(new Object[] {
                            c.getComplaintId(),
                            "SR-" + String.format("%04d", c.getComplaintId()),
                            c.getPriorityString(),
                            c.getStatusString(),
                            c.getTitle().toUpperCase()
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "HQ Database Link Failure.");
        }
    }
}
