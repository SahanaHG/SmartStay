import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handles ALL reading and writing of the data files. No other class touches files.
 * Each record is one line; fields are separated by the | character.
 * Lines starting with # are comments (used as column headers).
 */
public class FileManager {

    private static final String ROOMS_FILE = "rooms.txt";
    private static final String CUSTOMERS_FILE = "customers.txt";
    private static final String BOOKINGS_FILE = "bookings.txt";
    private static final String PAYMENTS_FILE = "payments.txt";
    private static final String NO_SERVICES = "NONE";

    private final Path dataDirectory;
    private final List<String> loadWarnings = new ArrayList<>();

    public FileManager(String dataDirectory) {
        Path path = Paths.get(dataDirectory);
        if (!Files.exists(path) && Files.exists(Paths.get("SmartStay", dataDirectory))) {
            path = Paths.get("SmartStay", dataDirectory);
        }
        this.dataDirectory = path;
    }

    public List<String> getLoadWarnings() {
        return loadWarnings;
    }

    // ---------------------------------------------------------------- ROOMS

    public List<Room> loadRooms() {
        List<Room> rooms = new ArrayList<>();
        Set<Integer> seenNumbers = new HashSet<>();
        for (String line : readLines(ROOMS_FILE)) {
            try {
                String[] p = split(line, 4);
                Room room = Room.create(p[1], Integer.parseInt(p[0]), Integer.parseInt(p[2]));
                room.setStatus(RoomStatus.valueOf(p[3]));
                if (seenNumbers.add(room.getRoomNumber())) {
                    rooms.add(room);
                } else {
                    loadWarnings.add("Duplicate room " + room.getRoomNumber() + " ignored in " + ROOMS_FILE);
                }
            } catch (IllegalArgumentException e) {
                warnSkipped(ROOMS_FILE, line);
            }
        }
        return rooms;
    }

    public void saveRooms(Collection<Room> rooms) {
        List<String> lines = new ArrayList<>();
        for (Room room : rooms) {
            lines.add(String.join("|", String.valueOf(room.getRoomNumber()), room.getRoomType(),
                    String.valueOf(room.getFloorNumber()), room.getStatus().name()));
        }
        writeLines(ROOMS_FILE, "# roomNumber|type|floor|status", lines);
    }

    // ------------------------------------------------------------ CUSTOMERS

    public List<Customer> loadCustomers() {
        List<Customer> customers = new ArrayList<>();
        Set<String> seenIds = new HashSet<>();
        for (String line : readLines(CUSTOMERS_FILE)) {
            try {
                String[] p = split(line, 4);
                Customer customer = new Customer(p[0], p[1], p[2], p[3]);
                if (seenIds.add(customer.getCustomerId())) {
                    customers.add(customer);
                } else {
                    loadWarnings.add("Duplicate customer " + p[0] + " ignored in " + CUSTOMERS_FILE);
                }
            } catch (IllegalArgumentException e) {
                warnSkipped(CUSTOMERS_FILE, line);
            }
        }
        return customers;
    }

    public void saveCustomers(List<Customer> customers) {
        List<String> lines = new ArrayList<>();
        for (Customer c : customers) {
            lines.add(String.join("|", c.getCustomerId(), c.getName(), c.getPhone(), c.getEmail()));
        }
        writeLines(CUSTOMERS_FILE, "# customerId|name|phone|email", lines);
    }

    // --------------------------------------------------------- RESERVATIONS

    public List<Reservation> loadReservations(List<Customer> customers) {
        Map<String, Customer> customersById = new HashMap<>();
        for (Customer c : customers) {
            customersById.put(c.getCustomerId(), c);
        }

        List<Reservation> reservations = new ArrayList<>();
        Set<String> seenIds = new HashSet<>();
        for (String line : readLines(BOOKINGS_FILE)) {
            try {
                String[] p = split(line, 14);
                Customer customer = customersById.get(p[1]);
                if (customer == null) {
                    throw new IllegalArgumentException("Unknown customer " + p[1]);
                }
                LocalDate checkIn = LocalDate.parse(p[4]);
                LocalDate checkOut = LocalDate.parse(p[5]);
                List<Service> services = parseServices(p[7]);
                PriceBreakdown price = new PriceBreakdown(
                        Integer.parseInt(p[8]), Integer.parseInt(p[9]),
                        Integer.parseInt(p[10]), Integer.parseInt(p[11]),
                        PricingEngine.serviceCosts(services, PricingEngine.countNights(checkIn, checkOut)));
                Reservation reservation = new Reservation(p[0], customer, Integer.parseInt(p[2]), p[3],
                        checkIn, checkOut, Integer.parseInt(p[6]), services, price,
                        BookingStatus.valueOf(p[12]), PaymentStatus.valueOf(p[13]));
                if (seenIds.add(reservation.getBookingId())) {
                    reservations.add(reservation);
                } else {
                    loadWarnings.add("Duplicate booking " + p[0] + " ignored in " + BOOKINGS_FILE);
                }
            } catch (IllegalArgumentException | DateTimeException e) {
                warnSkipped(BOOKINGS_FILE, line);
            }
        }
        return reservations;
    }

