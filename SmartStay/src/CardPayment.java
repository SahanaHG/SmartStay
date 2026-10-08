/** Credit/Debit card simulation. Only the last 4 digits are ever stored. */
public class CardPayment implements PaymentMethod {

    @Override
    public String getName() {
        return "Credit/Debit Card";
    }

    @Override
    public String getDetailPrompt() {
        return "Enter 16-digit card number";
    }

    @Override
    public boolean isValidDetail(String detail) {
        return detail != null && normalize(detail).matches("\\d{16}");
    }

    @Override
    public String describe(String detail) {
        String digits = normalize(detail);
        return "Card ****" + digits.substring(digits.length() - 4);
    }

    /** Demo trick: a card number ending in 0000 is declined, so you can show a failed payment. */
    @Override
    public boolean authorize(String detail) {
        return !normalize(detail).endsWith("0000");
    }

    private String normalize(String detail) {
        return detail.replaceAll("[ -]", "");
    }
}
