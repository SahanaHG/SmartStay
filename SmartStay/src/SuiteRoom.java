import java.util.List;

public class SuiteRoom extends Room {

    public SuiteRoom(int roomNumber, int floorNumber) {
        super(roomNumber, floorNumber);
    }

    @Override
    public String getRoomType() {
        return "Suite";
    }

    @Override
    public int getPricePerNight() {
        return 5000;
    }

    @Override
    public int getMaxGuests() {
        return 4;
    }

    @Override
    public List<String> getAmenities() {
        return List.of("Wi-Fi", "TV", "AC", "Mini Bar", "Living Area");
    }
}
