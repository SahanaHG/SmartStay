import java.util.List;

/**
 * Base class for every room type.
 * Common data lives here; each room type decides its own price, capacity and amenities.
 */
public abstract class Room {

    public static final List<String> TYPES = List.of("Standard", "Deluxe", "Suite");

    private final int roomNumber;
    private final int floorNumber;
    private RoomStatus status;

    protected Room(int roomNumber, int floorNumber) {
        if (roomNumber <= 0) {
            throw new IllegalArgumentException("Room number must be a positive number.");
        }
        if (floorNumber < 0) {
            throw new IllegalArgumentException("Floor number cannot be negative.");
        }
        this.roomNumber = roomNumber;
        this.floorNumber = floorNumber;
        this.status = RoomStatus.AVAILABLE;
    }

    /** Factory method: creates the correct Room subclass from a type name. */
    public static Room create(String type, int roomNumber, int floorNumber) {
        return switch (type.trim().toLowerCase()) {
            case "standard" -> new StandardRoom(roomNumber, floorNumber);
            case "deluxe" -> new DeluxeRoom(roomNumber, floorNumber);
            case "suite" -> new SuiteRoom(roomNumber, floorNumber);
            default -> throw new IllegalArgumentException("Unknown room type: " + type);
        };
    }

    // Each room type provides its own values (polymorphism)
    public abstract String getRoomType();

    public abstract int getPricePerNight();

    public abstract int getMaxGuests();

    public abstract List<String> getAmenities();

    public String getAmenitiesText() {
        return String.join(", ", getAmenities());
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }
}
