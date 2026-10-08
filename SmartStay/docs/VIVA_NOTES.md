# Viva / Interview Notes

**Why is Room abstract?** Every room has a number, floor and status, but price, capacity and amenities depend on the type. An abstract class holds the shared part and forces each subclass to define the rest.

**Where is polymorphism used?** The app stores all rooms as `Room`. Calling `getPricePerNight()` runs the version of the actual subclass. `PaymentMethod` works the same way for UPI, Card and Cash.

**Why an interface for payments?** The three methods validate and authorise differently, but the booking code treats them identically. Adding Net Banking later means writing one new class.

**How is double booking prevented?** `Reservation.overlaps()` checks whether two date ranges clash (`newIn < existingOut && newOut > existingIn`). The check runs when showing rooms and again right before saving.

**Why can a room that is "Booked" still be sold?** Status means "has an active reservation". Availability is decided per date range, so the same room can be booked for different dates.

**How is room status kept correct?** It is never set by hand during booking. `refreshRoomStatuses()` derives it from reservations after every change.

**How are IDs generated?** From the highest existing number in the data (SS1001, SS1002...), so they never repeat after a restart and nothing is hard-coded.

**Why is there a PricingEngine?** All money rules are in one place, so the UI never calculates prices and rules are easy to change and test.

**How does File I/O work?** `FileManager` is the only class that touches files. Records are `|`-separated lines; `BufferedReader` reads them at startup and `BufferedWriter` rewrites them after each change. Bad lines are skipped with a warning.

**How is invalid input handled?** `ConsoleUI` loops until the input is valid (`readInt`, `readValid`, `readDate`). Business errors are thrown as `IllegalArgumentException` / `IllegalStateException` and shown as friendly messages.

**What would you improve?** A database (JDBC), a GUI, unit tests, and staff login.
