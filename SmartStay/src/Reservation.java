import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** One hotel booking: who, which room, when, how much, and its current status. */
public class Reservation {

    private final String bookingId;
    private final Customer customer;
    private final int roomNumber;
    private final String roomType;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final int guests;
    private final List<Service> services;
    private final PriceBreakdown priceBreakdown;
    private BookingStatus bookingStatus;
    private PaymentStatus paymentStatus;

    public Reservation(String bookingId, Customer customer, int roomNumber, String roomType,
                       LocalDate checkIn, LocalDate checkOut, int guests, List<Service> services,
                       PriceBreakdown priceBreakdown, BookingStatus bookingStatus,
                       PaymentStatus paymentStatus) {
        if (bookingId == null || bookingId.isBlank()) {
            throw new IllegalArgumentException("Booking ID cannot be empty.");
        }
        if (customer == null) {
            throw new IllegalArgumentException("A reservation needs a customer.");
        }
        if (guests <= 0) {
            throw new IllegalArgumentException("Number of guests must be at least 1.");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out must be after check-in.");
        }
        this.bookingId = bookingId;
        this.customer = customer;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.guests = guests;
        this.services = new ArrayList<>(services);
        this.priceBreakdown = priceBreakdown;
        this.bookingStatus = bookingStatus;
        this.paymentStatus = paymentStatus;
    }

    public int getNights() {
        return PricingEngine.countNights(checkIn, checkOut);
    }

    public int getTotalAmount() {
        return priceBreakdown.getTotal();
    }

    /** Active = confirmed and the guest has not yet checked out. */
    public boolean isActive() {
        return bookingStatus == BookingStatus.CONFIRMED && checkOut.isAfter(LocalDate.now());
    }

    /** True if this booking's dates clash with the requested dates (check-out day is free). */
    public boolean overlaps(LocalDate otherCheckIn, LocalDate otherCheckOut) {
        return otherCheckIn.isBefore(checkOut) && otherCheckOut.isAfter(checkIn);
    }

    public void confirmPayment() {
        this.paymentStatus = PaymentStatus.PAID;
        this.bookingStatus = BookingStatus.CONFIRMED;
    }

    public void cancel() {
        if (bookingStatus == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking " + bookingId + " is already cancelled.");
        }
        this.bookingStatus = BookingStatus.CANCELLED;
        if (paymentStatus == PaymentStatus.PAID) {
            this.paymentStatus = PaymentStatus.REFUNDED;
        }
    }

    public String getBookingId() {
        return bookingId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public int getGuests() {
        return guests;
    }

    public List<Service> getServices() {
        return Collections.unmodifiableList(services);
    }

    public PriceBreakdown getPriceBreakdown() {
        return priceBreakdown;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
}
