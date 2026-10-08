import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Immutable record of how a booking's price was calculated. */
public class PriceBreakdown {

    private final int baseRoomCost;
    private final int weekendSurcharge;
    private final int longStayDiscount;
    private final int loyaltyDiscount;
    private final Map<Service, Integer> serviceCosts;

    public PriceBreakdown(int baseRoomCost, int weekendSurcharge, int longStayDiscount,
                          int loyaltyDiscount, Map<Service, Integer> serviceCosts) {
        this.baseRoomCost = baseRoomCost;
        this.weekendSurcharge = weekendSurcharge;
        this.longStayDiscount = longStayDiscount;
        this.loyaltyDiscount = loyaltyDiscount;
        this.serviceCosts = new LinkedHashMap<>(serviceCosts);
    }

    public int getBaseRoomCost() {
        return baseRoomCost;
    }

    public int getWeekendSurcharge() {
        return weekendSurcharge;
    }

    public int getLongStayDiscount() {
        return longStayDiscount;
    }

    public int getLoyaltyDiscount() {
        return loyaltyDiscount;
    }

    public Map<Service, Integer> getServiceCosts() {
        return Collections.unmodifiableMap(serviceCosts);
    }

    public int getServicesTotal() {
        int total = 0;
        for (int cost : serviceCosts.values()) {
            total += cost;
        }
        return total;
    }

    /** Room cost after surcharge and discounts. */
    public int getRoomTotal() {
        return baseRoomCost + weekendSurcharge - longStayDiscount - loyaltyDiscount;
    }

    public int getTotal() {
        return getRoomTotal() + getServicesTotal();
    }
}
