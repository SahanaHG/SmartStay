import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/** Entry point. Only wires the pieces together; all logic lives in other classes. */
public class Main {

    public static void main(String[] args) {
        // Make sure the rupee symbol and box lines print correctly in any console
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));

        FileManager fileManager = new FileManager("data");
        HotelManager hotelManager = new HotelManager(fileManager);
        new ConsoleUI(hotelManager).start();
    }
}
