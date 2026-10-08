import java.util.regex.Pattern;

/** A hotel guest. Validation rules live here so every part of the app uses the same rules. */
public class Customer {

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[\\p{L}][\\p{L} .'-]{1,39}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$");

    private final String customerId;
    private final String name;
    private final String phone;
    private final String email;

    public Customer(String customerId, String name, String phone, String email) {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("Customer ID cannot be empty.");
        }
        if (!isValidName(name)) {
            throw new IllegalArgumentException("Invalid customer name.");
        }
        if (!isValidPhone(phone)) {
            throw new IllegalArgumentException("Invalid phone number.");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email address.");
        }
        this.customerId = customerId;
        this.name = name.trim();
        this.phone = phone.trim();
        this.email = email.trim();
    }

    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
}
