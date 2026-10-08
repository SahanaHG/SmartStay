/**
 * A way to pay. UPI, Card and Cash each implement this differently,
 * so the booking code can treat them all the same (polymorphism).
 */
public interface PaymentMethod {

    String getName();

    /** Text asking for extra details (UPI ID, card number) or null if none are needed. */
    String getDetailPrompt();

    boolean isValidDetail(String detail);

    /** Safe description to store, e.g. "Card ****1234". */
    String describe(String detail);

    /** Simulated bank/gateway response. false = payment declined. */
    boolean authorize(String detail);
}
