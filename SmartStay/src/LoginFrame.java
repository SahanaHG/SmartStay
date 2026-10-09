import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Task 4: Hotel Reservation System - CodeAlpha Java Internship.
 *
 * User Authentication & Staff Login Window.
 * Supported Demo Credentials:
 *   - admin / admin123        (Administrator - General Manager)
 *   - desk_sarah / sarah123    (Front Desk Officer)
 *   - desk_rahul / rahul123    (Front Desk Officer)
 *   - manager / manager123     (Property Manager)
 */
@SuppressWarnings("serial")
public final class LoginFrame extends JFrame {

    private static final Color BG_DARK = new Color(11, 19, 43);         // Midnight Navy
    private static final Color BG_LIGHT = new Color(248, 250, 252);     // Slate 50
    private static final Color CARD_BG = Color.WHITE;
    private static final Color CARD_BORDER = new Color(226, 232, 240);  // Slate 200
    private static final Color TEXT_MAIN = new Color(15, 23, 42);       // Slate 900
    private static final Color TEXT_MUTED = new Color(100, 116, 139);   // Slate 500
    private static final Color PRIMARY_BLUE = new Color(37, 99, 235);   // Royal Blue
    private static final Color PRIMARY_HOVER = new Color(29, 78, 216);
    private static final Color DANGER = new Color(239, 68, 68);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 12);

    public record AuthUser(String username, String fullName, String role, String accessLevel) {
    }

    private static final Map<String, String> PASSWORDS = new HashMap<>();
    private static final Map<String, AuthUser> USERS = new HashMap<>();

    static {
        PASSWORDS.put("admin", "admin123");
        USERS.put("admin", new AuthUser("admin", "Administrator", "ADMIN", "Full PMS Access"));

        PASSWORDS.put("desk_sarah", "sarah123");
        USERS.put("desk_sarah", new AuthUser("desk_sarah", "Sarah Jenkins", "FRONT DESK", "Bookings & Check-in"));

        PASSWORDS.put("desk_rahul", "rahul123");
        USERS.put("desk_rahul", new AuthUser("desk_rahul", "Rahul Sharma", "FRONT DESK", "Bookings & Check-in"));

        PASSWORDS.put("manager", "manager123");
        USERS.put("manager", new AuthUser("manager", "Michael Chang", "MANAGER", "Reports & Overrides"));
    }

    private final HotelManager hotel;
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final JComboBox<String> comboRole;
    private final JLabel lblError;

    public LoginFrame(HotelManager hotel) {
        super("SmartStay | Front Desk Security Login");
        this.hotel = hotel;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 560);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_LIGHT);
        root.setBorder(new EmptyBorder(30, 36, 30, 36));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                new EmptyBorder(24, 28, 24, 28)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 0, 3, 0);
        gbc.weightx = 1.0;

        // Brand Logo
        JLabel logoSquare = new JLabel("SS", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_BLUE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoSquare.setForeground(Color.WHITE);
        logoSquare.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoSquare.setPreferredSize(new Dimension(44, 44));
        logoSquare.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel logoWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoWrapper.setOpaque(false);
        logoWrapper.add(logoSquare);
        card.add(logoWrapper, gbc);

        // Titles
        gbc.gridy++;
        JLabel lblTitle = new JLabel("SmartStay PMS", SwingConstants.CENTER);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(TEXT_MAIN);
        card.add(lblTitle, gbc);

        gbc.gridy++;
        JLabel lblSub = new JLabel("Front Desk & Staff Authentication", SwingConstants.CENTER);
        lblSub.setFont(FONT_SUBTITLE);
        lblSub.setForeground(TEXT_MUTED);
        card.add(lblSub, gbc);

        // Error Banner
        gbc.gridy++;
        gbc.insets = new Insets(8, 0, 4, 0);
        lblError = new JLabel(" ", SwingConstants.CENTER);
        lblError.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblError.setForeground(DANGER);
        card.add(lblError, gbc);

        // Fields
        gbc.insets = new Insets(2, 0, 1, 0);
        txtUsername = new JTextField("admin", 20);
        txtUsername.setFont(FONT_BODY);
        txtUsername.setPreferredSize(new Dimension(280, 34));

        txtPassword = new JPasswordField("admin123", 20);
        txtPassword.setFont(FONT_BODY);
        txtPassword.setPreferredSize(new Dimension(280, 34));

        comboRole = new JComboBox<>(new String[]{"Administrator (General Manager)", "Front Desk Officer", "Property Manager"});
        comboRole.setFont(FONT_BODY);
        comboRole.setPreferredSize(new Dimension(280, 34));

        gbc.gridy++;
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(FONT_SUBTITLE);
        lblUser.setForeground(TEXT_MAIN);
        card.add(lblUser, gbc);

        gbc.gridy++;
        card.add(txtUsername, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(8, 0, 1, 0);
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(FONT_SUBTITLE);
        lblPass.setForeground(TEXT_MAIN);
        card.add(lblPass, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(2, 0, 1, 0);
        card.add(txtPassword, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(8, 0, 1, 0);
        JLabel lblRole = new JLabel("Role");
        lblRole.setFont(FONT_SUBTITLE);
        lblRole.setForeground(TEXT_MAIN);
        card.add(lblRole, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(2, 0, 1, 0);
        card.add(comboRole, gbc);

        // Sign In Button
        gbc.gridy++;
        gbc.insets = new Insets(16, 0, 8, 0);
        JButton btnLogin = new JButton("Sign In →") {
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
        btnLogin.setFont(FONT_BOLD);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setPreferredSize(new Dimension(280, 38));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogin.addActionListener(e -> attemptLogin());
        txtPassword.addActionListener(e -> attemptLogin());
        txtUsername.addActionListener(e -> attemptLogin());
        card.add(btnLogin, gbc);

        // Demo Hint
        gbc.gridy++;
        gbc.insets = new Insets(8, 0, 0, 0);
        JLabel lblHint = new JLabel("<html><center><font color='#64748B'>Default Demo Login:<br>Username: <b>admin</b> &nbsp;|&nbsp; Password: <b>admin123</b></font></center></html>", SwingConstants.CENTER);
        lblHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        card.add(lblHint, gbc);

        root.add(card, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void attemptLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please enter both username and password.");
            return;
        }

        String expectedPassword = PASSWORDS.get(username.toLowerCase());
        if (expectedPassword != null && expectedPassword.equals(password)) {
            lblError.setText(" ");
            AuthUser user = USERS.get(username.toLowerCase());
            SmartStayDashboard dashboard = new SmartStayDashboard(hotel, user);
            dashboard.setVisible(true);
            dispose();
        } else {
            lblError.setText("Invalid credentials. Try admin / admin123");
            txtPassword.setText("");
            txtPassword.requestFocusInWindow();
        }
    }
}
