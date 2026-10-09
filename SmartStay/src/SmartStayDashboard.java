import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * Task 4: Hotel Reservation System - Full Property Management System (PMS).
 *
 * Sidebar structure:
 * - MAIN:
 *     1. Dashboard
 *     2. Reservations
 *     3. Rooms
 *     4. Customers
 *     5. Payments
 * - ADMINISTRATION:
 *     6. Admin Center
 *     7. Reports
 *     8. Users
 * - BOTTOM ACTIONS:
 *     - Logout
 *     - Load Data
 *     - Save Data
 */
@SuppressWarnings("serial")
public final class SmartStayDashboard extends JFrame {

    // Executive Palette: Deep Midnight Navy & Royal Sapphire with Crisp Slate
    private static final Color BG_DARK = new Color(11, 19, 43);         // Midnight Navy
    private static final Color BG_DARK_HOVER = new Color(26, 38, 70);   // Dark Hover
    private static final Color BG_LIGHT = new Color(248, 250, 252);     // Slate 50
    private static final Color CARD_BG = Color.WHITE;
    private static final Color CARD_BORDER = new Color(226, 232, 240);  // Slate 200
    private static final Color TEXT_MAIN = new Color(15, 23, 42);       // Slate 900
    private static final Color TEXT_MUTED = new Color(100, 116, 139);   // Slate 500
    private static final Color PRIMARY_BLUE = new Color(37, 99, 235);   // Royal Blue (Save Data)
    private static final Color PRIMARY_HOVER = new Color(29, 78, 216);
    private static final Color SUCCESS = new Color(16, 185, 129);       // Emerald Ready
    private static final Color SUCCESS_LIGHT = new Color(236, 253, 245);
    private static final Color DANGER = new Color(239, 68, 68);         // Rose Occupied
    private static final Color DANGER_LIGHT = new Color(254, 242, 242);
    private static final Color WARNING = new Color(245, 158, 11);       // Amber Maintenance
    private static final Color WARNING_LIGHT = new Color(254, 243, 199);
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_STAT_NUM = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font FONT_BADGE = new Font("Segoe UI", Font.BOLD, 11);

    private final HotelManager hotel;
    private final LoginFrame.AuthUser currentUser;
    private final CardLayout contentCardLayout = new CardLayout();
    private final JPanel contentCardPanel = new JPanel(contentCardLayout);

    // Sidebar navigation buttons
    private final List<JButton> navButtons = new ArrayList<>();
    private String currentView = "DASHBOARD";

    // Dashboard dynamic components
    private JLabel lblTotalRooms;
    private JLabel lblAvailableRooms;
    private JLabel lblOccupiedRooms;
    private JLabel lblTotalRevenue;
    private JPanel roomGridContainer;
    private DefaultTableModel recentBookingsModel;
    private String roomGridFilter = "ALL";

    // Dynamic data models for all screens
    private DefaultTableModel allRoomsModel;
    private DefaultTableModel allReservationsModel;
    private DefaultTableModel allCustomersModel;
    private DefaultTableModel allPaymentsModel;
    private DefaultTableModel allUsersModel;

    // Reports labels
    private JLabel lblReportOccupancy;
    private JLabel lblReportRevenue;
    private JLabel lblReportBookings;
    private JLabel lblReportPopular;

    public SmartStayDashboard(HotelManager hotel) {
        this(hotel, new LoginFrame.AuthUser("admin", "Administrator", "ADMIN", "Full PMS Access"));
    }

