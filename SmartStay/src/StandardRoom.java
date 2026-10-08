import java.util.List;

public class StandardRoom extends Room {

    public StandardRoom(int roomNumber, int floorNumber) {
        super(roomNumber, floorNumber);
    }

    @Override
    public String getRoomType() {
        return "Standard";
    }

    @Override
    public int getPricePerNight() {
        return 2000;
    }

    @Override
    public int getMaxGuests() {
        return 2;
    }

    @Override
    public List<String> getAmenities() {
        return List.of("Wi-Fi", "TV", "AC");
    }
}
