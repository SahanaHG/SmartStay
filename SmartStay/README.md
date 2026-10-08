# SmartStay – Hotel Reservation System

> A console-based hotel reservation and room management system in pure Java 21, with smart room search, dynamic pricing, simulated payments and permanent File I/O storage.

Built for the **CodeAlpha Java Programming Internship – Task 4 (Hotel Reservation System)**.

## Overview

SmartStay simulates a real hotel booking experience from the terminal. A guest can search rooms, book a stay, pay (simulated), view the booking and cancel it. Everything is saved in text files, so bookings survive after the program closes.

What makes it different from a basic booking program:

- **Date-range availability.** Rooms are checked per date range, not with a simple true/false flag, so double booking is impossible while the same room can still be sold for other dates.
- **Smart Pricing.** Weekend surcharge, long-stay discount and loyalty discount are applied automatically and shown in the price summary.
- **Best Match search.** Rooms are ranked by how well they fit the guest count and budget.
- **Loyalty tiers.** Returning guests (identified by phone number) earn Silver and Gold discounts.

## Features

| Area | What it does |
|---|---|
| Rooms | 20 rooms across Standard, Deluxe and Suite with different price, capacity and amenities |
| Smart search | Filter by type, guests, budget and optional dates; results ranked with a match score |
| Reservations | Auto-generated booking IDs (SS1001, SS1002...), customer validation, date validation |
| Pricing | Nights calculated with `ChronoUnit`; optional services; weekend, long-stay and loyalty rules |
| Payment (simulation) | UPI, Card, Cash; unique transaction IDs; declined-payment handling; refund on cancel |
| Booking details | Full view by Booking ID |
| Cancellation | Confirmation prompt, refund status, room becomes available again |
| Statistics | Rooms, reservations, revenue, occupancy bar and most popular room type, all calculated live |
| Admin panel | Add room, view rooms, update availability (maintenance mode), search rooms |
| Persistence | Rooms, customers, bookings and payments saved in `data/` and reloaded at start |
| Safe input | Invalid input never crashes the program |

## Technologies Used

- Java 21 (JDK 21)
- IntelliJ IDEA (any IDE or the command line works)
- Java Collections (`List`, `Map`, `TreeMap`), `java.time` (`LocalDate`, `ChronoUnit`)
- File I/O (`BufferedReader`, `BufferedWriter`, `FileReader`, `FileWriter`)
- No external frameworks, no paid APIs, no dependencies

## OOP Concepts Used

| Concept | Where |
|---|---|
| Abstraction | `Room` is abstract: it declares price, capacity and amenities but cannot be created directly |
| Inheritance | `StandardRoom`, `DeluxeRoom`, `SuiteRoom` extend `Room` |
| Polymorphism / overriding | Each room type overrides `getPricePerNight()`, `getMaxGuests()`, `getAmenities()`; the app handles all rooms as `Room` |
| Interface | `PaymentMethod` is implemented by `UpiPayment`, `CardPayment`, `CashPayment` (each validates and authorises differently) |
| Encapsulation | Private fields, getters, validation inside constructors |
| Enums with behaviour | `Service` (knows how it is charged), `LoyaltyTier`, `RoomStatus`, `BookingStatus`, `PaymentStatus` |
| Separation of responsibilities | UI (`ConsoleUI`), logic (`HotelManager`, `PricingEngine`), storage (`FileManager`) |
| Composition | `Reservation` has a `Customer`, a list of `Service` and a `PriceBreakdown` |

## File I/O

All files are inside the `data/` folder. One record per line, fields separated by `|`.

| File | Contents |
|---|---|
| `rooms.txt` | room number, type, floor, status |
| `customers.txt` | customer ID, name, phone, email |
| `bookings.txt` | booking ID, customer, room, dates, guests, services, price parts, statuses |
| `payments.txt` | transaction ID, booking ID, amount, method, status, time |

- Data is loaded when the program starts and saved after every booking, cancellation and admin change.
- Missing files are created automatically on first run (20 default rooms are generated).
- Corrupted or duplicate lines are skipped with a warning instead of crashing.
- Full card numbers are never stored, only the last 4 digits.

## Project Structure

