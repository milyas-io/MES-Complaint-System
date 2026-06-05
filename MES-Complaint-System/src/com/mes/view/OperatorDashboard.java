package com.mes.view;

import com.mes.dao.ComplaintDAO;
import com.mes.model.Complaint;
import com.mes.model.Engineer;
import com.mes.model.Feedback;
import com.mes.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class OperatorDashboard extends JFrame {
    private User currentUser;
    private ComplaintDAO dao = new ComplaintDAO();

    private JTable historyTable;
    private DefaultTableModel tableModel;
    private DefaultListModel<String> techListModel;

    private final Color MES_GREEN = new Color(25,45,25);
    private final Color ACCENT_GOLD = new Color(210,170,50);
    private final Color SCREEN_BG = new Color(240,242,240);

    public OperatorDashboard(User user) {
        this.currentUser = user;
        setupUI();
        refreshHistoryTable();
    }

    private void setupUI() {
        setTitle("MES Service Hub | Authentication: " + currentUser.getName());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(SCREEN_BG);
        setContentPane(mainContainer);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(MES_GREEN);
        topBar.setPreferredSize(new Dimension(0, 90));
        topBar.setBorder(new MatteBorder(0, 0, 4, 0, ACCENT_GOLD));

        JLabel title = new JLabel(" MILITARY ENGINEERING SERVICES | COORDINATION COMMAND");
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

        JPanel workspace = new JPanel(new BorderLayout(25, 25));
        workspace.setOpaque(false);
        workspace.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel actionGrid = new JPanel(new GridLayout(1, 3, 30, 0));
        actionGrid.setOpaque(false);
        actionGrid.add(createActionCard("Log Service Request", "Initiate a new maintenance task", e -> openLogForm()));
        actionGrid.add(createActionCard("Dispatch Specialist", "Assign an engineer to an active task", e -> openAssignForm()));
        actionGrid.add(createActionCard("Resolution Review", "Finalize task and record feedback", e -> openFeedbackForm()));
        workspace.add(actionGrid, BorderLayout.NORTH);

        String[] cols = {"REFERENCE NUMBER", "SERVICE DESCRIPTION", "TASK PROGRESS"};
        tableModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        historyTable = new JTable(tableModel);
        historyTable.setRowHeight(45);
        historyTable.setFont(new Font("SansSerif", Font.PLAIN, 15));

        JTableHeader header = historyTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 50));
        header.setBackground(MES_GREEN);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 14));

        JScrollPane tableScroll = new JScrollPane(historyTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        workspace.add(tableScroll, BorderLayout.CENTER);

        mainContainer.add(workspace, BorderLayout.CENTER);
        mainContainer.add(createSidebar(), BorderLayout.EAST);
    }

    private JPanel createSidebar() {
        JPanel side = new JPanel(new BorderLayout());
        side.setPreferredSize(new Dimension(280, 0));
        side.setBackground(Color.WHITE);
        side.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY),
                new EmptyBorder(20, 15, 20, 15)
        ));

        JLabel sideTitle = new JLabel("AVAILABLE TECHNICIANS");
        sideTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        sideTitle.setForeground(MES_GREEN);
        side.add(sideTitle, BorderLayout.NORTH);

        techListModel = new DefaultListModel<>();
        JList<String> techList = new JList<>(techListModel);
        techList.setFixedCellHeight(40);
        techList.setFont(new Font("Monospaced", Font.BOLD, 13));
        side.add(new JScrollPane(techList), BorderLayout.CENTER);
        return side;
    }

    public void refreshHistoryTable() {
        try {
            tableModel.setRowCount(0);
            List<Complaint> history = dao.getAllComplaints();
            for (Complaint c : history) {
                tableModel.addRow(new Object[] {
                        "SR-" + String.format("%04d", c.getComplaintId()),
                        c.getTitle().toUpperCase(),
                        c.getStatusString().replace("_", " ")
                });
            }

            techListModel.clear();
            List<Engineer> engineers = dao.getAvailableEngineers();
            for (Engineer en : engineers) {
                techListModel.addElement("ID: " + en.getUserId() + " | " + en.getName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Data Sync Error: " + e.getMessage());
        }
    }

    /**
     * Opens the LogComplaintForm and passes a reference to this dashboard instance.
     * This allows the form to call refreshHistoryTable() on successful submission.
     */
    private void openLogForm() {
        new LogComplaintForm(this, currentUser).setVisible(true);
    }

    private void openAssignForm() {
        int row = historyTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select an unassigned task first."); return; }

        String refId = (String) tableModel.getValueAt(row, 0);
        int cid = Integer.parseInt(refId.replace("SR-", ""));

        JDialog dlg = new JDialog(this, "Dispatch Resource for " + refId, true);
        dlg.setSize(400, 450);
        dlg.setLayout(new BorderLayout(15, 15));
        dlg.setLocationRelativeTo(this);

        DefaultListModel<String> m = new DefaultListModel<>();
        try {
            List<Engineer> available = dao.getAvailableEngineers();
            for (Engineer en : available) { m.addElement(en.getUserId() + " - " + en.getName()); }
        } catch (Exception e) { e.printStackTrace(); }

        JList<String> list = new JList<>(m);
        list.setFixedCellHeight(35);
        dlg.add(new JLabel(" Select Specialist for Dispatch :", SwingConstants.LEFT), BorderLayout.NORTH);
        dlg.add(new JScrollPane(list), BorderLayout.CENTER);

        JButton btnAssign = new JButton("CONFIRM DISPATCH");
        btnAssign.setBackground(MES_GREEN);
        btnAssign.setForeground(Color.WHITE);
        btnAssign.setPreferredSize(new Dimension(0, 55));
        btnAssign.addActionListener(e -> {
            try {
                String sel = list.getSelectedValue();
                if (sel == null) return;
                int eid = Integer.parseInt(sel.split(" - ")[0]);
                if (dao.assignEngineer(cid, eid)) {
                    JOptionPane.showMessageDialog(dlg, "Unit Dispatched Successfully.");
                    refreshHistoryTable(); // Refresh table after assignment
                    dlg.dispose();
                } else {
                    JOptionPane.showMessageDialog(dlg, "Assignment error or already assigned.");
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, "Assignment error: " + ex.getMessage()); }
        });
        dlg.add(btnAssign, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void openFeedbackForm() {
        int row = historyTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a completed task to review."); return; }

        String refId = (String) tableModel.getValueAt(row, 0);
        int cid = Integer.parseInt(refId.replace("SR-", ""));

        JDialog dlg = new JDialog(this, "Operational Review: " + refId, true);
        dlg.setSize(450, 400);
        dlg.setLayout(new GridBagLayout());
        dlg.setLocationRelativeTo(this);

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(10,20,10,20);

        JComboBox<Integer> cmbRating = new JComboBox<>(new Integer[]{1,2,3,4,5});
        JTextArea txtComments = new JTextArea(4, 20);

        g.gridy = 0; dlg.add(new JLabel("Personnel Satisfaction Rating (1-5) :"), g);
        g.gridy = 1; dlg.add(cmbRating, g);
        g.gridy = 2; dlg.add(new JLabel("Post-Resolution Remarks : "), g);
        g.gridy = 3; dlg.add(new JScrollPane(txtComments), g);

        JButton btnSubmit = new JButton("FINALIZE & CLOSE TASK");
        btnSubmit.setBackground(MES_GREEN); btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setPreferredSize(new Dimension(0, 50));
        btnSubmit.addActionListener(e -> {
            try {
                Feedback fb = new Feedback(cid, (int) cmbRating.getSelectedItem(), txtComments.getText());
                if (dao.submitFeedback(fb)) {
                    JOptionPane.showMessageDialog(dlg, "Task Archive Finalized.");
                    refreshHistoryTable(); // Refresh table after feedback
                    dlg.dispose();
                } else {
                    JOptionPane.showMessageDialog(dlg, "Feedback submission failed.");
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, "Invalid Input: " + ex.getMessage()); }
        });
        g.gridy = 4; g.insets = new Insets(30, 20, 10, 20);
        dlg.add(btnSubmit, g);
        dlg.setVisible(true);
    }

    private JPanel createActionCard(String title, String desc, java.awt.event.ActionListener a) {
        JPanel card = new JPanel(new BorderLayout(0, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitle.setForeground(MES_GREEN);

        JLabel lblDesc = new JLabel("<html>" + desc + "</html>");
        lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblDesc.setForeground(Color.GRAY);

        JButton btn = new JButton("OPEN TOOL");
        btn.setBackground(MES_GREEN); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.addActionListener(a);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblDesc, BorderLayout.CENTER);
        card.add(btn, BorderLayout.SOUTH);
        return card;
    }
}