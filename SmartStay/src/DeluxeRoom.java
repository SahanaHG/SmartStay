import java.util.List;

public class DeluxeRoom extends Room {

    public DeluxeRoom(int roomNumber, int floorNumber) {
        super(roomNumber, floorNumber);
    }

    @Override
    public String getRoomType() {
        return "Deluxe";
    }

    @Override
    public int getPricePerNight() {
        return 3500;
    }

    @Override
    public int getMaxGuests() {
        return 3;
    }

    @Override
    public List<String> getAmenities() {
        return List.of("Wi-Fi", "TV", "AC", "Mini Bar");
    }
}