```
SmartStay/
├── src/
│   ├── Main.java              starts the app
│   ├── ConsoleUI.java         menus, input and output
│   ├── HotelManager.java      booking, search, cancel, statistics
│   ├── FileManager.java       all File I/O
│   ├── PricingEngine.java     all price rules
│   ├── Room.java              abstract base class
│   ├── StandardRoom.java
│   ├── DeluxeRoom.java
│   ├── SuiteRoom.java
│   ├── Customer.java          with validation
│   ├── Reservation.java
│   ├── PriceBreakdown.java
│   ├── Payment.java
│   ├── PaymentMethod.java     interface
│   ├── UpiPayment.java
│   ├── CardPayment.java
│   ├── CashPayment.java
│   ├── Service.java           enum: Breakfast, Airport Pickup, Extra Bed
│   ├── LoyaltyTier.java       enum
│   ├── RoomStatus.java        enum
│   ├── BookingStatus.java     enum
│   └── PaymentStatus.java     enum
├── data/                      rooms, customers, bookings, payments
├── docs/                      test cases, demo script, viva notes, GitHub and LinkedIn text
├── README.md
└── .gitignore
```

## How to Run

**IntelliJ IDEA**

1. File → Open → select the `SmartStay` folder.
2. File → Project Structure → Project → set SDK to JDK 21. Mark `src` as *Sources Root* if it is not already.
3. Right-click `src/Main.java` → **Run 'Main.main()'**.

The working directory must be the project root so the `data/` folder is found (this is the IntelliJ default).

**Command line**

```bash
javac -d out src/*.java
java -cp out Main
```

Colours use ANSI codes (IntelliJ and most terminals support them). To switch colours off, set the environment variable `NO_COLOR=1`.

**Demo tip:** a card number ending in `0000` is declined, so you can show a failed payment and retry.

## Pricing Rules

| Rule | Value |
|---|---|
| Weekend surcharge | +10% of the room price for each Friday and Saturday night |
| Long-stay discount | -10% of room cost for 5 or more nights |
| Loyalty | Silver (2+ confirmed bookings) -5%, Gold (5+) -10% of room cost |
| Breakfast | ₹300 per night |
| Airport pickup | ₹800 one-time |
| Extra bed | ₹500 per night |
| Maximum stay | 30 nights |

## Sample Output

```
PRICE SUMMARY
────────────────────────────────────────────────
Room Cost (3 nights)  :     ₹15,000
Weekend Surcharge (1) :      + ₹500
Breakfast             :        ₹900
Airport Pickup        :        ₹800
Extra Bed             :      ₹1,500
────────────────────────────────────────────────
TOTAL                 :     ₹18,700
────────────────────────────────────────────────

Payment processing...
Payment successful!

Transaction ID: TXN10001
Payment Status: PAID

════════════════════════════════════════════════
             SMARTSTAY CONFIRMATION
════════════════════════════════════════════════

Booking ID            : SS1001
Customer              : Sahana
Room                  : 301
Room Type             : Suite

Check-in              : 2026-10-10
Check-out             : 2026-10-13
Nights                : 3
Guests                : 2

Room Cost             : ₹15,500
Extra Services        : ₹3,200
────────────────────────────────────────────────
TOTAL PAID            : ₹18,700

Paid Via              : UPI (sahana@upi)
Transaction ID        : TXN10001
Payment Status        : PAID
Booking Status        : CONFIRMED

       Thank you for choosing SmartStay!
════════════════════════════════════════════════
```

```
────────────────────────────────────────────────
HOTEL STATISTICS
────────────────────────────────────────────────
Total Rooms           : 20
Available Rooms       : 18
Booked Rooms          : 2

Total Reservations    : 2
Confirmed             : 2
Cancelled             : 0
Revenue (PAID)        : ₹22,200

Occupancy             : ██░░░░░░░░░░░░░░░░░░ 10%

Bookings by room type:
  Standard  ░░░░░░░░░░░░░░░░░░░░ 0
  Deluxe    ████████████████████ 1
  Suite     ████████████████████ 1
────────────────────────────────────────────────
Most Popular Room Type: Deluxe
```

## Future Enhancements

- Swing or JavaFX graphical interface
- Move storage from text files to SQLite/MySQL with JDBC
- Staff login with roles (admin / receptionist)
- Check-in and check-out workflow, invoice export to PDF
- Room-service ordering and seasonal pricing
- Unit tests with JUnit

## Author

**Sahana HG**, B.Tech Computer Science and Engineering student, Sapthagiri NPS University

- GitHub: `https://github.com/SahanaHG/SmartStay`
- LinkedIn: `https://www.linkedin.com/in/sahana-hg`

Built as part of the CodeAlpha Java Programming Internship.
