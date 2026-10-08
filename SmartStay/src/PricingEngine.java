import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SmartStay Smart Pricing. All money calculations live here (never in the UI).
 *
 * Rules:
 *  - Base cost       = price per night x nights
 *  - Weekend surcharge: +10% for every Friday and Saturday night
 *  - Long-stay discount: -10% of base cost for stays of 5+ nights
 *  - Loyalty discount: Silver -5%, Gold -10% of base cost
 */
public final class PricingEngine {

    public static final int WEEKEND_SURCHARGE_PERCENT = 10;
    public static final int LONG_STAY_MIN_NIGHTS = 5;
    public static final int LONG_STAY_DISCOUNT_PERCENT = 10;

    private PricingEngine() {
    }

    public static int countNights(LocalDate checkIn, LocalDate checkOut) {
        return (int) ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public static int countWeekendNights(LocalDate checkIn, LocalDate checkOut) {
        int count = 0;
        for (LocalDate night = checkIn; night.isBefore(checkOut); night = night.plusDays(1)) {
            DayOfWeek day = night.getDayOfWeek();
            if (day == DayOfWeek.FRIDAY || day == DayOfWeek.SATURDAY) {
                count++;
            }
        }
        return count;
    }

    public static PriceBreakdown calculate(Room room, LocalDate checkIn, LocalDate checkOut,
                                           List<Service> services, LoyaltyTier tier) {
        int nights = countNights(checkIn, checkOut);
        int baseCost = room.getPricePerNight() * nights;

        int weekendNights = countWeekendNights(checkIn, checkOut);
        int surcharge = percentOf(room.getPricePerNight(), WEEKEND_SURCHARGE_PERCENT) * weekendNights;

        int longStayDiscount = nights >= LONG_STAY_MIN_NIGHTS
                ? percentOf(baseCost, LONG_STAY_DISCOUNT_PERCENT) : 0;
        int loyaltyDiscount = percentOf(baseCost, tier.getDiscountPercent());

        return new PriceBreakdown(baseCost, surcharge, longStayDiscount, loyaltyDiscount,
                serviceCosts(services, nights));
    }

    public static Map<Service, Integer> serviceCosts(List<Service> services, int nights) {
        Map<Service, Integer> costs = new LinkedHashMap<>();
        for (Service service : services) {
            costs.put(service, service.calculateCost(nights));
        }
        return costs;
    }

    private static int percentOf(int amount, int percent) {
        return (amount * percent + 50) / 100; // rounded to nearest rupee
    }
}
