import java.time.LocalDateTime;

/** A simulated payment record. No real money or gateway is involved. */
public class Payment {

    private final String transactionId;
    private final String bookingId;
    private final int amount;
    private final String methodName;
    private final String methodDetail;
    private PaymentStatus status;
    private final LocalDateTime timestamp;

    public Payment(String transactionId, String bookingId, int amount, String methodName,
                   String methodDetail, PaymentStatus status, LocalDateTime timestamp) {
        if (amount < 0) {
            throw new IllegalArgumentException("Payment amount cannot be negative.");
        }
        this.transactionId = transactionId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.methodName = methodName;
        this.methodDetail = methodDetail;
        this.status = status;
        this.timestamp = timestamp;
    }

    public void markRefunded() {
        this.status = PaymentStatus.REFUNDED;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public int getAmount() {
        return amount;
    }

    public String getMethodName() {
        return methodName;
    }

    /** Safe-to-store description such as "Card ****1234" (full card numbers are never saved). */
    public String getMethodDetail() {
        return methodDetail;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
