/**
 * SmartStay Loyalty Program.
 * Returning guests earn a discount on the room cost based on their confirmed bookings.
 */
public enum LoyaltyTier {
    REGULAR("Regular", 0),
    SILVER("Silver", 5),
    GOLD("Gold", 10);

    private static final int SILVER_MIN_BOOKINGS = 2;
    private static final int GOLD_MIN_BOOKINGS = 5;

    private final String label;
    private final int discountPercent;

    LoyaltyTier(String label, int discountPercent) {
        this.label = label;
        this.discountPercent = discountPercent;
    }

    public String getLabel() {
        return label;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public static LoyaltyTier fromBookingCount(int confirmedBookings) {
        if (confirmedBookings >= GOLD_MIN_BOOKINGS) {
            return GOLD;
        }
        if (confirmedBookings >= SILVER_MIN_BOOKINGS) {
            return SILVER;
        }
        return REGULAR;
    }
}
