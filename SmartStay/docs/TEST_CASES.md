# SmartStay Test Cases

Use dates in the future (check-in cannot be in the past). Start with a fresh `data/` folder for a clean run.

## Input validation

| # | Where | Input | Expected result |
|---|---|---|---|
| 1 | Main menu | `abc` | Invalid input. Please enter a valid number. |
| 2 | Main menu | `-1` or `99` | Please enter a number between 1 and 10. |
| 3 | Phone | `123`, `12345abcde` | Invalid phone number. Please enter exactly 10 digits. |
| 4 | Name | empty, `123` | Invalid name message, asks again |
| 5 | Email | `bad` | Invalid email message, asks again |
| 6 | Check-in | `2020-01-01` | Check-in date cannot be in the past. |
| 7 | Check-in | `notadate`, `2026-02-30` | Invalid date. Use the format yyyy-MM-dd |
| 8 | Check-out | same as check-in | Check-out cannot be the same day as check-in. |
| 9 | Check-out | before check-in | Check-out must be after check-in. |
| 10 | Guests | `0`, `-3` | Please enter a number between 1 and 20. |
| 11 | Room number | `999` | Room 999 does not exist. |
| 12 | Booking ID | `XYZ` | Invalid Booking ID. It looks like SS1001. |
| 13 | Booking ID | `SS9999` | No booking found with ID SS9999. |
| 14 | Payment | card `1234` | Invalid Credit/Debit Card details. |
| 15 | Admin add room | existing number | Room already exists. Choose a different room number. |

## Core features

| # | Scenario | Expected result |
|---|---|---|
| 16 | Search: Suite, 2 guests, budget 6000 | Only suites shown, top result tagged BEST MATCH |
| 17 | Book Suite 301 for 3 nights with all services | Price summary, payment, confirmation with ID SS1001 and TXN10001 |
| 18 | View All Rooms after booking | Room 301 shows Booked |
| 19 | Second customer books 301 for the same dates | Room 301 is not offered; typing 301 gives "not available" message |
| 20 | Second customer books 301 for different dates | Allowed (date-range availability) |
| 21 | View Booking Details `SS1001` | Customer, room, dates, nights, guests, services, total, payment and booking status |
| 22 | Cancel `SS1001` and answer 1 (Yes) | Status CANCELLED, payment REFUNDED, "Room 301 is now available." |
| 23 | Cancel `SS1001` again | Booking SS1001 is already cancelled. |
| 24 | Cancel and answer 2 (No) | Booking unchanged |
| 25 | Hotel Statistics | Numbers match the data; most popular type correct |

## Pricing

| # | Scenario | Expected |
|---|---|---|
| 26 | Standard room, Fri to Thu (6 nights) | Base 12,000 + weekend surcharge 400 - long-stay discount 1,200 = 11,200 |
| 27 | Third booking by the same phone number | Welcome back, Silver tier, 5% loyalty discount |
| 28 | Breakfast + pickup + extra bed, 3 nights | 900 + 800 + 1,500 |

## Payment

| # | Scenario | Expected |
|---|---|---|
| 29 | Card ending `0000` | Payment declined, nothing booked, option to retry |
| 30 | Retry with Cash | Payment successful, booking confirmed |
| 31 | Payment Details menu | All attempts listed (FAILED, PAID, REFUNDED) with totals |

## File I/O and persistence

| # | Scenario | Expected |
|---|---|---|
| 32 | Make a booking, exit, start again | Booking still listed, room still Booked |
| 33 | Cancel, exit, start again | Booking still CANCELLED, room Available |
| 34 | Delete `data/payments.txt`, start | Program starts, file is recreated |
| 35 | Add a garbage line to `bookings.txt`, start | Warning shown, line skipped, no crash |
| 36 | Copy a booking line twice | "Duplicate booking ignored" warning |

## Admin

| # | Scenario | Expected |
|---|---|---|
| 37 | Add Suite 401, floor 4 | Room added and saved |
| 38 | Mark 401 under maintenance | Status Under Maintenance, not offered in search |
| 39 | Mark a booked room under maintenance | Refused: cancel active reservations first |
| 40 | Mark 401 available | Status Available |