    public SmartStayDashboard(HotelManager hotel, LoginFrame.AuthUser currentUser) {
        super("SmartStay | Hotel Property Management System");
        this.hotel = hotel;
        this.currentUser = currentUser != null ? currentUser : new LoginFrame.AuthUser("admin", "Administrator", "ADMIN", "Full PMS Access");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1320, 840);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);

        initUI();
        refreshAllData();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_LIGHT);

        // 1. Left Sidebar
        JPanel sidebar = buildSidebar();
        root.add(sidebar, BorderLayout.WEST);

        // 2. Main Work Area
        JPanel mainArea = new JPanel(new BorderLayout(0, 0));
        mainArea.setBackground(BG_LIGHT);

        JPanel topHeader = buildTopHeader();
        mainArea.add(topHeader, BorderLayout.NORTH);

        // 3. Register All 8 Views into CardLayout
        contentCardPanel.add(buildDashboardView(), "DASHBOARD");
        contentCardPanel.add(buildReservationsView(), "RESERVATIONS");
        contentCardPanel.add(buildRoomsView(), "ROOMS");
        contentCardPanel.add(buildCustomersView(), "CUSTOMERS");
        contentCardPanel.add(buildPaymentsView(), "PAYMENTS");
        contentCardPanel.add(buildAdminCenterView(), "ADMIN_CENTER");
        contentCardPanel.add(buildReportsView(), "REPORTS");
        contentCardPanel.add(buildUsersView(), "USERS");

        mainArea.add(contentCardPanel, BorderLayout.CENTER);
        root.add(mainArea, BorderLayout.CENTER);

        setContentPane(root);
    }

    // =========================================================================
    // 1. SIDEBAR (Matching Exact User Specification)
    // =========================================================================

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(BG_DARK);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(new EmptyBorder(20, 14, 20, 14));

        // Top Brand Panel
        JPanel topBrand = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBrand.setOpaque(false);

        JLabel logoSquare = new JLabel("SS", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_BLUE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoSquare.setForeground(Color.WHITE);
        logoSquare.setFont(new Font("Segoe UI", Font.BOLD, 15));
        logoSquare.setPreferredSize(new Dimension(38, 38));

        JPanel brandText = new JPanel(new GridLayout(2, 1, 0, 1));
        brandText.setOpaque(false);
        JLabel brandName = new JLabel("SmartStay");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        brandName.setForeground(Color.WHITE);
        JLabel brandTag = new JLabel("HOTEL PMS");
        brandTag.setFont(new Font("Segoe UI", Font.BOLD, 10));
        brandTag.setForeground(new Color(148, 163, 184));
        brandText.add(brandName);
        brandText.add(brandTag);

        topBrand.add(logoSquare);
        topBrand.add(brandText);

        // Center Nav Items Container
        JPanel navContainer = new JPanel();
        navContainer.setLayout(new BoxLayout(navContainer, BoxLayout.Y_AXIS));
        navContainer.setOpaque(false);
        navContainer.setBorder(new EmptyBorder(22, 0, 10, 0));

        // --- MAIN SECTION ---
        navContainer.add(createSectionLabel("MAIN"));
        navContainer.add(Box.createRigidArea(new Dimension(0, 8)));
        navContainer.add(createNavButton("⊞  Dashboard", "DASHBOARD", true));
        navContainer.add(Box.createRigidArea(new Dimension(0, 4)));
        navContainer.add(createNavButton("📅  Reservations", "RESERVATIONS", false));
        navContainer.add(Box.createRigidArea(new Dimension(0, 4)));
        navContainer.add(createNavButton("🏨  Rooms", "ROOMS", false));
        navContainer.add(Box.createRigidArea(new Dimension(0, 4)));
        navContainer.add(createNavButton("👤  Customers", "CUSTOMERS", false));
        navContainer.add(Box.createRigidArea(new Dimension(0, 4)));
        navContainer.add(createNavButton("💳  Payments", "PAYMENTS", false));

        navContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- ADMINISTRATION SECTION ---
        navContainer.add(createSectionLabel("ADMINISTRATION"));
        navContainer.add(Box.createRigidArea(new Dimension(0, 8)));
        navContainer.add(createNavButton("⚙️  Admin Center", "ADMIN_CENTER", false));
        navContainer.add(Box.createRigidArea(new Dimension(0, 4)));
        navContainer.add(createNavButton("📊  Reports", "REPORTS", false));
        navContainer.add(Box.createRigidArea(new Dimension(0, 4)));
        navContainer.add(createNavButton("👥  Users", "USERS", false));

        // Bottom Action Buttons (Logout, Load Data, Save Data)
        JPanel bottomActions = new JPanel();
        bottomActions.setLayout(new BoxLayout(bottomActions, BoxLayout.Y_AXIS));
        bottomActions.setOpaque(false);

        JButton btnLogout = createBottomWhiteButton("Logout");
        btnLogout.addActionListener(e -> handleLogout());

        JButton btnLoadData = createBottomWhiteButton("Load Data");
        btnLoadData.addActionListener(e -> handleLoadData());

        JButton btnSaveData = createBottomBlueButton("Save Data");
        btnSaveData.addActionListener(e -> handleSaveData());

        bottomActions.add(btnLogout);
        bottomActions.add(Box.createRigidArea(new Dimension(0, 8)));
        bottomActions.add(btnLoadData);
        bottomActions.add(Box.createRigidArea(new Dimension(0, 8)));
        bottomActions.add(btnSaveData);

        sidebar.add(topBrand, BorderLayout.NORTH);
        sidebar.add(navContainer, BorderLayout.CENTER);
        sidebar.add(bottomActions, BorderLayout.SOUTH);

        return sidebar;
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 10));
        label.setForeground(new Color(148, 163, 184));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 10, 0, 0));
        return label;
    }

    private JButton createNavButton(String title, String viewKey, boolean active) {
        JButton btn = new JButton(title) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean isCurrent = currentView.equals(viewKey);
                if (isCurrent) {
                    g2.setColor(PRIMARY_BLUE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                } else if (getModel().isRollover()) {
                    g2.setColor(BG_DARK_HOVER);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(202, 36));
        btn.setPreferredSize(new Dimension(202, 36));
        btn.setFont(FONT_BOLD);
        btn.setForeground(active ? Color.WHITE : new Color(203, 213, 225));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 12, 0, 0));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> switchView(viewKey));
        navButtons.add(btn);
        return btn;
    }

    private JButton createBottomWhiteButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(241, 245, 249) : Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(202, 36));
        btn.setPreferredSize(new Dimension(202, 36));
        btn.setFont(FONT_BOLD);
        btn.setForeground(new Color(15, 23, 42)); // Dark text
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createBottomBlueButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? PRIMARY_HOVER : PRIMARY_BLUE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(202, 36));
        btn.setPreferredSize(new Dimension(202, 36));
        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void switchView(String viewKey) {
        currentView = viewKey;
        contentCardLayout.show(contentCardPanel, viewKey);
        for (JButton btn : navButtons) {
            btn.repaint();
        }
    }

    // =========================================================================
    // 2. TOP HEADER
    // =========================================================================

    private JPanel buildTopHeader() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, CARD_BORDER),
                new EmptyBorder(12, 28, 12, 28)
        ));

        // Left Header Titles
        JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 2));
        titleGroup.setOpaque(false);
        JLabel title = new JLabel("Dashboard");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_MAIN);
        JLabel subtitle = new JLabel("Overview of today's hotel operations & room inventory");
        subtitle.setFont(FONT_SUBTITLE);
        subtitle.setForeground(TEXT_MUTED);
        titleGroup.add(title);
        titleGroup.add(subtitle);

        // Right Actions & Profile (matching screenshot header with badge & + New Reservation)
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightActions.setOpaque(false);

        // Online Badge (Clickable)
        JPanel onlineBadge = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(SUCCESS);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        onlineBadge.setOpaque(false);
        onlineBadge.setBackground(SUCCESS_LIGHT);
        onlineBadge.setCursor(new Cursor(Cursor.HAND_CURSOR));
        onlineBadge.setToolTipText("System Online - Click to check connection");
        JLabel dot = new JLabel("●");
        dot.setForeground(SUCCESS);
        dot.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        JLabel onlineText = new JLabel("Online");
        onlineText.setFont(FONT_BADGE);
        onlineText.setForeground(new Color(5, 150, 105));
        onlineBadge.add(dot);
        onlineBadge.add(onlineText);
        onlineBadge.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(SmartStayDashboard.this,
                        "System Status: ONLINE\n\n"
                                + "• Storage Engine : Local File I/O Persistence\n"
                                + "• Active Directory: data/\n"
                                + "• Rooms Synchronized: 20 Rooms\n"
                                + "• Double-Booking Lock: Active",
                        "System Connectivity & Health", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Administrator Pill (Clickable Profile Menu)
        JPanel adminPill = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(CARD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        adminPill.setOpaque(false);
        adminPill.setBackground(BG_LIGHT);
        adminPill.setBorder(new EmptyBorder(2, 8, 2, 8));
        adminPill.setCursor(new Cursor(Cursor.HAND_CURSOR));
        adminPill.setToolTipText("Administrator Profile - Click for options");

        String initial = (currentUser != null && !currentUser.fullName().isEmpty())
                ? currentUser.fullName().substring(0, 1).toUpperCase() : "A";
        JLabel avatar = new JLabel(initial, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_BLUE);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        avatar.setPreferredSize(new Dimension(24, 24));

        JLabel adminName = new JLabel(currentUser != null ? currentUser.fullName() : "Administrator");
        adminName.setFont(FONT_BOLD);
        adminName.setForeground(TEXT_MAIN);

        JLabel roleLabel = new JLabel(currentUser != null ? currentUser.role() : "ADMIN");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        roleLabel.setForeground(TEXT_MUTED);

        adminPill.add(avatar);
        adminPill.add(adminName);
        adminPill.add(roleLabel);

        adminPill.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                adminPill.setBackground(new Color(241, 245, 249));
                adminPill.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                adminPill.setBackground(BG_LIGHT);
                adminPill.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                showAdminProfilePopup(adminPill);
            }
        });

        // + New reservation blue button
        JButton btnNewRes = new JButton("+ New reservation") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? PRIMARY_HOVER : PRIMARY_BLUE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnNewRes.setFont(FONT_BOLD);
        btnNewRes.setForeground(Color.WHITE);
        btnNewRes.setFocusPainted(false);
        btnNewRes.setBorderPainted(false);
        btnNewRes.setContentAreaFilled(false);
        btnNewRes.setPreferredSize(new Dimension(150, 36));
        btnNewRes.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNewRes.addActionListener(e -> openNewReservationDialog(null));

        rightActions.add(onlineBadge);
        rightActions.add(adminPill);
        rightActions.add(btnNewRes);

        header.add(titleGroup, BorderLayout.WEST);
        header.add(rightActions, BorderLayout.EAST);
        return header;
    }

    // =========================================================================
    // 3. VIEW 1: DASHBOARD
    // =========================================================================

    private JScrollPane buildDashboardView() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(BG_LIGHT);
        container.setBorder(new EmptyBorder(20, 28, 24, 28));

        // Greeting Header
        JPanel banner = new JPanel(new BorderLayout(16, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(CARD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        banner.setBorder(new EmptyBorder(16, 20, 16, 20));
        banner.setOpaque(false);
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        JPanel bannerText = new JPanel(new GridLayout(2, 1, 0, 2));
        bannerText.setOpaque(false);
        JLabel greet = new JLabel("Good morning, Administrator");
        greet.setFont(FONT_HEADER);
        greet.setForeground(TEXT_MAIN);
        JLabel greetSub = new JLabel("Here's today's hotel operations overview across all 20 rooms.");
        greetSub.setFont(FONT_SUBTITLE);
        greetSub.setForeground(TEXT_MUTED);
        bannerText.add(greet);
        bannerText.add(greetSub);

        JLabel dateLabel = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")));
        dateLabel.setFont(FONT_BOLD);
        dateLabel.setForeground(PRIMARY_BLUE);

        banner.add(bannerText, BorderLayout.WEST);
        banner.add(dateLabel, BorderLayout.EAST);
        container.add(banner);
        container.add(Box.createRigidArea(new Dimension(0, 18)));

        // 4 KPI Stat Cards Grid (Total Rooms, Available, Occupied, Total Revenue)
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        kpiGrid.setOpaque(false);
        kpiGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        lblTotalRooms = new JLabel("20");
        lblAvailableRooms = new JLabel("20");
        lblOccupiedRooms = new JLabel("0");
        lblTotalRevenue = new JLabel("₹0.00");

        kpiGrid.add(createKpiCard("TOTAL ROOMS", lblTotalRooms, "Hotel inventory", ACCENT_BLUE));
        kpiGrid.add(createKpiCard("AVAILABLE", lblAvailableRooms, "Ready for guests", SUCCESS));
        kpiGrid.add(createKpiCard("OCCUPIED", lblOccupiedRooms, "Currently occupied", DANGER));
        kpiGrid.add(createKpiCard("TOTAL REVENUE", lblTotalRevenue, "Total earned", WARNING));

        container.add(kpiGrid);
        container.add(Box.createRigidArea(new Dimension(0, 22)));

        // Lower Section: Room Matrix on Left, Recent Bookings Table on Right
        JPanel splitSection = new JPanel(new GridBagLayout());
        splitSection.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Left Panel: Interactive Room Matrix Grid
        gbc.gridx = 0;
        gbc.weightx = 0.58;
        gbc.insets = new Insets(0, 0, 0, 10);
        splitSection.add(buildRoomMatrixPanel(), gbc);

        // Right Panel: Recent Reservations Feed
        gbc.gridx = 1;
        gbc.weightx = 0.42;
        gbc.insets = new Insets(0, 10, 0, 0);
        splitSection.add(buildRecentBookingsPanel(), gbc);

        container.add(splitSection);

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel createKpiCard(String labelText, JLabel numLabel, String subtitleText, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 6)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(CARD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                g2.setColor(accent);
                g2.fillRoundRect(12, 0, 36, 4, 2, 2);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 18, 14, 18));

        JLabel title = new JLabel(labelText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 11));
        title.setForeground(TEXT_MUTED);

        numLabel.setFont(FONT_STAT_NUM);
        numLabel.setForeground(TEXT_MAIN);

        JLabel sub = new JLabel(subtitleText);
        sub.setFont(FONT_SUBTITLE);
        sub.setForeground(TEXT_MUTED);

        JPanel topGroup = new JPanel(new GridLayout(2, 1, 0, 2));
        topGroup.setOpaque(false);
        topGroup.add(title);
        topGroup.add(numLabel);

        card.add(topGroup, BorderLayout.CENTER);
        card.add(sub, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildRoomMatrixPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(CARD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(16, 18, 16, 18));
        panel.setPreferredSize(new Dimension(500, 460));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JLabel title = new JLabel("Live Room Grid");
        title.setFont(FONT_HEADER);
        title.setForeground(TEXT_MAIN);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        filterBar.setOpaque(false);
        filterBar.add(createFilterButton("All", "ALL"));
        filterBar.add(createFilterButton("Standard", "Standard"));
        filterBar.add(createFilterButton("Deluxe", "Deluxe"));
        filterBar.add(createFilterButton("Suite", "Suite"));

        topBar.add(title, BorderLayout.WEST);
        topBar.add(filterBar, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        roomGridContainer = new JPanel(new GridLayout(0, 4, 10, 10));
        roomGridContainer.setOpaque(false);

        JScrollPane scroll = new JScrollPane(roomGridContainer);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JButton createFilterButton(String text, String filterVal) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BADGE);
        btn.setForeground(roomGridFilter.equals(filterVal) ? PRIMARY_BLUE : TEXT_MUTED);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(roomGridFilter.equals(filterVal) ? PRIMARY_BLUE : CARD_BORDER, 1),
                new EmptyBorder(4, 8, 4, 8)
        ));
        btn.setBackground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            roomGridFilter = filterVal;
            refreshRoomGrid();
        });
        return btn;
    }

    private void refreshRoomGrid() {
        if (roomGridContainer == null) return;
        roomGridContainer.removeAll();

        List<Room> allRooms = hotel.getAllRooms();
        for (Room r : allRooms) {
            if (!roomGridFilter.equals("ALL") && !r.getRoomType().equalsIgnoreCase(roomGridFilter)) {
                continue;
            }
            roomGridContainer.add(createRoomTile(r));
        }

        roomGridContainer.revalidate();
        roomGridContainer.repaint();
    }

    private JPanel createRoomTile(Room room) {
        boolean isAvailable = room.getStatus() == RoomStatus.AVAILABLE;
        boolean isMaintenance = room.getStatus() == RoomStatus.MAINTENANCE;

        Color tileBg = isAvailable ? SUCCESS_LIGHT : (isMaintenance ? WARNING_LIGHT : DANGER_LIGHT);
        Color tileBorder = isAvailable ? SUCCESS : (isMaintenance ? WARNING : DANGER);
        Color statusColor = isAvailable ? new Color(5, 150, 105) : (isMaintenance ? new Color(217, 119, 6) : new Color(220, 38, 38));
        String statusText = isAvailable ? "READY" : (isMaintenance ? "MAINTENANCE" : "BOOKED");

        JPanel tile = new JPanel(new BorderLayout(0, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(tileBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(tileBorder);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tile.setOpaque(false);
        tile.setBorder(new EmptyBorder(10, 10, 10, 10));
        tile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        tile.setPreferredSize(new Dimension(100, 80));

        JLabel num = new JLabel("ROOM " + room.getRoomNumber());
        num.setFont(new Font("Segoe UI", Font.BOLD, 12));
        num.setForeground(TEXT_MAIN);

        JLabel type = new JLabel(room.getRoomType() + " | ₹" + room.getPricePerNight());
        type.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        type.setForeground(TEXT_MUTED);

        JLabel status = new JLabel(statusText);
        status.setFont(new Font("Segoe UI", Font.BOLD, 10));
        status.setForeground(statusColor);

        tile.add(num, BorderLayout.NORTH);
        tile.add(type, BorderLayout.CENTER);
        tile.add(status, BorderLayout.SOUTH);

        tile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleRoomTileClick(room);
            }
        });

        return tile;
    }

    private void handleRoomTileClick(Room room) {
        if (room.getStatus() == RoomStatus.AVAILABLE) {
            int opt = JOptionPane.showConfirmDialog(this,
                    "Room " + room.getRoomNumber() + " (" + room.getRoomType() + " - ₹" + room.getPricePerNight() + "/night)\n"
                            + "Capacity: " + room.getMaxGuests() + " Guests\n"
                            + "Amenities: " + room.getAmenities() + "\n\n"
                            + "Would you like to book this room now?",
                    "Book Room " + room.getRoomNumber(),
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (opt == JOptionPane.YES_OPTION) {
                openNewReservationDialog(room);
            }
        } else if (room.getStatus() == RoomStatus.BOOKED) {
            Reservation active = null;
            for (Reservation r : hotel.getAllReservations()) {
                if (r.getRoomNumber() == room.getRoomNumber() && r.getBookingStatus() == BookingStatus.CONFIRMED) {
                    active = r;
                    break;
                }
            }
            if (active != null) {
                showBookingDetailsDialog(active);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Room " + room.getRoomNumber() + " is currently occupied.",
                        "Room Occupied", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            int opt = JOptionPane.showConfirmDialog(this,
                    "Room " + room.getRoomNumber() + " is under Maintenance.\nPut back into active service?",
                    "Maintenance Mode", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                hotel.setRoomMaintenance(room.getRoomNumber(), false);
                refreshAllData();
            }
        }
    }

    private JPanel buildRecentBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(CARD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(16, 18, 16, 18));
        panel.setPreferredSize(new Dimension(420, 460));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        JLabel title = new JLabel("Recent Bookings");
        title.setFont(FONT_HEADER);
        title.setForeground(TEXT_MAIN);

        JButton btnViewAll = new JButton("View All →");
        btnViewAll.setFont(FONT_BADGE);
        btnViewAll.setForeground(PRIMARY_BLUE);
        btnViewAll.setContentAreaFilled(false);
        btnViewAll.setBorderPainted(false);
        btnViewAll.setFocusPainted(false);
        btnViewAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnViewAll.addActionListener(e -> switchView("RESERVATIONS"));

        topBar.add(title, BorderLayout.WEST);
        topBar.add(btnViewAll, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"ID", "Guest", "Room", "Stay", "Amount", "Status"};
        recentBookingsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = styleTable(new JTable(recentBookingsModel));
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        String bookingId = (String) table.getValueAt(row, 0);
                        Reservation res = hotel.findReservation(bookingId);
                        if (res != null) {
                            showBookingDetailsDialog(res);
                        }
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    // 4. VIEW 2: RESERVATIONS
    // =========================================================================

    private JPanel buildReservationsView() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(new EmptyBorder(20, 28, 24, 28));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Reservations Management");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_MAIN);
        JLabel subtitle = new JLabel("View guest bookings, print folios, or process cancellations");
        subtitle.setFont(FONT_SUBTITLE);
        subtitle.setForeground(TEXT_MUTED);

        JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 2));
        titleGroup.setOpaque(false);
        titleGroup.add(title);
        titleGroup.add(subtitle);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton btnViewFolio = createActionButton("View Folio / Details", Color.WHITE, TEXT_MAIN, CARD_BORDER);
        JButton btnCancel = createActionButton("Cancel Reservation", DANGER, Color.WHITE, DANGER);
        btnCancel.addActionListener(e -> promptCancelReservation());

        actions.add(btnViewFolio);
        actions.add(btnCancel);
        header.add(titleGroup, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        String[] cols = {"Booking ID", "Guest Name", "Phone", "Room #", "Type", "Check-In", "Check-Out", "Amount", "Status"};
        allReservationsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = styleTable(new JTable(allReservationsModel));
        btnViewFolio.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a reservation from the table first.", "Notice", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String bId = (String) table.getValueAt(row, 0);
            Reservation res = hotel.findReservation(bId);
            if (res != null) {
                showBookingDetailsDialog(res);
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        String bId = (String) table.getValueAt(row, 0);
                        Reservation res = hotel.findReservation(bId);
                        if (res != null) {
                            showBookingDetailsDialog(res);
                        }
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    // 5. VIEW 3: ROOMS
    // =========================================================================

    private JPanel buildRoomsView() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(new EmptyBorder(20, 28, 24, 28));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Rooms Inventory");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_MAIN);
        JLabel subtitle = new JLabel("All 20 rooms categorized across Standard, Deluxe, and Suite");
        subtitle.setFont(FONT_SUBTITLE);
        subtitle.setForeground(TEXT_MUTED);

        JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 2));
        titleGroup.setOpaque(false);
        titleGroup.add(title);
        titleGroup.add(subtitle);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton btnToggleMaint = createActionButton("Toggle Maintenance", Color.WHITE, TEXT_MAIN, CARD_BORDER);
        btnToggleMaint.addActionListener(e -> promptMaintenanceToggle());
        actions.add(btnToggleMaint);

        header.add(titleGroup, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        String[] cols = {"Room #", "Floor", "Category", "Price / Night", "Max Guests", "Status", "Amenities"};
        allRoomsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = styleTable(new JTable(allRoomsModel));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    // 6. VIEW 4: CUSTOMERS (New Tab from Screenshot)
    // =========================================================================

    private JPanel buildCustomersView() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(new EmptyBorder(20, 28, 24, 28));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Customer Directory");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_MAIN);
        JLabel subtitle = new JLabel("Registered guest profiles and loyalty tier status");
        subtitle.setFont(FONT_SUBTITLE);
        subtitle.setForeground(TEXT_MUTED);

        JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 2));
        titleGroup.setOpaque(false);
        titleGroup.add(title);
        titleGroup.add(subtitle);

        header.add(titleGroup, BorderLayout.WEST);
        panel.add(header, BorderLayout.NORTH);

        String[] cols = {"Customer ID", "Full Name", "Phone Number", "Email Address", "Loyalty Tier"};
        allCustomersModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = styleTable(new JTable(allCustomersModel));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    // 7. VIEW 5: PAYMENTS
    // =========================================================================

    private JPanel buildPaymentsView() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(new EmptyBorder(20, 28, 24, 28));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Payment Audit Trail");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_MAIN);
        JLabel subtitle = new JLabel("Simulated transactions across Credit Card, UPI, and Cash");
        subtitle.setFont(FONT_SUBTITLE);
        subtitle.setForeground(TEXT_MUTED);

        JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 2));
        titleGroup.setOpaque(false);
        titleGroup.add(title);
        titleGroup.add(subtitle);
        header.add(titleGroup, BorderLayout.WEST);
        panel.add(header, BorderLayout.NORTH);

        String[] cols = {"Transaction ID", "Booking ID", "Amount", "Method", "Details", "Status", "Timestamp"};
        allPaymentsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = styleTable(new JTable(allPaymentsModel));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    // 8. VIEW 6: ADMIN CENTER (Administration Section)
    // =========================================================================

    private JScrollPane buildAdminCenterView() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(BG_LIGHT);
        container.setBorder(new EmptyBorder(20, 28, 24, 28));

        JLabel title = new JLabel("Admin Control Center");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_MAIN);
        JLabel subtitle = new JLabel("Hotel property configurations, room inventory maintenance, and data management");
        subtitle.setFont(FONT_SUBTITLE);
        subtitle.setForeground(TEXT_MUTED);

        container.add(title);
        container.add(subtitle);
        container.add(Box.createRigidArea(new Dimension(0, 20)));

        // Admin Action Cards Grid
        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 360));

        // Card 1: Room Maintenance
        grid.add(createAdminCard("Room Maintenance Mode",
                "Take a room out of service for repairs or reactivate an existing room.",
                "Toggle Maintenance", e -> promptMaintenanceToggle()));

        // Card 2: Add New Room
        grid.add(createAdminCard("Add New Room",
                "Expand property inventory by provisioning a new Standard, Deluxe, or Suite room.",
                "+ Provision Room", e -> promptAddNewRoom()));

        // Card 3: File I/O Storage Status
        grid.add(createAdminCard("File I/O Persistence",
                "All bookings, payments, and customers are backed up into data/ text files.",
                "Flush / Save to Disk", e -> handleSaveData()));

        // Card 4: Reload Data
        grid.add(createAdminCard("Database Sync & Reload",
                "Re-sync and reload all data files from disk storage into live memory.",
                "Reload from Disk", e -> handleLoadData()));

        container.add(grid);

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBorder(null);
        return scroll;
    }

    private JPanel createAdminCard(String cardTitle, String desc, String btnText, ActionListener action) {
        JPanel card = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(CARD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel t = new JLabel(cardTitle);
        t.setFont(FONT_HEADER);
        t.setForeground(TEXT_MAIN);

        JLabel d = new JLabel("<html>" + desc + "</html>");
        d.setFont(FONT_BODY);
        d.setForeground(TEXT_MUTED);

        JButton btn = createActionButton(btnText, PRIMARY_BLUE, Color.WHITE, PRIMARY_BLUE);
        btn.addActionListener(action);

        card.add(t, BorderLayout.NORTH);
        card.add(d, BorderLayout.CENTER);
        card.add(btn, BorderLayout.SOUTH);

        return card;
    }

    private void promptAddNewRoom() {
        JTextField txtNumber = new JTextField();
        JTextField txtFloor = new JTextField();
        JComboBox<String> comboType = new JComboBox<>(new String[]{"Standard", "Deluxe", "Suite"});

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.add(new JLabel("Room Number (e.g. 109):"));
        form.add(txtNumber);
        form.add(new JLabel("Floor Number:"));
        form.add(txtFloor);
        form.add(new JLabel("Room Category:"));
        form.add(comboType);

        int opt = JOptionPane.showConfirmDialog(this, form, "Add New Hotel Room", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                int rNum = Integer.parseInt(txtNumber.getText().trim());
                int floor = Integer.parseInt(txtFloor.getText().trim());
                String type = (String) comboType.getSelectedItem();
                hotel.addRoom(type, rNum, floor);
                refreshAllData();
                JOptionPane.showMessageDialog(this, "Room " + rNum + " added successfully!", "Room Added", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding room: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // 9. VIEW 7: REPORTS (Administration Section)
    // =========================================================================

    private JScrollPane buildReportsView() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(BG_LIGHT);
        container.setBorder(new EmptyBorder(20, 28, 24, 28));

        JLabel title = new JLabel("Executive Reports & Analytics");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_MAIN);
        JLabel subtitle = new JLabel("Occupancy rates, revenue analysis, and booking category breakdowns");
        subtitle.setFont(FONT_SUBTITLE);
        subtitle.setForeground(TEXT_MUTED);

        container.add(title);
        container.add(subtitle);
        container.add(Box.createRigidArea(new Dimension(0, 20)));

        // Report KPI Cards
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        kpiGrid.setOpaque(false);
        kpiGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        lblReportOccupancy = new JLabel("0%");
        lblReportRevenue = new JLabel("₹0.00");
        lblReportBookings = new JLabel("0");
        lblReportPopular = new JLabel("Standard");

        kpiGrid.add(createKpiCard("OCCUPANCY RATE", lblReportOccupancy, "Current capacity utilized", PRIMARY_BLUE));
        kpiGrid.add(createKpiCard("TOTAL REVENUE", lblReportRevenue, "Paid transactions", SUCCESS));
        kpiGrid.add(createKpiCard("TOTAL RESERVATIONS", lblReportBookings, "All-time bookings", ACCENT_BLUE));
        kpiGrid.add(createKpiCard("TOP CATEGORY", lblReportPopular, "Most popular room type", WARNING));

        container.add(kpiGrid);
        container.add(Box.createRigidArea(new Dimension(0, 24)));

        // Printable Summary Card
        JPanel summaryCard = new JPanel(new BorderLayout(0, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(CARD_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        summaryCard.setOpaque(false);
        summaryCard.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel sumTitle = new JLabel("Property Performance Summary");
        sumTitle.setFont(FONT_HEADER);
        sumTitle.setForeground(TEXT_MAIN);

        JTextArea txtReport = new JTextArea(8, 50);
        txtReport.setEditable(false);
        txtReport.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtReport.setBackground(new Color(248, 250, 252));
        txtReport.setBorder(new EmptyBorder(12, 12, 12, 12));
        txtReport.setText("SmartStay Hotel Operations Report\n"
                + "Generated on: " + LocalDate.now() + "\n\n"
                + "Total Rooms Inventory: 20 Rooms\n"
                + " - Standard: 8 Rooms (₹2,000/night)\n"
                + " - Deluxe: 8 Rooms (₹3,500/night)\n"
                + " - Suite: 4 Rooms (₹5,000/night)\n\n"
                + "Persistence: Active File I/O in 'data/' directory.\n"
                + "Double booking prevention: 100% Guaranteed via date overlap checks.");

        summaryCard.add(sumTitle, BorderLayout.NORTH);
        summaryCard.add(txtReport, BorderLayout.CENTER);

        container.add(summaryCard);

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBorder(null);
        return scroll;
    }

    // =========================================================================
    // 10. VIEW 8: USERS (Administration Section)
    // =========================================================================

    private JPanel buildUsersView() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(new EmptyBorder(20, 28, 24, 28));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("System Users & Staff Directory");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_MAIN);
        JLabel subtitle = new JLabel("Authorized front desk staff and administrator credentials");
        subtitle.setFont(FONT_SUBTITLE);
        subtitle.setForeground(TEXT_MUTED);

        JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 2));
        titleGroup.setOpaque(false);
        titleGroup.add(title);
        titleGroup.add(subtitle);

        header.add(titleGroup, BorderLayout.WEST);
        panel.add(header, BorderLayout.NORTH);

        String[] cols = {"Username", "Full Name", "Role", "Assigned Shift", "Access Level", "Status"};
        allUsersModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        allUsersModel.addRow(new Object[]{"admin", "System Administrator", "Hotel General Manager", "All Shifts", "Full PMS Access", "Active"});
        allUsersModel.addRow(new Object[]{"desk_sarah", "Sarah Jenkins", "Front Desk Officer", "Morning (08:00 - 16:00)", "Bookings & Check-in", "Active"});
        allUsersModel.addRow(new Object[]{"desk_rahul", "Rahul Sharma", "Front Desk Officer", "Evening (16:00 - 00:00)", "Bookings & Check-in", "Active"});
        allUsersModel.addRow(new Object[]{"night_auditor", "Michael Chang", "Night Auditor", "Night (00:00 - 08:00)", "Daily Audit & Folios", "Active"});
        allUsersModel.addRow(new Object[]{"housekeeping", "Elena Rostova", "Housekeeping Supervisor", "Day Shift", "Room Status & Maintenance", "Active"});

        JTable table = styleTable(new JTable(allUsersModel));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(CARD_BORDER, 1));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    // 11. PROFILE POPUP & BOTTOM BUTTON HANDLERS
    // =========================================================================

    private void showAdminProfilePopup(Component source) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                new EmptyBorder(6, 6, 6, 6)
        ));

        JMenuItem itemProfile = new JMenuItem("👤 " + (currentUser != null ? currentUser.fullName() : "Administrator")
                + " (" + (currentUser != null ? currentUser.role() : "ADMIN") + ")");
        itemProfile.setFont(FONT_BOLD);
        itemProfile.setEnabled(false);

        JMenuItem itemUsers = new JMenuItem("👥 View Staff & Users Directory");
        itemUsers.setFont(FONT_BODY);
        itemUsers.addActionListener(e -> switchView("USERS"));

        JMenuItem itemAdminCenter = new JMenuItem("⚙️ Open Admin Control Center");
        itemAdminCenter.setFont(FONT_BODY);
        itemAdminCenter.addActionListener(e -> switchView("ADMIN_CENTER"));

        JMenuItem itemLogout = new JMenuItem("🚪 Logout & Return to Login Screen");
        itemLogout.setFont(FONT_BODY);
        itemLogout.setForeground(DANGER);
        itemLogout.addActionListener(e -> handleLogout());

        menu.add(itemProfile);
        menu.addSeparator();
        menu.add(itemUsers);
        menu.add(itemAdminCenter);
        menu.addSeparator();
        menu.add(itemLogout);

        menu.show(source, 0, source.getHeight() + 4);
    }

    private void handleLogout() {
        String name = currentUser != null ? currentUser.fullName() : "Administrator";
        int opt = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out of " + name + " session?",
                "Logout Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opt == JOptionPane.YES_OPTION) {
            LoginFrame login = new LoginFrame(hotel);
            login.setVisible(true);
            dispose();
        }
    }

    private void handleLoadData() {
        try {
            hotel.reloadAll();
            refreshAllData();
            JOptionPane.showMessageDialog(this,
                    "✓ Data reloaded successfully from 'data/' text files.\n"
                            + "Synchronized: rooms.txt, customers.txt, bookings.txt, and payments.txt.",
                    "Data Loaded", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSaveData() {
        try {
            hotel.saveAll();
            JOptionPane.showMessageDialog(this,
                    "✓ All data saved successfully to disk!\n"
                            + "Updated: rooms.txt, customers.txt, bookings.txt, and payments.txt.",
                    "Data Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // 12. NEW RESERVATION WIZARD & MODAL
    // =========================================================================

    private void openNewReservationDialog(Room preSelectedRoom) {
        JDialog dialog = new JDialog(this, "Create New Hotel Reservation", true);
        dialog.setSize(520, 640);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel("New Guest Reservation");
        title.setFont(FONT_HEADER);
        title.setForeground(TEXT_MAIN);
        form.add(title);
        form.add(Box.createRigidArea(new Dimension(0, 16)));

        JTextField txtName = new JTextField();
        JTextField txtPhone = new JTextField();
        JTextField txtEmail = new JTextField();

        form.add(createFormField("Guest Full Name", txtName));
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(createFormField("Phone Number (10 digits)", txtPhone));
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(createFormField("Email Address", txtEmail));
        form.add(Box.createRigidArea(new Dimension(0, 14)));

        JComboBox<Room> roomCombo = new JComboBox<>();
        for (Room r : hotel.getAllRooms()) {
            if (r.getStatus() == RoomStatus.AVAILABLE || (preSelectedRoom != null && r.getRoomNumber() == preSelectedRoom.getRoomNumber())) {
                roomCombo.addItem(r);
            }
        }
        if (preSelectedRoom != null) {
            roomCombo.setSelectedItem(preSelectedRoom);
        }

        roomCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Room r) {
                    setText("Room " + r.getRoomNumber() + " (" + r.getRoomType() + " - ₹" + r.getPricePerNight() + "/night - max " + r.getMaxGuests() + " guests)");
                }
                return this;
            }
        });

        JTextField txtCheckIn = new JTextField(LocalDate.now().toString());
        JTextField txtCheckOut = new JTextField(LocalDate.now().plusDays(1).toString());
        JComboBox<Integer> guestsCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4});

        form.add(createFormField("Select Room", roomCombo));
        form.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel datesPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        datesPanel.setOpaque(false);
        datesPanel.add(createFormField("Check-In (YYYY-MM-DD)", txtCheckIn));
        datesPanel.add(createFormField("Check-Out (YYYY-MM-DD)", txtCheckOut));
        form.add(datesPanel);
        form.add(Box.createRigidArea(new Dimension(0, 10)));

        form.add(createFormField("Number of Guests", guestsCombo));
        form.add(Box.createRigidArea(new Dimension(0, 14)));

        JComboBox<String> paymentMethodCombo = new JComboBox<>(new String[]{"Credit / Debit Card", "UPI", "Cash"});
        JTextField txtPaymentDetail = new JTextField("4111222233334444");

        paymentMethodCombo.addActionListener(e -> {
            String sel = (String) paymentMethodCombo.getSelectedItem();
            if ("UPI".equals(sel)) {
                txtPaymentDetail.setText("guest@upi");
            } else if ("Cash".equals(sel)) {
                txtPaymentDetail.setText("CASH_COUNTER");
            } else {
                txtPaymentDetail.setText("4111222233334444");
            }
        });

        form.add(createFormField("Payment Simulation Method", paymentMethodCombo));
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(createFormField("Card Number / UPI ID / Reference", txtPaymentDetail));
        form.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton btnSubmit = createActionButton("Confirm Booking & Simulate Payment", PRIMARY_BLUE, Color.WHITE, PRIMARY_BLUE);
        btnSubmit.setPreferredSize(new Dimension(0, 40));

        btnSubmit.addActionListener(e -> {
            try {
                String name = txtName.getText().trim();
                String phone = txtPhone.getText().trim();
                String email = txtEmail.getText().trim();
                if (name.isBlank() || phone.isBlank()) {
                    JOptionPane.showMessageDialog(dialog, "Guest name and phone number are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Room selectedRoom = (Room) roomCombo.getSelectedItem();
                if (selectedRoom == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select an available room.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                LocalDate checkIn = LocalDate.parse(txtCheckIn.getText().trim());
                LocalDate checkOut = LocalDate.parse(txtCheckOut.getText().trim());
                int guests = (Integer) guestsCombo.getSelectedItem();

                Customer customer = hotel.findCustomerByPhone(phone);
                if (customer == null) {
                    customer = hotel.registerCustomer(name, phone, email);
                }

                Reservation reservation = hotel.prepareReservation(customer, selectedRoom, checkIn, checkOut, guests, List.of());

                PaymentMethod method;
                String methodChoice = (String) paymentMethodCombo.getSelectedItem();
                if ("UPI".equals(methodChoice)) {
                    method = new UpiPayment();
                } else if ("Cash".equals(methodChoice)) {
                    method = new CashPayment();
                } else {
                    method = new CardPayment();
                }

                Payment payment = hotel.completeBooking(reservation, method, txtPaymentDetail.getText().trim());
                if (payment.getStatus() == PaymentStatus.PAID) {
                    dialog.dispose();
                    refreshAllData();
                    showBookingSuccessDialog(reservation, payment);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Payment failed authorization. Booking could not be completed.", "Payment Declined", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Booking Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        form.add(btnSubmit);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        dialog.add(scroll, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JPanel createFormField(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(FONT_SUBTITLE);
        l.setForeground(TEXT_MAIN);
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void showBookingSuccessDialog(Reservation reservation, Payment payment) {
        String msg = String.format(Locale.US,
                "✓ RESERVATION CONFIRMED!\n\n"
                        + "Booking ID: %s\n"
                        + "Guest Name: %s\n"
                        + "Room: %d (%s)\n"
                        + "Stay: %s to %s (%d nights)\n"
                        + "Total Paid: ₹%,d\n"
                        + "Payment: %s (Txn: %s)\n\n"
                        + "Saved to File I/O (bookings.txt & payments.txt).",
                reservation.getBookingId(),
                reservation.getCustomer().getName(),
                reservation.getRoomNumber(),
                reservation.getRoomType(),
                reservation.getCheckIn(),
                reservation.getCheckOut(),
                reservation.getNights(),
                reservation.getTotalAmount(),
                payment.getMethodName(),
                payment.getTransactionId()
        );

        JOptionPane.showMessageDialog(this, msg, "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
    }

    private void promptCancelReservation() {
        String input = JOptionPane.showInputDialog(this,
                "Enter Booking ID to cancel (e.g. SS1001):",
                "Cancel Hotel Reservation",
                JOptionPane.QUESTION_MESSAGE);
        if (input == null || input.isBlank()) return;

        try {
            Reservation res = hotel.getCancellableReservation(input.trim());
            int opt = JOptionPane.showConfirmDialog(this,
                    "Confirm cancellation for:\n\n"
                            + "Booking ID : " + res.getBookingId() + "\n"
                            + "Guest      : " + res.getCustomer().getName() + "\n"
                            + "Room       : " + res.getRoomNumber() + " (" + res.getRoomType() + ")\n"
                            + "Refund     : ₹" + res.getTotalAmount() + "\n\n"
                            + "Proceed with cancellation?",
                    "Confirm Cancellation",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (opt == JOptionPane.YES_OPTION) {
                hotel.cancelReservation(res);
                refreshAllData();
                JOptionPane.showMessageDialog(this,
                        "Booking " + res.getBookingId() + " has been cancelled.\n"
                                + "Simulated refund of ₹" + res.getTotalAmount() + " processed.\n"
                                + "Room " + res.getRoomNumber() + " is now Available again.",
                        "Cancelled Successfully", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cancellation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showBookingDetailsDialog(Reservation res) {
        JDialog dialog = new JDialog(this, "Guest Folio - " + res.getBookingId(), true);
        dialog.setSize(480, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel("SMARTSTAY LUXURY FOLIO");
        title.setFont(FONT_HEADER);
        title.setForeground(PRIMARY_BLUE);

        JLabel sub = new JLabel("Official Hotel Reservation & Billing Summary");
        sub.setFont(FONT_SUBTITLE);
        sub.setForeground(TEXT_MUTED);

        panel.add(title);
        panel.add(sub);
        panel.add(Box.createRigidArea(new Dimension(0, 16)));

        panel.add(createFolioRow("Booking Reference", res.getBookingId()));
        panel.add(createFolioRow("Booking Status", res.getBookingStatus().name()));
        panel.add(createFolioRow("Payment Status", res.getPaymentStatus().name()));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFolioRow("Guest Name", res.getCustomer().getName()));
        panel.add(createFolioRow("Contact Phone", res.getCustomer().getPhone()));
        panel.add(createFolioRow("Email", res.getCustomer().getEmail()));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFolioRow("Room Number", "Room " + res.getRoomNumber()));
        panel.add(createFolioRow("Room Category", res.getRoomType()));
        panel.add(createFolioRow("Check-In Date", res.getCheckIn().toString()));
        panel.add(createFolioRow("Check-Out Date", res.getCheckOut().toString()));
        panel.add(createFolioRow("Total Stay", res.getNights() + " nights"));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFolioRow("Total Amount", String.format(Locale.US, "₹%,d", res.getTotalAmount())));

        panel.add(Box.createVerticalGlue());

        JButton btnClose = createActionButton("Close Folio", PRIMARY_BLUE, Color.WHITE, PRIMARY_BLUE);
        btnClose.addActionListener(e -> dialog.dispose());
        panel.add(btnClose);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JPanel createFolioRow(String label, String value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        JLabel l = new JLabel(label);
        l.setFont(FONT_SUBTITLE);
        l.setForeground(TEXT_MUTED);
        JLabel v = new JLabel(value);
        v.setFont(FONT_BOLD);
        v.setForeground(TEXT_MAIN);
        p.add(l, BorderLayout.WEST);
        p.add(v, BorderLayout.EAST);
        return p;
    }

    private void promptMaintenanceToggle() {
        String input = JOptionPane.showInputDialog(this, "Enter Room Number to toggle maintenance:", "Maintenance Mode", JOptionPane.QUESTION_MESSAGE);
        if (input == null || input.isBlank()) return;
        try {
            int roomNum = Integer.parseInt(input.trim());
            Room room = hotel.findRoom(roomNum);
            if (room == null) {
                JOptionPane.showMessageDialog(this, "Room " + roomNum + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean setMaint = (room.getStatus() != RoomStatus.MAINTENANCE);
            hotel.setRoomMaintenance(roomNum, setMaint);
            refreshAllData();
            JOptionPane.showMessageDialog(this, "Room " + roomNum + " status updated to: " + (setMaint ? "MAINTENANCE" : "AVAILABLE"));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid room number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // 13. DATA SYNCHRONIZATION
    // =========================================================================

    public void refreshAllData() {
        HotelManager.Statistics stats = hotel.getStatistics();

        // 1. Dashboard KPI Cards
        lblTotalRooms.setText(String.valueOf(stats.totalRooms()));
        lblAvailableRooms.setText(String.valueOf(stats.availableRooms()));
        lblOccupiedRooms.setText(String.valueOf(stats.bookedRooms()));
        lblTotalRevenue.setText(String.format(Locale.US, "₹%,d.00", stats.revenue()));

        // 2. Room Grid
        refreshRoomGrid();

        // 3. Recent Bookings Table
        if (recentBookingsModel != null) {
            recentBookingsModel.setRowCount(0);
            List<Reservation> resList = hotel.getAllReservations();
            int limit = Math.min(resList.size(), 8);
            for (int i = resList.size() - 1; i >= resList.size() - limit; i--) {
                Reservation r = resList.get(i);
                recentBookingsModel.addRow(new Object[]{
                        r.getBookingId(),
                        r.getCustomer().getName(),
                        r.getRoomNumber() + " (" + r.getRoomType() + ")",
                        r.getCheckIn() + " → " + r.getCheckOut(),
                        String.format(Locale.US, "₹%,d", r.getTotalAmount()),
                        r.getBookingStatus().name()
                });
            }
        }

        // 4. All Rooms Table
        if (allRoomsModel != null) {
            allRoomsModel.setRowCount(0);
            for (Room r : hotel.getAllRooms()) {
                allRoomsModel.addRow(new Object[]{
                        r.getRoomNumber(),
                        r.getFloorNumber(),
                        r.getRoomType(),
                        String.format(Locale.US, "₹%,d", r.getPricePerNight()),
                        r.getMaxGuests() + " Guests",
                        r.getStatus().name(),
                        r.getAmenities()
                });
            }
        }

        // 5. All Reservations Table
        if (allReservationsModel != null) {
            allReservationsModel.setRowCount(0);
            for (Reservation r : hotel.getAllReservations()) {
                allReservationsModel.addRow(new Object[]{
                        r.getBookingId(),
                        r.getCustomer().getName(),
                        r.getCustomer().getPhone(),
                        r.getRoomNumber(),
                        r.getRoomType(),
                        r.getCheckIn(),
                        r.getCheckOut(),
                        String.format(Locale.US, "₹%,d", r.getTotalAmount()),
                        r.getBookingStatus().name()
                });
            }
        }

        // 6. All Customers Table
        if (allCustomersModel != null) {
            allCustomersModel.setRowCount(0);
            for (Customer c : hotel.getAllCustomers()) {
                allCustomersModel.addRow(new Object[]{
                        c.getCustomerId(),
                        c.getName(),
                        c.getPhone(),
                        c.getEmail(),
                        hotel.getLoyaltyTier(c).name()
                });
            }
        }

        // 7. All Payments Table
        if (allPaymentsModel != null) {
            allPaymentsModel.setRowCount(0);
            for (Payment p : hotel.getAllPayments()) {
                allPaymentsModel.addRow(new Object[]{
                        p.getTransactionId(),
                        p.getBookingId(),
                        String.format(Locale.US, "₹%,d", p.getAmount()),
                        p.getMethodName(),
                        p.getMethodDetail(),
                        p.getStatus().name(),
                        p.getTimestamp()
                });
            }
        }

        // 8. Reports KPI Labels
        if (lblReportOccupancy != null) {
            int total = stats.totalRooms();
            int occ = total > 0 ? (stats.bookedRooms() * 100 / total) : 0;
            lblReportOccupancy.setText(occ + "%");
            lblReportRevenue.setText(String.format(Locale.US, "₹%,d.00", stats.revenue()));
            lblReportBookings.setText(String.valueOf(stats.confirmed()));
            lblReportPopular.setText(stats.mostPopularType());
        }
    }

    private JButton createActionButton(String text, Color bg, Color fg, Color border) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.darker() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                if (border != null) {
                    g2.setColor(border);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BOLD);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 36));
        return btn;
    }

    private JTable styleTable(JTable table) {
        table.setRowHeight(36);
        table.setFont(FONT_BODY);
        table.setForeground(TEXT_MAIN);
        table.setSelectionBackground(new Color(238, 242, 255));
        table.setSelectionForeground(PRIMARY_BLUE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(TEXT_MUTED);
        header.setPreferredSize(new Dimension(0, 38));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, CARD_BORDER));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        centerRenderer.setBorder(new EmptyBorder(0, 12, 0, 12));
        table.setDefaultRenderer(Object.class, centerRenderer);

        return table;
    }
}
