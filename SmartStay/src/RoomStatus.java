/** Current state of a room in the hotel. */
public enum RoomStatus {
    AVAILABLE("Available"),
    BOOKED("Booked"),
    MAINTENANCE("Under Maintenance");

    private final String label;

    RoomStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
