public class CashPayment implements PaymentMethod {

    @Override
    public String getName() {
        return "Cash";
    }

    @Override
    public String getDetailPrompt() {
        return null; // nothing to enter, the guest pays at the front desk
    }

    @Override
    public boolean isValidDetail(String detail) {
        return true;
    }

    @Override
    public String describe(String detail) {
        return "Cash (front desk)";
    }

    @Override
    public boolean authorize(String detail) {
        return true;
    }
}
