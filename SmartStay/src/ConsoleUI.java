import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Predicate;

/**
 * Everything the user sees and types. Menus, input checking and pretty output live here.
 * Business rules live in HotelManager; this class only asks it to do things.
 */
public class ConsoleUI {

    private static final int WIDTH = 48;
    private static final int MAX_CARDS_SHOWN = 5;
    private static final boolean COLOR = System.getenv("NO_COLOR") == null;
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String DIM = "\u001B[2m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final HotelManager hotel;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleUI(HotelManager hotel) {
        this.hotel = hotel;
    }

    // ================================================================ MAIN LOOP

    public void start() {
        for (String message : hotel.getStartupMessages()) {
            System.out.println(style("i  " + message, YELLOW));
        }
        printWelcomeDashboard();
        try {
            boolean running = true;
            while (running) {
                printMainMenu();
                int choice = readInt("Enter your choice: ", 1, 10);
                running = handleChoice(choice);
            }
        } catch (NoSuchElementException e) {
            System.out.println("\nInput closed. Goodbye!");
        }
    }

    private boolean handleChoice(int choice) {
        try {
            switch (choice) {
                case 1 -> viewAllRooms();
                case 2 -> searchRooms();
                case 3 -> makeReservation();
                case 4 -> viewBookingDetails();
                case 5 -> cancelReservation();
                case 6 -> viewAllReservations();
                case 7 -> viewPaymentDetails();
                case 8 -> showStatistics();
                case 9 -> {
                    printGoodbye();
                    return false;
                }
                case 10 -> {
                    adminMenu();
                    return true;
                }
                default -> printError("Invalid menu choice.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            printError(e.getMessage());
        } catch (UncheckedIOException e) {
            printError("File problem: " + e.getMessage());
        }
        pause();
        return true;
    }

    private void printWelcomeDashboard() {
        var stats = hotel.getStatistics();
        System.out.println();
        System.out.println(style("╔══════════════════════════════════════════════════════╗", CYAN));
        System.out.println(style("║        ✦ SMARTSTAY HOTEL WELCOME DASHBOARD ✦         ║", BOLD + CYAN));
        System.out.println(style("║            5-Star Luxury Reservation System          ║", CYAN));
        System.out.println(style("╠══════════════════════════════════════════════════════╣", CYAN));
        System.out.println(style("║  Date: " + LocalDate.now() + "  |  Operational Status: ONLINE      ║", YELLOW));
        System.out.println(style("║  Total Rooms: " + stats.totalRooms() + "  |  Available: " + stats.availableRooms() + "  |  Booked: " + stats.bookedRooms() + "        ║", GREEN));
        System.out.println(style("║  Live Revenue: ₹" + stats.revenue() + "  |  Confirmed Bookings: " + stats.confirmed() + "     ║", GREEN));
        System.out.println(style("╚══════════════════════════════════════════════════════╝", CYAN));
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println(style("═".repeat(WIDTH), CYAN));
        System.out.println(style(center("SMARTSTAY"), BOLD + CYAN));
        System.out.println(center("HOTEL RESERVATION SYSTEM"));
        System.out.println(style("═".repeat(WIDTH), CYAN));
        System.out.println(" 1. View All Rooms");
        System.out.println(" 2. Search Available Rooms");
        System.out.println(" 3. Make Reservation");
        System.out.println(" 4. View Booking Details");
        System.out.println(" 5. Cancel Reservation");
        System.out.println(" 6. View All Reservations");
        System.out.println(" 7. Payment Details");
        System.out.println(" 8. Hotel Statistics");
        System.out.println(" 9. Exit");
        System.out.println(style("─".repeat(WIDTH), DIM));
        System.out.println("10. Admin: Room Management");
        System.out.println(style("═".repeat(WIDTH), CYAN));
    }

    private void printGoodbye() {
        System.out.println();
        System.out.println(style("Thank you for using SmartStay. All data is saved. Goodbye!", GREEN));
    }

    // ================================================================ 1. ROOMS

    private void viewAllRooms() {
        printHeader("ROOM CATEGORIES");
        for (String type : Room.TYPES) {
            Room sample = null;
            int count = 0;
            for (Room room : hotel.getAllRooms()) {
                if (room.getRoomType().equals(type)) {
                    count++;
                    if (sample == null) {
                        sample = room;
                    }
                }
            }
            if (sample != null) {
                System.out.printf("%s%n   %s  |  up to %d guests  |  %d rooms%n   %s%n",
                        style(type.toUpperCase(), BOLD), rupees(sample.getPricePerNight()) + "/night",
                        sample.getMaxGuests(), count, sample.getAmenitiesText());
            }
        }
        printHeader("ALL ROOMS");
        printRoomTable(hotel.getAllRooms());
    }

    private void printRoomTable(List<Room> rooms) {
        if (rooms.isEmpty()) {
            System.out.println("No rooms to show.");
            return;
        }
        System.out.println(style(String.format("%-6s %-6s %-10s %-12s %-7s %-18s %s",
                "ROOM", "FLOOR", "TYPE", "PRICE/NIGHT", "GUESTS", "STATUS", "AMENITIES"), DIM));
        for (Room room : rooms) {
            String status = String.format("%-18s", room.getStatus().getLabel());
            System.out.printf("%-6d %-6d %-10s %-12s %-7d %s %s%n",
                    room.getRoomNumber(), room.getFloorNumber(), room.getRoomType(),
                    rupees(room.getPricePerNight()), room.getMaxGuests(),
                    colorStatus(status, room.getStatus()), room.getAmenitiesText());
        }
        System.out.println(style(rooms.size() + " room(s)", DIM));
    }

    // ================================================================ 2. SEARCH

    private void searchRooms() {
        printHeader("SEARCH ROOMS");
        String type = readRoomTypeChoice(true);
        int guests = readInt("Enter number of guests (1-20): ", 1, 20);
        int budget = readInt("Enter maximum budget per night (0 = no limit): ", 0, 1_000_000);

        LocalDate checkIn = null;
        LocalDate checkOut = null;
        System.out.println("Check availability for specific dates?");
        System.out.println(" 1. Yes");
        System.out.println(" 2. No (show rooms available now)");
        if (readInt("Enter choice (1-2): ", 1, 2) == 1) {
            checkIn = readCheckIn();
            checkOut = readCheckOut(checkIn);
        }
        printSearchResults(hotel.searchRooms(type, guests, budget, checkIn, checkOut), checkIn != null);
    }

    private String readRoomTypeChoice(boolean allowAny) {
        System.out.println("Room type:");
        int first = 1;
        if (allowAny) {
            System.out.println(" 1. Any");
            first = 2;
        }
        for (int i = 0; i < Room.TYPES.size(); i++) {
            System.out.println(" " + (first + i) + ". " + Room.TYPES.get(i));
        }
        int lastOption = first + Room.TYPES.size() - 1;
        int choice = readInt("Enter room type (1-" + lastOption + "): ", 1, lastOption);
        if (allowAny && choice == 1) {
            return null;
        }
        return Room.TYPES.get(choice - first);
    }

    private void printSearchResults(List<HotelManager.RoomMatch> results, boolean datesChecked) {
        if (results.isEmpty()) {
            System.out.println(style("\nNo rooms match your search. Try a higher budget, fewer guests or other dates.", YELLOW));
            return;
        }
        printHeader("AVAILABLE ROOMS (" + results.size() + " found)");
        int shown = Math.min(results.size(), MAX_CARDS_SHOWN);
        for (int i = 0; i < shown; i++) {
            HotelManager.RoomMatch match = results.get(i);
            printRoomCard(match.room(), match.matchPercent(), i == 0, datesChecked);
        }
        System.out.println(style("─".repeat(WIDTH), DIM));
        if (results.size() > shown) {
            List<String> others = new ArrayList<>();
            for (int i = shown; i < results.size(); i++) {
                others.add(String.valueOf(results.get(i).room().getRoomNumber()));
            }
            System.out.println("Also available: " + String.join(", ", others));
        }
    }

    /** matchPercent < 0 hides the match score; datesChecked shows "Available for your dates". */
    private void printRoomCard(Room room, int matchPercent, boolean best, boolean datesChecked) {
        System.out.println(style("─".repeat(WIDTH), DIM));
        String tag = best ? style("  ★ BEST MATCH", BOLD + GREEN) : "";
        System.out.println("Room: " + style(String.valueOf(room.getRoomNumber()), BOLD) + tag);
        System.out.println("Type: " + room.getRoomType());
        System.out.println("Price: " + rupees(room.getPricePerNight()) + "/night");
        System.out.println("Capacity: " + room.getMaxGuests() + " guests");
        System.out.println("Amenities: " + room.getAmenitiesText());
        String status = datesChecked ? "Available for your dates" : room.getStatus().getLabel();
        System.out.println("Status: " + (datesChecked ? style(status, GREEN) : colorStatus(status, room.getStatus())));
        if (matchPercent >= 0) {
            System.out.println("Match score: " + matchPercent + "%");
        }
    }

    // ================================================================ 3. RESERVE

    private void makeReservation() {
        printHeader("MAKE A RESERVATION");
        Customer customer = readCustomer();

        LocalDate checkIn = readCheckIn();
        LocalDate checkOut = readCheckOut(checkIn);
        int nights = PricingEngine.countNights(checkIn, checkOut);
        System.out.println("Number of nights: " + style(String.valueOf(nights), BOLD));

        int guests = readInt("Number of guests (1-20): ", 1, 20);
        String type = readRoomTypeChoice(true);
        int budget = readInt("Maximum budget per night (0 = no limit): ", 0, 1_000_000);

        List<HotelManager.RoomMatch> options = hotel.searchRooms(type, guests, budget, checkIn, checkOut);
        printSearchResults(options, true);
        if (options.isEmpty()) {
            return;
        }

        Room room = pickRoom(options);
        if (room == null) {
            System.out.println("Reservation cancelled.");
            return;
        }
        List<Service> services = readServices();
        Reservation reservation = hotel.prepareReservation(customer, room, checkIn, checkOut, guests, services);

        printPriceSummary(reservation);
        System.out.println("Proceed to payment?");
        System.out.println(" 1. Yes");
        System.out.println(" 2. No");
        if (readInt("Enter choice (1-2): ", 1, 2) == 2) {
            System.out.println("Reservation cancelled. Nothing was booked.");
            return;
        }

        Payment payment = processPayment(reservation);
        if (payment == null) {
            System.out.println(style("Booking cancelled. Nothing was booked.", YELLOW));
            return;
        }
        printConfirmation(reservation, payment);
    }

    private Customer readCustomer() {
        String phone = readValid("Phone number (10 digits): ", Customer::isValidPhone,
                "Invalid phone number. Please enter exactly 10 digits.");
        Customer existing = hotel.findCustomerByPhone(phone);
        if (existing != null) {
            LoyaltyTier tier = hotel.getLoyaltyTier(existing);
            System.out.println(style("Welcome back, " + existing.getName() + "!", GREEN)
                    + "  Loyalty tier: " + tier.getLabel()
                    + (tier.getDiscountPercent() > 0 ? " (" + tier.getDiscountPercent() + "% off room cost)" : ""));
            return existing;
        }
        String name = readValid("Customer name: ", Customer::isValidName,
                "Invalid name. Use 2-40 letters (spaces . ' - allowed). It cannot be empty.");
        String email = readValid("Email address: ", Customer::isValidEmail,
                "Invalid email. Example: name@example.com");
        return hotel.registerCustomer(name, phone, email);
    }

    private Room pickRoom(List<HotelManager.RoomMatch> options) {
        while (true) {
            int number = readInt("\nEnter the room number to book (0 to cancel): ", 0, 99_999);
            if (number == 0) {
                return null;
            }
            for (HotelManager.RoomMatch match : options) {
                if (match.room().getRoomNumber() == number) {
                    return match.room();
                }
            }
            if (hotel.findRoom(number) == null) {
                printError("Room " + number + " does not exist.");
            } else {
                printError("Room " + number + " is not available for these dates and preferences. Please pick an available room.");
            }
        }
    }

    private List<Service> readServices() {
        printHeader("OPTIONAL SERVICES");
        List<Service> chosen = new ArrayList<>();
        for (Service service : Service.values()) {
            String prompt = String.format("Add %s (%s %s)? 1 = Yes, 2 = No: ",
                    service.getDisplayName(), rupees(service.getPrice()), service.getChargeLabel());
            if (readInt(prompt, 1, 2) == 1) {
                chosen.add(service);
            }
        }
        return chosen;
    }

    private Payment processPayment(Reservation reservation) {
        while (true) {
            printHeader("PAYMENT");
            System.out.println("Amount: " + style(rupees(reservation.getTotalAmount()), BOLD));
            System.out.println("Select payment method:");
            System.out.println(" 1. UPI");
            System.out.println(" 2. Card");
            System.out.println(" 3. Cash");
            int choice = readInt("Enter choice (1-3): ", 1, 3);
            PaymentMethod method = switch (choice) {
                case 1 -> new UpiPayment();
                case 2 -> new CardPayment();
                default -> new CashPayment();
            };

            String detail = "";
            if (method.getDetailPrompt() != null) {
                detail = readValid(method.getDetailPrompt() + ": ", method::isValidDetail,
                        "Invalid " + method.getName() + " details. Please try again.");
            }

            System.out.println("\nPayment processing...");
            sleep(700);
            Payment payment = hotel.completeBooking(reservation, method, detail);

            if (payment.getStatus() == PaymentStatus.PAID) {
                System.out.println(style("Payment successful!", GREEN));
                System.out.println("\nTransaction ID: " + payment.getTransactionId());
                System.out.println("Payment Status: " + style("PAID", GREEN));
                return payment;
            }
            printError("Payment declined by the bank (" + payment.getTransactionId() + ").");
            System.out.println(" 1. Try another payment method");
            System.out.println(" 2. Cancel booking");
            if (readInt("Enter choice (1-2): ", 1, 2) == 2) {
                return null;
            }
        }
    }

    // ============================================================ PRINT BOOKINGS

    private void printPriceSummary(Reservation r) {
        PriceBreakdown price = r.getPriceBreakdown();
        int weekendNights = PricingEngine.countWeekendNights(r.getCheckIn(), r.getCheckOut());
        printHeader("PRICE SUMMARY");
        moneyLine("Room Cost (" + r.getNights() + (r.getNights() == 1 ? " night)" : " nights)"), price.getBaseRoomCost(), "");
        if (price.getWeekendSurcharge() > 0) {
            moneyLine("Weekend Surcharge (" + weekendNights + ")", price.getWeekendSurcharge(), "+ ");
        }
        if (price.getLongStayDiscount() > 0) {
            moneyLine("Long-Stay Discount", price.getLongStayDiscount(), "- ");
        }
        if (price.getLoyaltyDiscount() > 0) {
            moneyLine("Loyalty Discount", price.getLoyaltyDiscount(), "- ");
        }
        for (Map.Entry<Service, Integer> entry : price.getServiceCosts().entrySet()) {
            moneyLine(entry.getKey().getDisplayName(), entry.getValue(), "");
        }
        System.out.println(style("─".repeat(WIDTH), DIM));
        System.out.println(style(String.format("%-22s: %11s", "TOTAL", rupees(price.getTotal())), BOLD));
        System.out.println(style("─".repeat(WIDTH), DIM));
    }

    private void printConfirmation(Reservation r, Payment payment) {
        PriceBreakdown price = r.getPriceBreakdown();
        System.out.println();
        System.out.println(style("═".repeat(WIDTH), GREEN));
        System.out.println(style(center("SMARTSTAY CONFIRMATION"), BOLD + GREEN));
        System.out.println(style("═".repeat(WIDTH), GREEN));
        System.out.println();
        field("Booking ID", r.getBookingId());
        field("Customer", r.getCustomer().getName());
        field("Room", String.valueOf(r.getRoomNumber()));
        field("Room Type", r.getRoomType());
        System.out.println();
        field("Check-in", r.getCheckIn().toString());
        field("Check-out", r.getCheckOut().toString());
        field("Nights", String.valueOf(r.getNights()));
        field("Guests", String.valueOf(r.getGuests()));
        System.out.println();
        field("Room Cost", rupees(price.getRoomTotal()));
        field("Extra Services", rupees(price.getServicesTotal()));
        System.out.println(style("─".repeat(WIDTH), DIM));
        System.out.println(style(String.format("%-22s: %s", "TOTAL PAID", rupees(price.getTotal())), BOLD));
        System.out.println();
        field("Paid Via", payment.getMethodDetail());
        field("Transaction ID", payment.getTransactionId());
        field("Payment Status", style(r.getPaymentStatus().name(), GREEN));
        field("Booking Status", style(r.getBookingStatus().name(), GREEN));
        System.out.println();
        System.out.println(center("Thank you for choosing SmartStay!"));
        System.out.println(style("═".repeat(WIDTH), GREEN));
    }

    private void printBookingDetails(Reservation r) {
        Customer c = r.getCustomer();
        PriceBreakdown price = r.getPriceBreakdown();
        Payment payment = hotel.findPaymentForBooking(r.getBookingId());

        printHeader("BOOKING DETAILS: " + r.getBookingId());
        field("Customer", c.getName() + " (" + c.getCustomerId() + ")");
        field("Phone", c.getPhone());
        field("Email", c.getEmail());
        System.out.println(style("─".repeat(WIDTH), DIM));
        field("Room", String.valueOf(r.getRoomNumber()));
        field("Room Type", r.getRoomType());
        field("Check-in", r.getCheckIn().toString());
        field("Check-out", r.getCheckOut().toString());
        field("Nights", String.valueOf(r.getNights()));
        field("Guests", String.valueOf(r.getGuests()));
        System.out.println(style("─".repeat(WIDTH), DIM));
        moneyLine("Room Cost", price.getBaseRoomCost(), "");
        if (price.getWeekendSurcharge() > 0) {
            moneyLine("Weekend Surcharge", price.getWeekendSurcharge(), "+ ");
        }
        if (price.getLongStayDiscount() > 0) {
            moneyLine("Long-Stay Discount", price.getLongStayDiscount(), "- ");
        }
        if (price.getLoyaltyDiscount() > 0) {
            moneyLine("Loyalty Discount", price.getLoyaltyDiscount(), "- ");
        }
        if (r.getServices().isEmpty()) {
            field("Services", "None");
        }
        for (Map.Entry<Service, Integer> entry : price.getServiceCosts().entrySet()) {
            moneyLine(entry.getKey().getDisplayName(), entry.getValue(), "");
        }
        System.out.println(style("─".repeat(WIDTH), DIM));
        System.out.println(style(String.format("%-22s: %11s", "TOTAL AMOUNT", rupees(price.getTotal())), BOLD));
        System.out.println(style("─".repeat(WIDTH), DIM));
        if (payment != null) {
            field("Paid Via", payment.getMethodDetail());
            field("Transaction ID", payment.getTransactionId());
        }
        field("Payment Status", colorPayment(r.getPaymentStatus()));
        field("Booking Status", colorBooking(r.getBookingStatus()));
    }

    // ============================================================ 4-8 VIEWS

    private void viewBookingDetails() {
        printHeader("VIEW BOOKING DETAILS");
        String id = readBookingId();
        if (id == null) {
            return;
        }
        Reservation reservation = hotel.findReservation(id);
        if (reservation == null) {
            printError("No booking found with ID " + id + ".");
            return;
        }
        printBookingDetails(reservation);
    }

    private void cancelReservation() {
        printHeader("CANCEL RESERVATION");
        String id = readBookingId();
        if (id == null) {
            return;
        }
        Reservation reservation = hotel.getCancellableReservation(id);
        System.out.println(style("\nBooking found.", GREEN));
        System.out.printf("%s | Room %d (%s) | %s to %s | %s%n", reservation.getBookingId(),
                reservation.getRoomNumber(), reservation.getRoomType(), reservation.getCheckIn(),
                reservation.getCheckOut(), rupees(reservation.getTotalAmount()));

        System.out.println("\nAre you sure you want to cancel?");
        System.out.println(" 1. Yes");
        System.out.println(" 2. No");
        if (readInt("Enter choice (1-2): ", 1, 2) == 2) {
            System.out.println("Cancellation aborted. Your booking is unchanged.");
            return;
        }

        hotel.cancelReservation(reservation);
        System.out.println(style("\nReservation cancelled successfully.", GREEN));
        System.out.println("Refund of " + rupees(reservation.getTotalAmount()) + " marked as REFUNDED (simulated).");
        Room room = hotel.findRoom(reservation.getRoomNumber());
        if (room != null && room.getStatus() == RoomStatus.AVAILABLE) {
            System.out.println(style("Room " + room.getRoomNumber() + " is now available.", GREEN));
        } else if (room != null) {
            System.out.println("Room " + room.getRoomNumber() + " is free again for the cancelled dates.");
        }
    }

    private void viewAllReservations() {
        printHeader("ALL RESERVATIONS");
        List<Reservation> all = hotel.getAllReservations();
        if (all.isEmpty()) {
            System.out.println("No reservations yet. Use option 3 to make one.");
            return;
        }
        System.out.println(style(String.format("%-8s %-14s %-5s %-11s %-11s %-3s %-9s %-10s %s",
                "BOOKING", "CUSTOMER", "ROOM", "CHECK-IN", "CHECK-OUT", "N", "TOTAL", "STATUS", "PAYMENT"), DIM));
        for (Reservation r : all) {
            String name = r.getCustomer().getName();
            if (name.length() > 14) {
                name = name.substring(0, 13) + "…";
            }
            String status = String.format("%-10s", r.getBookingStatus());
            System.out.printf("%-8s %-14s %-5d %-11s %-11s %-3d %-9s %s %s%n",
                    r.getBookingId(), name, r.getRoomNumber(), r.getCheckIn(), r.getCheckOut(),
                    r.getNights(), rupees(r.getTotalAmount()),
                    colorBooking(r.getBookingStatus(), status), colorPayment(r.getPaymentStatus()));
        }
        System.out.println(style(all.size() + " reservation(s)", DIM));
    }

    private void viewPaymentDetails() {
        printHeader("PAYMENT DETAILS");
        List<Payment> all = hotel.getAllPayments();
        if (all.isEmpty()) {
            System.out.println("No payments recorded yet.");
            return;
        }
        System.out.println(style(String.format("%-9s %-8s %-28s %-9s %-9s %s",
                "TXN ID", "BOOKING", "METHOD", "AMOUNT", "STATUS", "TIME"), DIM));
        int collected = 0;
        for (Payment p : all) {
            String status = String.format("%-9s", p.getStatus());
            System.out.printf("%-9s %-8s %-28s %-9s %s %s%n", p.getTransactionId(), p.getBookingId(),
                    p.getMethodDetail(), rupees(p.getAmount()), colorPayment(p.getStatus(), status),
                    p.getTimestamp().format(TIME_FORMAT));
            if (p.getStatus() == PaymentStatus.PAID) {
                collected += p.getAmount();
            }
        }
        System.out.println(style("─".repeat(WIDTH), DIM));
        System.out.println("Total collected (PAID): " + style(rupees(collected), BOLD));
    }

    private void showStatistics() {
        HotelManager.Statistics s = hotel.getStatistics();
        printHeader("HOTEL STATISTICS");
        field("Total Rooms", String.valueOf(s.totalRooms()));
        field("Available Rooms", style(String.valueOf(s.availableRooms()), GREEN));
        field("Booked Rooms", style(String.valueOf(s.bookedRooms()), YELLOW));
        if (s.maintenanceRooms() > 0) {
            field("Under Maintenance", style(String.valueOf(s.maintenanceRooms()), RED));
        }
        System.out.println();
        field("Total Reservations", String.valueOf(s.totalReservations()));
        field("Confirmed", String.valueOf(s.confirmed()));
        field("Cancelled", String.valueOf(s.cancelled()));
        field("Revenue (PAID)", rupees(s.revenue()));
        System.out.println();
        int occupied = s.totalRooms() == 0 ? 0 : s.bookedRooms() * 100 / s.totalRooms();
        field("Occupancy", bar(occupied, 100, 20) + " " + occupied + "%");
        System.out.println();
        System.out.println("Bookings by room type:");
        int max = 1;
        for (int count : s.bookingsByType().values()) {
            max = Math.max(max, count);
        }
        for (Map.Entry<String, Integer> entry : s.bookingsByType().entrySet()) {
            System.out.printf("  %-9s %s %d%n", entry.getKey(), bar(entry.getValue(), max, 20), entry.getValue());
        }
        System.out.println(style("─".repeat(WIDTH), DIM));
        System.out.println("Most Popular Room Type: " + style(s.mostPopularType(), BOLD + CYAN));
    }

    // ================================================================ ADMIN

    private void adminMenu() {
        while (true) {
            printHeader("ADMIN: ROOM MANAGEMENT");
            System.out.println(" 1. Add New Room");
            System.out.println(" 2. View Rooms");
            System.out.println(" 3. Update Room Availability");
            System.out.println(" 4. Search Rooms");
            System.out.println(" 5. Back to Main Menu");
            int choice = readInt("Enter your choice (1-5): ", 1, 5);
            if (choice == 5) {
                return;
            }
            try {
                switch (choice) {
                    case 1 -> addRoom();
                    case 2 -> {
                        printHeader("ALL ROOMS");
                        printRoomTable(hotel.getAllRooms());
                    }
                    case 3 -> updateRoomAvailability();
                    case 4 -> searchRooms();
                    default -> printError("Invalid choice.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                printError(e.getMessage());
            }
        }
    }

    private void addRoom() {
        printHeader("ADD NEW ROOM");
        String type = readRoomTypeChoice(false);
        int number = readInt("Room number (1-9999): ", 1, 9999);
        int floor = readInt("Floor number (0-50): ", 0, 50);
        Room room = hotel.addRoom(type, number, floor);
        System.out.println(style("Room " + room.getRoomNumber() + " (" + room.getRoomType() + ") added successfully.", GREEN));
        printRoomCard(room, -1, false, false);
    }

    private void updateRoomAvailability() {
        printHeader("UPDATE ROOM AVAILABILITY");
        int number = readInt("Enter room number: ", 1, 9999);
        Room room = hotel.findRoom(number);
        if (room == null) {
            printError("Room " + number + " does not exist.");
            return;
        }
        System.out.println("Room " + number + " is currently: " + colorStatus(room.getStatus().getLabel(), room.getStatus()));
        System.out.println(" 1. Mark as Available (end maintenance)");
        System.out.println(" 2. Mark as Under Maintenance");
        System.out.println(" 3. Back");
        int choice = readInt("Enter choice (1-3): ", 1, 3);
        if (choice == 3) {
            return;
        }
        Room updated = hotel.setRoomMaintenance(number, choice == 2);
        System.out.println(style("Room " + number + " is now: " + updated.getStatus().getLabel(), GREEN));
    }

    // ============================================================ INPUT HELPERS

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            String text = readLine(prompt);
            try {
                int value = Integer.parseInt(text);
                if (value >= min && value <= max) {
                    return value;
                }
                printError("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                printError("Invalid input. Please enter a valid number.");
            }
        }
    }

    private String readValid(String prompt, Predicate<String> validator, String errorMessage) {
        while (true) {
            String text = readLine(prompt);
            if (validator.test(text)) {
                return text;
            }
            printError(errorMessage);
        }
    }

    private LocalDate readDate(String prompt) {
        String[] patterns = {
                "d-M-yyyy", "d/M/yyyy", "d.M.yyyy",
                "yyyy-M-d", "yyyy/M/d", "yyyy.M.d", "yyyy-MM-dd"
        };
        while (true) {
            String text = readLine(prompt).trim();
            for (String p : patterns) {
                try {
                    return LocalDate.parse(text, DateTimeFormatter.ofPattern(p));
                } catch (Exception ignored) {}
            }
            try {
                return LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (Exception ignored) {}
            printError("Invalid date format. Use DD-MM-YYYY or YYYY-MM-DD (example: "
                    + LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ").");
        }
    }

    private LocalDate readCheckIn() {
        while (true) {
            LocalDate date = readDate("Check-in date (yyyy-MM-dd): ");
            try {
                hotel.validateCheckIn(date);
                return date;
            } catch (IllegalArgumentException e) {
                printError(e.getMessage());
            }
        }
    }

    private LocalDate readCheckOut(LocalDate checkIn) {
        while (true) {
            LocalDate date = readDate("Check-out date (yyyy-MM-dd): ");
            try {
                hotel.validateCheckOut(checkIn, date);
                return date;
            } catch (IllegalArgumentException e) {
                printError(e.getMessage());
            }
        }
    }

    /** Returns the booking ID in upper case, or null if the user types 0 to go back. */
    private String readBookingId() {
        while (true) {
            String text = readLine("Enter Booking ID (example SS1001, or 0 to go back): ").toUpperCase();
            if (text.equals("0")) {
                return null;
            }
            if (text.matches("SS\\d{4,}")) {
                return text;
            }
            printError("Invalid Booking ID. It looks like SS1001.");
        }
    }

    private void pause() {
        readLine(style("\nPress Enter to continue...", DIM));
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ============================================================ FORMAT HELPERS

    private static String rupees(int amount) {
        return String.format(Locale.US, "₹%,d", amount);
    }

    private static String style(String text, String codes) {
        return COLOR ? codes + text + RESET : text;
    }

    private static String center(String text) {
        int padding = Math.max(0, (WIDTH - text.length()) / 2);
        return " ".repeat(padding) + text;
    }

    private void printHeader(String title) {
        System.out.println();
        System.out.println(style("─".repeat(WIDTH), DIM));
        System.out.println(style(title, BOLD));
        System.out.println(style("─".repeat(WIDTH), DIM));
    }

    private void printError(String message) {
        System.out.println(style("✖ " + message, RED));
    }

    private void field(String label, String value) {
        System.out.println(String.format("%-22s: %s", label, value));
    }

    private void moneyLine(String label, int amount, String sign) {
        System.out.println(String.format("%-22s: %11s", label, sign + rupees(amount)));
    }

    private String bar(int value, int max, int length) {
        int filled = max == 0 ? 0 : value * length / max;
        return style("█".repeat(filled), CYAN) + style("░".repeat(length - filled), DIM);
    }

    private String colorStatus(String text, RoomStatus status) {
        return switch (status) {
            case AVAILABLE -> style(text, GREEN);
            case BOOKED -> style(text, YELLOW);
            case MAINTENANCE -> style(text, RED);
        };
    }

    private String colorBooking(BookingStatus status) {
        return colorBooking(status, status.name());
    }

    private String colorBooking(BookingStatus status, String text) {
        return switch (status) {
            case CONFIRMED -> style(text, GREEN);
            case CANCELLED -> style(text, RED);
            case PENDING -> style(text, YELLOW);
        };
    }

    private String colorPayment(PaymentStatus status) {
        return colorPayment(status, status.name());
    }

    private String colorPayment(PaymentStatus status, String text) {
        return switch (status) {
            case PAID -> style(text, GREEN);
            case REFUNDED -> style(text, YELLOW);
            case FAILED -> style(text, RED);
            case PENDING -> style(text, DIM);
        };
    }
}
