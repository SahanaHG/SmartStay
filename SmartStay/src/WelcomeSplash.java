import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Task 4: Hotel Reservation System - CodeAlpha Java Internship.
 *
 * Welcome Dashboard & Intro Window matching the reference aesthetic:
 * "01 — DASHBOARD & OPERATIONS"
 * "Hotel Management System • Java Swing • CodeAlpha"
 */
@SuppressWarnings("serial")
public final class WelcomeSplash extends JFrame {

    private static final Color BG_DARK = new Color(11, 19, 43);        // Deep Midnight Navy
    private static final Color CARD_BG = new Color(20, 32, 60);        // Dark Slate Blue
    private static final Color ACCENT_BLUE = new Color(37, 99, 235);    // Royal Blue
    private static final Color ACCENT_HOVER = new Color(29, 78, 216);
    private static final Color TEXT_MUTED = new Color(148, 163, 184);   // Slate 400
    private static final Color SUCCESS = new Color(16, 185, 129);       // Emerald Green

    private final HotelManager hotel;
    private final JProgressBar progressBar;
    private final JLabel lblStatus;
    private Timer autoTimer;

    public WelcomeSplash(HotelManager hotel) {
        super("SmartStay | Welcome & Operations");
        this.hotel = hotel;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 480);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 16)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_DARK);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Subtle inner border
                g2.setColor(new Color(30, 41, 59));
                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        root.setBorder(new EmptyBorder(40, 48, 32, 48));

        // Center Content Group
        JPanel centerGroup = new JPanel();
        centerGroup.setLayout(new BoxLayout(centerGroup, BoxLayout.Y_AXIS));
        centerGroup.setOpaque(false);

        // Logo Shield Icon
        JLabel logoBadge = new JLabel("SS", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT_BLUE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoBadge.setForeground(Color.WHITE);
        logoBadge.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoBadge.setPreferredSize(new Dimension(54, 54));
        logoBadge.setMaximumSize(new Dimension(54, 54));
        logoBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("01 — DASHBOARD & OPERATIONS");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("SmartStay Luxury Hotel & Residences • Java Swing PMS");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(96, 165, 250)); // Soft sky blue
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblInternship = new JLabel("CodeAlpha Java Programming Internship — Task 4 (Hotel Reservation System)");
        lblInternship.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblInternship.setForeground(TEXT_MUTED);
        lblInternship.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 3 Key Stats Badges Row
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        statsRow.setOpaque(false);
        statsRow.add(createBadge("🏨  20 Rooms Loaded", new Color(30, 58, 138), new Color(147, 197, 253)));
        statsRow.add(createBadge("●  Status: ONLINE", new Color(6, 78, 59), new Color(110, 231, 183)));
        statsRow.add(createBadge("💾  File I/O Active", new Color(67, 56, 202), new Color(199, 210, 254)));
        statsRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Enter Login Button
        JButton btnProceed = new JButton("Enter Front Desk Login →") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? ACCENT_HOVER : ACCENT_BLUE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnProceed.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnProceed.setForeground(Color.WHITE);
        btnProceed.setFocusPainted(false);
        btnProceed.setBorderPainted(false);
        btnProceed.setContentAreaFilled(false);
        btnProceed.setPreferredSize(new Dimension(240, 42));
        btnProceed.setMaximumSize(new Dimension(240, 42));
        btnProceed.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnProceed.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnProceed.addActionListener(e -> proceedToLogin());

        centerGroup.add(logoBadge);
        centerGroup.add(Box.createRigidArea(new Dimension(0, 16)));
        centerGroup.add(lblTitle);
        centerGroup.add(Box.createRigidArea(new Dimension(0, 8)));
        centerGroup.add(lblSubtitle);
        centerGroup.add(Box.createRigidArea(new Dimension(0, 4)));
        centerGroup.add(lblInternship);
        centerGroup.add(Box.createRigidArea(new Dimension(0, 18)));
        centerGroup.add(statsRow);
        centerGroup.add(Box.createRigidArea(new Dimension(0, 22)));
        centerGroup.add(btnProceed);

        // Bottom Progress Group
        JPanel bottomGroup = new JPanel();
        bottomGroup.setLayout(new BoxLayout(bottomGroup, BoxLayout.Y_AXIS));
        bottomGroup.setOpaque(false);

        lblStatus = new JLabel("System online. Auto-launching login portal...", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(TEXT_MUTED);
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(360, 6));
        progressBar.setMaximumSize(new Dimension(360, 6));
        progressBar.setForeground(ACCENT_BLUE);
        progressBar.setBackground(new Color(30, 41, 59));
        progressBar.setBorderPainted(false);
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        bottomGroup.add(lblStatus);
        bottomGroup.add(Box.createRigidArea(new Dimension(0, 8)));
        bottomGroup.add(progressBar);

        root.add(centerGroup, BorderLayout.CENTER);
        root.add(bottomGroup, BorderLayout.SOUTH);

        setContentPane(root);

        // Auto timer transition (~1.8 seconds)
        autoTimer = new Timer(20, null);
        final int[] progress = {0};
        autoTimer.addActionListener(e -> {
            progress[0] += 2;
            progressBar.setValue(progress[0]);

            if (progress[0] >= 100) {
                autoTimer.stop();
                proceedToLogin();
            }
        });
        autoTimer.start();
    }

    private JPanel createBadge(String text, Color bg, Color fg) {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setOpaque(false);
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(fg);
        badge.add(l);
        return badge;
    }

    private void proceedToLogin() {
        if (autoTimer != null && autoTimer.isRunning()) {
            autoTimer.stop();
        }
        LoginFrame login = new LoginFrame(hotel);
        login.setVisible(true);
        dispose();
    }
}
