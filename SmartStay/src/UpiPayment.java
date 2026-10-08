import java.util.regex.Pattern;

public class UpiPayment implements PaymentMethod {

    private static final Pattern UPI_PATTERN = Pattern.compile("^[A-Za-z0-9._-]{2,}@[A-Za-z]{2,}$");

    @Override
    public String getName() {
        return "UPI";
    }

    @Override
    public String getDetailPrompt() {
        return "Enter UPI ID (example: name@upi)";
    }

    @Override
    public boolean isValidDetail(String detail) {
        return detail != null && UPI_PATTERN.matcher(detail.trim()).matches();
    }

    @Override
    public String describe(String detail) {
        return "UPI (" + detail.trim() + ")";
    }

    @Override
    public boolean authorize(String detail) {
        return true;
    }
}
