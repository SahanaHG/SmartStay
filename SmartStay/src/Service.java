/** Optional extras a guest can add to a booking. Each service knows how it is charged. */
public enum Service {
    BREAKFAST("Breakfast", 300, true),
    AIRPORT_PICKUP("Airport Pickup", 800, false),
    EXTRA_BED("Extra Bed", 500, true);

    private final String displayName;
    private final int price;
    private final boolean chargedPerNight;

    Service(String displayName, int price, boolean chargedPerNight) {
        this.displayName = displayName;
        this.price = price;
        this.chargedPerNight = chargedPerNight;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getPrice() {
        return price;
    }

    public String getChargeLabel() {
        return chargedPerNight ? "per night" : "one-time";
    }

    public int calculateCost(int nights) {
        return chargedPerNight ? price * nights : price;
    }
}
