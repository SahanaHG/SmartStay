/** Lifecycle of a reservation. PENDING exists only until payment succeeds. */
public enum BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
}
