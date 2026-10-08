import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The brain of SmartStay: rooms, customers, bookings, payments and statistics.
 * It never prints anything (that is ConsoleUI's job) and never touches files directly
 * (that is FileManager's job).
 */
public class HotelManager {

    private static final int MAX_NIGHTS = 30;
    private static final int FIRST_CUSTOMER_NUMBER = 1001;
    private static final int FIRST_BOOKING_NUMBER = 1001;
    private static final int FIRST_TRANSACTION_NUMBER = 10001;

    /** A room found by search, with a smart match score (0-100). */
    public record RoomMatch(Room room, int matchPercent) {
    }

    /** Dashboard numbers, calculated fresh from the stored data. */
    public record Statistics(int totalRooms, int availableRooms, int bookedRooms, int maintenanceRooms,
                             int totalReservations, int confirmed, int cancelled, int revenue,
                             String mostPopularType, Map<String, Integer> bookingsByType) {
    }

    private final FileManager fileManager;
    private final Map<Integer, Room> rooms = new TreeMap<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();
    private final List<Payment> payments = new ArrayList<>();
    private final List<String> startupMessages = new ArrayList<>();

    public HotelManager(FileManager fileManager) {
        this.fileManager = fileManager;
        loadData();
    }

    // ------------------------------------------------------------- STARTUP

    private void loadData() {
        for (Room room : fileManager.loadRooms()) {
            rooms.put(room.getRoomNumber(), room);
        }
        if (rooms.isEmpty()) {
            createDefaultRooms();
            startupMessages.add("First run: created " + rooms.size() + " default rooms.");
        }
        customers.addAll(fileManager.loadCustomers());
        reservations.addAll(fileManager.loadReservations(customers));
        payments.addAll(fileManager.loadPayments());
        startupMessages.addAll(fileManager.getLoadWarnings());

        refreshRoomStatuses();
        saveAll();
    }

    /** 8 Standard (floor 1), 8 Deluxe (floor 2), 4 Suites (floor 3). */
    private void createDefaultRooms() {
        for (int i = 1; i <= 8; i++) {
            rooms.put(100 + i, Room.create("Standard", 100 + i, 1));
            rooms.put(200 + i, Room.create("Deluxe", 200 + i, 2));
        }
        for (int i = 1; i <= 4; i++) {
            rooms.put(300 + i, Room.create("Suite", 300 + i, 3));
        }
    }

    public List<String> getStartupMessages() {
        return startupMessages;
    }

    private void saveAll() {
        fileManager.saveRooms(rooms.values());
        fileManager.saveCustomers(customers);
        fileManager.saveReservations(reservations);
        fileManager.savePayments(payments);
    }

    // --------------------------------------------------------------- ROOMS

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public Room findRoom(int roomNumber) {
        return rooms.get(roomNumber);
    }

    public Room addRoom(String type, int roomNumber, int floorNumber) {
        if (rooms.containsKey(roomNumber)) {
            throw new IllegalArgumentException("Room " + roomNumber + " already exists. Choose a different room number.");
        }
        Room room = Room.create(type, roomNumber, floorNumber);
        rooms.put(roomNumber, room);
        saveAll();
        return room;
    }

    /** Admin action: take a room out of service or put it back. */
    public Room setRoomMaintenance(int roomNumber, boolean underMaintenance) {
        Room room = rooms.get(roomNumber);
        if (room == null) {
            throw new IllegalArgumentException("Room " + roomNumber + " does not exist.");
        }
        if (underMaintenance) {
            if (hasActiveReservation(roomNumber)) {
                throw new IllegalStateException("Room " + roomNumber
                        + " has active reservations. Cancel them before closing the room.");
            }
            room.setStatus(RoomStatus.MAINTENANCE);
        } else {
            if (room.getStatus() != RoomStatus.MAINTENANCE) {
                throw new IllegalStateException("Room " + roomNumber + " is not under maintenance.");
            }
            room.setStatus(RoomStatus.AVAILABLE);
        }
        refreshRoomStatuses();
        saveAll();
        return room;
    }

    /**
     * Room status is NEVER set by hand during booking. It is derived from reservations:
     * a room is BOOKED while it has a confirmed reservation that has not ended.
     */
    private void refreshRoomStatuses() {
        for (Room room : rooms.values()) {
            if (room.getStatus() == RoomStatus.MAINTENANCE) {
                continue;
            }
            room.setStatus(hasActiveReservation(room.getRoomNumber())
                    ? RoomStatus.BOOKED : RoomStatus.AVAILABLE);
        }
    }

    private boolean hasActiveReservation(int roomNumber) {
        for (Reservation r : reservations) {
            if (r.getRoomNumber() == roomNumber && r.isActive()) {
                return true;
            }
        }
        return false;
    }

    /** Double-booking prevention: a room is free only if no confirmed booking overlaps the dates. */
    public boolean isRoomFree(Room room, LocalDate checkIn, LocalDate checkOut) {
        if (room.getStatus() == RoomStatus.MAINTENANCE) {
            return false;
        }
        for (Reservation r : reservations) {
            if (r.getRoomNumber() == room.getRoomNumber()
                    && r.getBookingStatus() == BookingStatus.CONFIRMED
                    && r.overlaps(checkIn, checkOut)) {
                return false;
            }
        }
        return true;
    }

    // -------------------------------------------------------- SMART SEARCH

    /**
     * Smart search. type == null means any type; maxBudget == 0 means no limit;
     * if dates are given, rooms are checked for those dates, otherwise for "available now".
     * Results are sorted best match first.
     */
    public List<RoomMatch> searchRooms(String type, int guests, int maxBudget,
                                       LocalDate checkIn, LocalDate checkOut) {
        List<RoomMatch> results = new ArrayList<>();
        for (Room room : rooms.values()) {
            if (type != null && !room.getRoomType().equalsIgnoreCase(type)) {
                continue;
            }
            if (room.getMaxGuests() < guests) {
                continue;
            }
            if (maxBudget > 0 && room.getPricePerNight() > maxBudget) {
                continue;
            }
            boolean free = (checkIn != null && checkOut != null)
                    ? isRoomFree(room, checkIn, checkOut)
                    : room.getStatus() == RoomStatus.AVAILABLE;
            if (free) {
                results.add(new RoomMatch(room, calculateMatchPercent(room, guests, maxBudget)));
            }
        }
        results.sort(Comparator.comparingInt(RoomMatch::matchPercent).reversed()
                .thenComparingInt(match -> match.room().getRoomNumber()));
        return results;
    }

    /**
     * Match score = average of
     *  - capacity fit: how well the room size fits the group (no wasted beds)
     *  - budget fit: how much of the budget is saved (cheaper = better value)
     */
    private int calculateMatchPercent(Room room, int guests, int maxBudget) {
        int reference = maxBudget > 0 ? maxBudget : highestRoomPrice();
        int capacityFit = guests * 100 / room.getMaxGuests();
        int budgetFit = 100 - room.getPricePerNight() * 100 / reference;
        int score = (capacityFit + budgetFit) / 2;
        return Math.max(0, Math.min(100, score));
    }

    private int highestRoomPrice() {
        int highest = 1;
        for (Room room : rooms.values()) {
            highest = Math.max(highest, room.getPricePerNight());
        }
        return highest;
    }

    // ----------------------------------------------------------- CUSTOMERS

    public Customer findCustomerByPhone(String phone) {
        for (Customer c : customers) {
            if (c.getPhone().equals(phone.trim())) {
                return c;
            }
        }
        return null;
    }

    public Customer registerCustomer(String name, String phone, String email) {
        if (findCustomerByPhone(phone) != null) {
            throw new IllegalArgumentException("A customer with this phone number already exists.");
        }
        Customer customer = new Customer(generateCustomerId(), name, phone, email);
        customers.add(customer);
        saveAll();
        return customer;
    }

    public LoyaltyTier getLoyaltyTier(Customer customer) {
        int confirmedBookings = 0;
        for (Reservation r : reservations) {
            if (r.getCustomer().getCustomerId().equals(customer.getCustomerId())
                    && r.getBookingStatus() == BookingStatus.CONFIRMED) {
                confirmedBookings++;
            }
        }
        return LoyaltyTier.fromBookingCount(confirmedBookings);
    }

    // ---------------------------------------------------- DATE VALIDATION

    public void validateCheckIn(LocalDate checkIn) {
        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past.");
        }
    }

    public void validateCheckOut(LocalDate checkIn, LocalDate checkOut) {
        if (checkOut.equals(checkIn)) {
            throw new IllegalArgumentException("Check-out cannot be the same day as check-in.");
        }
        if (checkOut.isBefore(checkIn)) {
            throw new IllegalArgumentException("Check-out must be after check-in.");
        }
        if (PricingEngine.countNights(checkIn, checkOut) > MAX_NIGHTS) {
            throw new IllegalArgumentException("Maximum stay is " + MAX_NIGHTS + " nights.");
        }
    }

    // ------------------------------------------------------------- BOOKING

    /** Step 1: build the reservation and its price. Nothing is saved yet. */
    public Reservation prepareReservation(Customer customer, Room room, LocalDate checkIn,
                                          LocalDate checkOut, int guests, List<Service> services) {
        validateCheckIn(checkIn);
        validateCheckOut(checkIn, checkOut);
        if (guests <= 0 || guests > room.getMaxGuests()) {
            throw new IllegalArgumentException("Room " + room.getRoomNumber() + " allows 1 to "
                    + room.getMaxGuests() + " guests.");
        }
        if (!isRoomFree(room, checkIn, checkOut)) {
            throw new IllegalStateException("Room " + room.getRoomNumber()
                    + " is already booked for these dates.");
        }
        PriceBreakdown price = PricingEngine.calculate(room, checkIn, checkOut, services,
                getLoyaltyTier(customer));
        return new Reservation(generateBookingId(), customer, room.getRoomNumber(), room.getRoomType(),
                checkIn, checkOut, guests, services, price, BookingStatus.PENDING, PaymentStatus.PENDING);
    }

    /**
     * Step 2: take payment. On success the reservation is confirmed and saved.
     * On a declined payment nothing is booked, but the failed attempt is recorded.
     */
    public Payment completeBooking(Reservation reservation, PaymentMethod method, String detail) {
        Room room = rooms.get(reservation.getRoomNumber());
        if (room == null || !isRoomFree(room, reservation.getCheckIn(), reservation.getCheckOut())) {
            throw new IllegalStateException("Sorry, room " + reservation.getRoomNumber()
                    + " is no longer available for these dates.");
        }
        boolean approved = method.authorize(detail);
        Payment payment = new Payment(generateTransactionId(), reservation.getBookingId(),
                reservation.getTotalAmount(), method.getName(), method.describe(detail),
                approved ? PaymentStatus.PAID : PaymentStatus.FAILED,
                LocalDateTime.now().withNano(0));
        payments.add(payment);

        if (approved) {
            reservation.confirmPayment();
            reservations.add(reservation);
        }
        refreshRoomStatuses();
        saveAll();
        return payment;
    }

    // -------------------------------------------------------- LOOKUP/CANCEL

    public Reservation findReservation(String bookingId) {
        for (Reservation r : reservations) {
            if (r.getBookingId().equalsIgnoreCase(bookingId.trim())) {
                return r;
            }
        }
        return null;
    }

    /** Finds a booking and checks it can be cancelled. Throws a friendly message if not. */
    public Reservation getCancellableReservation(String bookingId) {
        Reservation reservation = findReservation(bookingId);
        if (reservation == null) {
            throw new IllegalArgumentException("No booking found with ID " + bookingId.trim().toUpperCase() + ".");
        }
        if (reservation.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking " + reservation.getBookingId() + " is already cancelled.");
        }
        if (!reservation.getCheckOut().isAfter(LocalDate.now())) {
            throw new IllegalStateException("This stay is already completed and cannot be cancelled.");
        }
        return reservation;
    }

    public void cancelReservation(Reservation reservation) {
        reservation.cancel();
        for (Payment payment : payments) {
            if (payment.getBookingId().equals(reservation.getBookingId())
                    && payment.getStatus() == PaymentStatus.PAID) {
                payment.markRefunded();
            }
        }
        refreshRoomStatuses();
        saveAll();
    }

    /** The successful (PAID or REFUNDED) payment of a booking, or null. */
    public Payment findPaymentForBooking(String bookingId) {
        Payment found = null;
        for (Payment p : payments) {
            if (p.getBookingId().equals(bookingId) && p.getStatus() != PaymentStatus.FAILED) {
                found = p;
            }
        }
        return found;
    }

    public List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(reservations);
    }

    public List<Payment> getAllPayments() {
        return Collections.unmodifiableList(payments);
    }

    // ---------------------------------------------------------- STATISTICS

    public Statistics getStatistics() {
        int available = 0;
        int booked = 0;
        int maintenance = 0;
        for (Room room : rooms.values()) {
            switch (room.getStatus()) {
                case AVAILABLE -> available++;
                case BOOKED -> booked++;
                case MAINTENANCE -> maintenance++;
            }
        }

        Map<String, Integer> bookingsByType = new LinkedHashMap<>();
        for (String type : Room.TYPES) {
            bookingsByType.put(type, 0);
        }
        int confirmed = 0;
        int cancelled = 0;
        for (Reservation r : reservations) {
            if (r.getBookingStatus() == BookingStatus.CONFIRMED) {
                confirmed++;
                bookingsByType.merge(r.getRoomType(), 1, Integer::sum);
            } else if (r.getBookingStatus() == BookingStatus.CANCELLED) {
                cancelled++;
            }
        }

        int revenue = 0;
        for (Payment p : payments) {
            if (p.getStatus() == PaymentStatus.PAID) {
                revenue += p.getAmount();
            }
        }

        String mostPopular = "N/A";
        int highest = 0;
        for (Map.Entry<String, Integer> entry : bookingsByType.entrySet()) {
            if (entry.getValue() > highest) {
                highest = entry.getValue();
                mostPopular = entry.getKey();
            }
        }

        return new Statistics(rooms.size(), available, booked, maintenance, confirmed + cancelled,
                confirmed, cancelled, revenue, mostPopular, bookingsByType);
    }

    // ------------------------------------------------------- ID GENERATION

    /** IDs are built from the highest existing number, so they are never hard-coded or duplicated. */
    private String generateBookingId() {
        int highest = FIRST_BOOKING_NUMBER - 1;
        for (Reservation r : reservations) {
            highest = Math.max(highest, numberPart(r.getBookingId()));
        }
        return "SS" + (highest + 1);
    }

    private String generateTransactionId() {
        int highest = FIRST_TRANSACTION_NUMBER - 1;
        for (Payment p : payments) {
            highest = Math.max(highest, numberPart(p.getTransactionId()));
        }
        return "TXN" + (highest + 1);
    }

    private String generateCustomerId() {
        int highest = FIRST_CUSTOMER_NUMBER - 1;
        for (Customer c : customers) {
            highest = Math.max(highest, numberPart(c.getCustomerId()));
        }
        return "C" + (highest + 1);
    }

    private int numberPart(String id) {
        String digits = id.replaceAll("\\D", "");
        return digits.isEmpty() ? 0 : Integer.parseInt(digits);
    }
}
