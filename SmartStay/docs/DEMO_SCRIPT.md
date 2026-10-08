# SmartStay Demo Video Script (about 4 minutes)

Before recording: delete the `data/` files (or start from the clean copy) so the first run creates fresh rooms. Use a large terminal font.

| Time | Action | What to say |
|---|---|---|
| 0:00 | Run `Main` | "This is SmartStay, a hotel reservation system in Java 21 using OOP and File I/O, built for CodeAlpha Task 4." |
| 0:20 | Menu 1 | "Three room categories: Standard, Deluxe and Suite, each with its own price, capacity and amenities. Room is an abstract class with three subclasses." |
| 0:45 | Menu 2: Suite, 2 guests, budget 6000 | "Smart search filters by type, guests and budget and ranks rooms with a match score." |
| 1:10 | Menu 3: new guest, dates, Suite 301, add services | "Dates use LocalDate; nights are calculated with ChronoUnit. Booking IDs are generated automatically." |
| 1:50 | Show price summary | "Pricing rules live in PricingEngine: weekend surcharge, long-stay and loyalty discounts, plus optional services." |
| 2:10 | Pay with a card ending 0000, then retry with UPI | "Payment is a simulation behind a PaymentMethod interface. A declined payment books nothing." |
| 2:30 | Show confirmation, then menu 4 | "Booking confirmation and full booking details." |
| 2:50 | Try booking 301 again for the same dates | "Double booking is blocked by date-range overlap checks." |
| 3:10 | Menu 5: cancel, then menu 1 | "Cancelling refunds the payment and frees the room automatically." |
| 3:30 | Menu 8 | "Statistics are calculated live from the stored data." |
| 3:45 | Exit, restart, menu 6 | "Everything is stored in text files, so data persists after restart." |
| 4:00 | Show `data/` folder and project structure | "UI, logic and storage are separate classes. Thanks for watching." |