    public void saveReservations(List<Reservation> reservations) {
        List<String> lines = new ArrayList<>();
        for (Reservation r : reservations) {
            PriceBreakdown price = r.getPriceBreakdown();
            lines.add(String.join("|",
                    r.getBookingId(), r.getCustomer().getCustomerId(),
                    String.valueOf(r.getRoomNumber()), r.getRoomType(),
                    r.getCheckIn().toString(), r.getCheckOut().toString(),
                    String.valueOf(r.getGuests()), servicesToText(r.getServices()),
                    String.valueOf(price.getBaseRoomCost()), String.valueOf(price.getWeekendSurcharge()),
                    String.valueOf(price.getLongStayDiscount()), String.valueOf(price.getLoyaltyDiscount()),
                    r.getBookingStatus().name(), r.getPaymentStatus().name()));
        }
        writeLines(BOOKINGS_FILE,
                "# bookingId|customerId|room|type|checkIn|checkOut|guests|services|baseCost|weekendSurcharge|longStayDiscount|loyaltyDiscount|bookingStatus|paymentStatus",
                lines);
    }

    // ------------------------------------------------------------- PAYMENTS

    public List<Payment> loadPayments() {
        List<Payment> payments = new ArrayList<>();
        for (String line : readLines(PAYMENTS_FILE)) {
            try {
                String[] p = split(line, 7);
                payments.add(new Payment(p[0], p[1], Integer.parseInt(p[2]), p[3], p[4],
                        PaymentStatus.valueOf(p[5]), LocalDateTime.parse(p[6])));
            } catch (IllegalArgumentException | DateTimeException e) {
                warnSkipped(PAYMENTS_FILE, line);
            }
        }
        return payments;
    }

    public void savePayments(List<Payment> payments) {
        List<String> lines = new ArrayList<>();
        for (Payment p : payments) {
            lines.add(String.join("|", p.getTransactionId(), p.getBookingId(),
                    String.valueOf(p.getAmount()), p.getMethodName(), p.getMethodDetail(),
                    p.getStatus().name(), p.getTimestamp().toString()));
        }
        writeLines(PAYMENTS_FILE, "# transactionId|bookingId|amount|method|detail|status|timestamp", lines);
    }

    // -------------------------------------------------------------- HELPERS

    /** Reads all data lines. A missing file is normal on first run, so it returns an empty list. */
    private List<String> readLines(String fileName) {
        List<String> lines = new ArrayList<>();
        File file = dataDirectory.resolve(fileName).toFile();
        if (!file.exists()) {
            return lines;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            loadWarnings.add("Could not read " + fileName + ": " + e.getMessage());
        }
        return lines;
    }

    private void writeLines(String fileName, String header, List<String> lines) {
        try {
            Files.createDirectories(dataDirectory);
            File file = dataDirectory.resolve(fileName).toFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
                writer.write(header);
                writer.newLine();
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not save " + fileName, e);
        }
    }

    private String[] split(String line, int expectedFields) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != expectedFields) {
            throw new IllegalArgumentException("Expected " + expectedFields + " fields");
        }
        return parts;
    }

    private void warnSkipped(String fileName, String line) {
        loadWarnings.add("Skipped a corrupted line in " + fileName + ": " + line);
    }

    private String servicesToText(List<Service> services) {
        if (services.isEmpty()) {
            return NO_SERVICES;
        }
        List<String> names = new ArrayList<>();
        for (Service service : services) {
            names.add(service.name());
        }
        return String.join(",", names);
    }

    private List<Service> parseServices(String text) {
        List<Service> services = new ArrayList<>();
        if (!text.equals(NO_SERVICES)) {
            for (String name : text.split(",")) {
                services.add(Service.valueOf(name));
            }
        }
        return services;
    }
}
