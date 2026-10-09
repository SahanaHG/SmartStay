import java.awt.GraphicsEnvironment;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Task 4: Hotel Reservation System - CodeAlpha Java Internship.
 *
 * Supports:
 * - Executive Property Management Dashboard (Desktop GUI) by default
 * - Terminal Console UI (via --console or headless fallback)
 */
public class Main {

    public static void main(String[] args) {
        FileManager fileManager = new FileManager("data");
        HotelManager hotelManager = new HotelManager(fileManager);

        boolean runConsole = false;
        for (String arg : args) {
            if ("--console".equalsIgnoreCase(arg) || "-c".equalsIgnoreCase(arg)) {
                runConsole = true;
                break;
            }
        }

        System.out.println("=================================================");
        System.out.println("   SMARTSTAY | HOTEL PROPERTY MANAGEMENT SYSTEM  ");
        System.out.println("=================================================");
        System.out.println("  * System Status     : Online");
        System.out.println("  * Total Rooms       : " + hotelManager.getAllRooms().size() + " Loaded");
        System.out.println("  * Reservations      : " + hotelManager.getAllReservations().size() + " Active");

        if (runConsole || GraphicsEnvironment.isHeadless()) {
            System.out.println("  * Launching         : Terminal Interactive Console UI");
            System.out.println("=================================================\n");
            ConsoleUI consoleUI = new ConsoleUI(hotelManager);
            consoleUI.start();
        } else {
            System.out.println("  * Launching         : Front Welcome Dashboard & Staff Login");
            System.out.println("  * Tip               : Pass '--console' for Terminal Menu");
            System.out.println("=================================================\n");

            SwingUtilities.invokeLater(() -> {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {
                }

                // 1. Welcome Page -> 2. Staff Login Portal -> 3. Executive PMS Dashboard
                WelcomeSplash welcome = new WelcomeSplash(hotelManager);
                welcome.setVisible(true);
            });
        }
    }
}
