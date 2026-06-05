package com.mes.view;

import com.mes.dao.ComplaintDAO;
import com.mes.dao.ReportDAO;
import com.mes.dao.UserDAO;
import com.mes.model.Complaint;
import com.mes.model.Report;
import com.mes.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class AdminDashboard extends JFrame {
    private User currentUser;
    private ComplaintDAO complaintDAO = new ComplaintDAO();
    private UserDAO userDAO = new UserDAO();

    private JTable masterTable;
    private DefaultTableModel masterModel;

    private final Color MES_GREEN = new Color(25,45,25);
    private final Color ACCENT_GOLD = new Color(210,170,50);
    private final Color SCREEN_BG = new Color(240,242,240);
    private final Color HOVER_GREEN = new Color(45,85,45);

    public AdminDashboard(User user) {
        if (user == null || !"Admin".equalsIgnoreCase(user.getRole())) {
            throw new SecurityException("UNAUTHORIZED ACCESS DETECTED");
        }
        this.currentUser = user;
        setupUI();
        refreshMasterTable();
    }

    private void generateStrategicReport() {
        ReportDAO rdao = new ReportDAO();
        LocalDate start = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now();
        try {
            int total = rdao.countTotal(start, end);
            int resolved = rdao.countResolved(start, end);
            int closed = rdao.countClosed(start, end);
            double perfRate = rdao.performanceRate(start, end);

            Report rpt = new Report("Monthly Summary", java.sql.Date.valueOf(start), java.sql.Date.valueOf(end));
            String txt = rpt.generate(total, resolved)
                    + "\nClosed: " + closed
                    + "\nPerformance Rate: " + String.format("%.2f%%", perfRate);

            JTextArea area = new JTextArea(txt);
            area.setFont(new Font("Monospaced", Font.PLAIN, 13));
            area.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(area),
                    "STRATEGIC READINESS REPORT", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Report generation error: " + e.getMessage());
        }
    }

    private void setupUI() {
        setTitle("MES Global Administration | Officer: " + currentUser.getName());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1100, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(SCREEN_BG);
        setContentPane(mainContainer);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(MES_GREEN);
        topBar.setPreferredSize(new Dimension(0, 90));
        topBar.setBorder(new MatteBorder(0, 0, 4, 0, ACCENT_GOLD));

        JLabel title = new JLabel(" MILITARY ENGINEERING SERVICES | CENTRAL CONTROL COMMAND");
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("TERMINATE SESSION");
        btnLogout.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnLogout.setBackground(new Color(120, 30, 30));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });

        JPanel logWrapper = new JPanel(new GridBagLayout());
        logWrapper.setOpaque(false);
        logWrapper.setBorder(new EmptyBorder(0, 0, 0, 30));
        logWrapper.add(btnLogout);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(logWrapper, BorderLayout.EAST);
        mainContainer.add(topBar, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));

        tabs.addTab(" PERSONNEL & ACCESS ", createUserMgmtPanel());
        tabs.addTab(" GLOBAL COMPLAINT LEDGER ", createLedgerPanel());
        tabs.addTab(" PERFORMANCE ANALYTICS ", createReportingPanel());

        mainContainer.add(tabs, BorderLayout.CENTER);
    }

    private JPanel createUserMgmtPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(SCREEN_BG);
        JPanel cardGrid = new JPanel(new GridLayout(1, 3, 40, 0));
        cardGrid.setOpaque(false);

        cardGrid.add(createActionCard("ENROLL NEW USER", "Create authorized accounts for Engineers or Operators.", e -> openAddUserForm()));
        cardGrid.add(createActionCard("MANUAL DISPATCH", "Assign specific field engineers to pending service requests.", e -> handleManualAssign()));
        cardGrid.add(createActionCard("VIEW ACCESS LOGS", "Monitor user login attempts and security events.", e -> showSystemLogs()));

        p.add(cardGrid);
        return p;
    }

    private void openAddUserForm() {
        JTextField nameField = new JTextField();
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JTextField emailField = new JTextField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Engineer", "Operator", "Admin"});

        Object[] message = {
            "Full Legal Name *", nameField,
            "System Username *", userField,
            "Secure Password *", passField,
            "Official Military Email *", emailField,
            "Assigned Role *", roleBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "USER ACCOUNT CREATION", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword()).trim();
            String email = emailField.getText().trim();
            String role = (String) roleBox.getSelectedItem();

            if (name.isEmpty() || user.isEmpty() || pass.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ERROR: All fields marked with (*) are mandatory.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                JOptionPane.showMessageDialog(this, "ERROR: The email format is invalid.", "Security Alert", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                if (userDAO.usernameExists(user)) {
                    JOptionPane.showMessageDialog(this, "User ID already exists");
                    return;
                }
                boolean success = userDAO.addUser(new User(0, user, "", name, email, role), pass);
                JOptionPane.showMessageDialog(this, success ? "New " + role + " successfully enrolled in system." : "Failed to enroll user.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "SQL SYNC FAILURE: " + ex.getMessage());
            }
        }
    }

    private void showSystemLogs() {
        // Since account locking and logging system has been removed, this feature is disabled.
        JOptionPane.showMessageDialog(this,
                "Security logging feature has been disabled.",
                "System Information",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createLedgerPanel() {
        JPanel p = new JPanel(new BorderLayout(25, 25));
        p.setBackground(SCREEN_BG);
        p.setBorder(new EmptyBorder(40, 50, 40, 50));

        String[] cols = {"DB_ID", "TICKET REF", "DESCRIPTION", "PRIORITY", "CURRENT STATUS", "ASSIGNED TO"};
        masterModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        masterTable = new JTable(masterModel);
        masterTable.setRowHeight(45);
        masterTable.getColumnModel().getColumn(0).setMinWidth(0);
        masterTable.getColumnModel().getColumn(0).setMaxWidth(0);

        JTableHeader header = masterTable.getTableHeader();
        header.setBackground(MES_GREEN);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 14));

        JButton btnSync = createStyledButton("SYNC WITH LIVE DATABASE");
        btnSync.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnSync.addActionListener(e -> refreshMasterTable());

        JButton btnTrack = createStyledButton("TRACK BY ID");
        btnTrack.addActionListener(e -> trackComplaintById());

        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        south.add(btnSync);
        south.add(btnTrack);

        p.add(new JScrollPane(masterTable), BorderLayout.CENTER);
        p.add(south, BorderLayout.SOUTH);
        return p;
    }

    private void trackComplaintById() {
        String idStr = JOptionPane.showInputDialog(this, "Enter Complaint ID:");
        if (idStr == null) return;
        try {
            int id = Integer.parseInt(idStr.trim());
            var cOpt = complaintDAO.findById(id);
            if (cOpt.isEmpty()) { JOptionPane.showMessageDialog(this, "Invalid Complaint ID."); return; }
            var c = cOpt.get();
            JOptionPane.showMessageDialog(this,
                    "Status: " + c.getStatusString() + "\nAssigned: " + (c.getAssignedToEngineerId() == null ? "No" : c.getAssignedToEngineerId()));
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void handleManualAssign() {
        int row = masterTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a complaint first."); return; }
        int cid = (Integer) masterModel.getValueAt(row, 0);

        try {
            List<com.mes.model.Engineer> available = complaintDAO.getAvailableEngineers();
            DefaultListModel<String> m = new DefaultListModel<>();
            available.forEach(en -> m.addElement(en.getUserId() + " - " + en.getName()));
            JList<String> list = new JList<>(m);
            list.setFixedCellHeight(35);

            int opt = JOptionPane.showConfirmDialog(this, new JScrollPane(list), "Select engineer to assign", JOptionPane.OK_CANCEL_OPTION);
            if (opt == JOptionPane.OK_OPTION) {
                String sel = list.getSelectedValue();
                if (sel == null) return;
                int eid = Integer.parseInt(sel.split(" - ")[0]);
                if (complaintDAO.assignEngineer(cid, eid)) {
                    JOptionPane.showMessageDialog(this, "Unit Dispatched Successfully.");
                    refreshMasterTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Assignment failed or already assigned.");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Assignment error: " + ex.getMessage());
        }
    }

    private void refreshMasterTable() {
        try {
            masterModel.setRowCount(0);
            List<Complaint> list = complaintDAO.getAllComplaints();
            for (Complaint c : list) {
                masterModel.addRow(new Object[] {
                    c.getComplaintId(),
                    "SR-" + String.format("%04d", c.getComplaintId()),
                    c.getTitle().toUpperCase(),
                    c.getPriorityString(),
                    c.getStatusString(),
                    (c.getAssignedToEngineerId() == null ? "NOT DISPATCHED" : "Engineer #" + c.getAssignedToEngineerId())
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "SYNC ERROR: " + e.getMessage());
        }
    }

    private JPanel createReportingPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(SCREEN_BG);
        JButton btn = createStyledButton("GENERATE STRATEGIC READINESS REPORT");
        btn.setPreferredSize(new Dimension(500, 100));
        btn.setFont(new Font("SansSerif", Font.BOLD, 18));
        btn.addActionListener(e -> generateStrategicReport());
        p.add(btn);
        return p;
    }

    private JPanel createActionCard(String title, String desc, java.awt.event.ActionListener action) {
        JPanel card = new JPanel(new BorderLayout(0, 15));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(320, 320));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTitle.setForeground(MES_GREEN);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblDesc = new JLabel("<html><center>" + desc + "</center></html>");
        lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblDesc.setForeground(Color.GRAY);
        lblDesc.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btn = createStyledButton("ACCESS MODULE");
        btn.addActionListener(action);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblDesc, BorderLayout.CENTER);
        card.add(btn, BorderLayout.SOUTH);
        return card;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(MES_GREEN);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}