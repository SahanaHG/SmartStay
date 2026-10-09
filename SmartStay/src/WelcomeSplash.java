import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Task 4: Hotel Reservation System - CodeAlpha Java Internship.
 *
 * Welcome Splash Screen matching the video intro aesthetic:
 * "01 — DASHBOARD & OPERATIONS"
 * "Hotel Management System • Java Swing • CodeAlpha"
 */
@SuppressWarnings("serial")
public final class WelcomeSplash extends JWindow {

    private static final Color BG_DARK = new Color(11, 19, 43);        // Deep Midnight Navy
    private static final Color ACCENT_BLUE = new Color(37, 99, 235);    // Royal Blue
    private static final Color TEXT_MUTED = new Color(148, 163, 184);   // Slate 400

    private final JProgressBar progressBar;
    private final JLabel lblStatus;

    public WelcomeSplash(Runnable onFinish) {
        setSize(680, 420);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(0, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_DARK);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Subtle border
                g2.setColor(new Color(30, 41, 59));
                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setBorder(new EmptyBorder(60, 40, 50, 40));

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
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Hotel Management System • Java Swing");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(96, 165, 250)); // Soft sky blue
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblInternship = new JLabel("SmartStay | CodeAlpha Java Programming Internship — Task 4");
        lblInternship.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblInternship.setForeground(TEXT_MUTED);
        lblInternship.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerGroup.add(logoBadge);
        centerGroup.add(Box.createRigidArea(new Dimension(0, 24)));
        centerGroup.add(lblTitle);
        centerGroup.add(Box.createRigidArea(new Dimension(0, 10)));
        centerGroup.add(lblSubtitle);
        centerGroup.add(Box.createRigidArea(new Dimension(0, 6)));
        centerGroup.add(lblInternship);

        // Bottom Progress Group
        JPanel bottomGroup = new JPanel();
        bottomGroup.setLayout(new BoxLayout(bottomGroup, BoxLayout.Y_AXIS));
        bottomGroup.setOpaque(false);

        lblStatus = new JLabel("Initializing system...", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(TEXT_MUTED);
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(340, 6));
        progressBar.setMaximumSize(new Dimension(340, 6));
        progressBar.setForeground(ACCENT_BLUE);
        progressBar.setBackground(new Color(30, 41, 59));
        progressBar.setBorderPainted(false);
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        bottomGroup.add(lblStatus);
        bottomGroup.add(Box.createRigidArea(new Dimension(0, 8)));
        bottomGroup.add(progressBar);

        panel.add(centerGroup, BorderLayout.CENTER);
        panel.add(bottomGroup, BorderLayout.SOUTH);

        setContentPane(panel);

        // Click to skip splash immediately
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    if (onFinish != null) {
                        onFinish.run();
                    }
                } finally {
                    dispose();
                }
            }
        });

        // Animated Loader Timer (~1.2 seconds)
        Timer timer = new Timer(15, null);
        final int[] progress = {0};
        timer.addActionListener(e -> {
            progress[0] += 2;
            progressBar.setValue(progress[0]);

            if (progress[0] < 30) {
                lblStatus.setText("Synchronizing 20 room inventory records...");
            } else if (progress[0] < 70) {
                lblStatus.setText("Mounting File I/O persistence engine...");
            } else if (progress[0] < 95) {
                lblStatus.setText("Starting front desk security authentication...");
            } else {
                lblStatus.setText("Welcome to SmartStay PMS");
            }

            if (progress[0] >= 100) {
                timer.stop();
                try {
                    if (onFinish != null) {
                        onFinish.run();
                    }
                } finally {
                    dispose();
                }
            }
        });
        timer.start();
    }

    public static void showAndProceed(Runnable onFinish) {
        SwingUtilities.invokeLater(() -> {
            WelcomeSplash splash = new WelcomeSplash(onFinish);
            splash.setVisible(true);
        });
    }
}
