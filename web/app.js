/**
 * SmartStay Hotel Reservation System - Frontend Logic & Engine
 * Synchronizes with local storage and Java WebServer API.
 */

// Initial 20 Rooms (mirrors SmartStay Java defaults)
const DEFAULT_ROOMS = [
  { roomNumber: 101, type: "Standard", floor: 1, price: 2000, maxGuests: 2, amenities: ["Wi-Fi", "TV", "AC"], status: "AVAILABLE" },
  { roomNumber: 102, type: "Standard", floor: 1, price: 2000, maxGuests: 2, amenities: ["Wi-Fi", "TV", "AC"], status: "AVAILABLE" },
  { roomNumber: 103, type: "Standard", floor: 1, price: 2000, maxGuests: 2, amenities: ["Wi-Fi", "TV", "AC"], status: "AVAILABLE" },
  { roomNumber: 104, type: "Standard", floor: 1, price: 2000, maxGuests: 2, amenities: ["Wi-Fi", "TV", "AC"], status: "AVAILABLE" },
  { roomNumber: 105, type: "Standard", floor: 1, price: 2000, maxGuests: 2, amenities: ["Wi-Fi", "TV", "AC"], status: "AVAILABLE" },
  { roomNumber: 106, type: "Standard", floor: 1, price: 2000, maxGuests: 2, amenities: ["Wi-Fi", "TV", "AC"], status: "AVAILABLE" },
  { roomNumber: 107, type: "Standard", floor: 1, price: 2000, maxGuests: 2, amenities: ["Wi-Fi", "TV", "AC"], status: "AVAILABLE" },
  { roomNumber: 108, type: "Standard", floor: 1, price: 2000, maxGuests: 2, amenities: ["Wi-Fi", "TV", "AC"], status: "AVAILABLE" },
  { roomNumber: 201, type: "Deluxe", floor: 2, price: 3500, maxGuests: 3, amenities: ["Wi-Fi", "TV", "AC", "Mini Bar"], status: "AVAILABLE" },
  { roomNumber: 202, type: "Deluxe", floor: 2, price: 3500, maxGuests: 3, amenities: ["Wi-Fi", "TV", "AC", "Mini Bar"], status: "AVAILABLE" },
  { roomNumber: 203, type: "Deluxe", floor: 2, price: 3500, maxGuests: 3, amenities: ["Wi-Fi", "TV", "AC", "Mini Bar"], status: "AVAILABLE" },
  { roomNumber: 204, type: "Deluxe", floor: 2, price: 3500, maxGuests: 3, amenities: ["Wi-Fi", "TV", "AC", "Mini Bar"], status: "AVAILABLE" },
  { roomNumber: 205, type: "Deluxe", floor: 2, price: 3500, maxGuests: 3, amenities: ["Wi-Fi", "TV", "AC", "Mini Bar"], status: "AVAILABLE" },
  { roomNumber: 206, type: "Deluxe", floor: 2, price: 3500, maxGuests: 3, amenities: ["Wi-Fi", "TV", "AC", "Mini Bar"], status: "AVAILABLE" },
  { roomNumber: 207, type: "Deluxe", floor: 2, price: 3500, maxGuests: 3, amenities: ["Wi-Fi", "TV", "AC", "Mini Bar"], status: "AVAILABLE" },
  { roomNumber: 208, type: "Deluxe", floor: 2, price: 3500, maxGuests: 3, amenities: ["Wi-Fi", "TV", "AC", "Mini Bar"], status: "AVAILABLE" },
  { roomNumber: 301, type: "Suite", floor: 3, price: 5000, maxGuests: 4, amenities: ["Wi-Fi", "TV", "AC", "Mini Bar", "Living Area"], status: "AVAILABLE" },
  { roomNumber: 302, type: "Suite", floor: 3, price: 5000, maxGuests: 4, amenities: ["Wi-Fi", "TV", "AC", "Mini Bar", "Living Area"], status: "AVAILABLE" },
  { roomNumber: 303, type: "Suite", floor: 3, price: 5000, maxGuests: 4, amenities: ["Wi-Fi", "TV", "AC", "Mini Bar", "Living Area"], status: "AVAILABLE" },
  { roomNumber: 304, type: "Suite", floor: 3, price: 5000, maxGuests: 4, amenities: ["Wi-Fi", "TV", "AC", "Mini Bar", "Living Area"], status: "AVAILABLE" }
];

const ROOM_IMAGES = {
  Standard: "assets/standard.jpg",
  Deluxe: "assets/deluxe.jpg",
  Suite: "assets/suite.jpg"
};

// Application State
let appState = {
  rooms: [],
  customers: [],
  bookings: [],
  payments: [],
  activeFilter: "ALL",
  activeFloor: "ALL",
  activeStatus: "ALL",
  selectedRoom: null,
  activePaymentMethod: "UPI"
};

// Initialize Application
document.addEventListener("DOMContentLoaded", () => {
  initStorage();
  initDateDefaults();
  initWelcomeDashboard();
  renderRooms();
  renderStatistics();
  renderAdmin();
  executeSmartSearch();
  setupSmoothScrolling();
});

// ------------------------------------------------------------- STORAGE
function initStorage() {
  const savedRooms = localStorage.getItem("smartstay_rooms");
  if (savedRooms) {
    try {
      appState.rooms = JSON.parse(savedRooms);
    } catch {
      appState.rooms = [...DEFAULT_ROOMS];
    }
  } else {
    appState.rooms = [...DEFAULT_ROOMS];
    saveState("rooms");
  }

  const savedCustomers = localStorage.getItem("smartstay_customers");
  appState.customers = savedCustomers ? JSON.parse(savedCustomers) : [];

  const savedBookings = localStorage.getItem("smartstay_bookings");
  appState.bookings = savedBookings ? JSON.parse(savedBookings) : [];

  const savedPayments = localStorage.getItem("smartstay_payments");
  appState.payments = savedPayments ? JSON.parse(savedPayments) : [];
}

function saveState(key) {
  if (key === "rooms") localStorage.setItem("smartstay_rooms", JSON.stringify(appState.rooms));
  if (key === "customers") localStorage.setItem("smartstay_customers", JSON.stringify(appState.customers));
  if (key === "bookings") localStorage.setItem("smartstay_bookings", JSON.stringify(appState.bookings));
  if (key === "payments") localStorage.setItem("smartstay_payments", JSON.stringify(appState.payments));
}

// ------------------------------------------------------------- DATES
function initDateDefaults() {
  const today = new Date();
  const tomorrow = new Date();
  tomorrow.setDate(today.getDate() + 2);

  const todayStr = formatDateISO(today);
  const tomorrowStr = formatDateISO(tomorrow);

  const heroIn = document.getElementById("hero-checkin");
  const heroOut = document.getElementById("hero-checkout");
  const sIn = document.getElementById("search-checkin");
  const sOut = document.getElementById("search-checkout");
  const bIn = document.getElementById("book-checkin");
  const bOut = document.getElementById("book-checkout");

  if (heroIn) { heroIn.min = todayStr; heroIn.value = todayStr; }
  if (heroOut) { heroOut.min = todayStr; heroOut.value = tomorrowStr; }
  if (sIn) { sIn.min = todayStr; sIn.value = todayStr; }
  if (sOut) { sOut.min = todayStr; sOut.value = tomorrowStr; }
  if (bIn) { bIn.min = todayStr; bIn.value = todayStr; }
  if (bOut) { bOut.min = todayStr; bOut.value = tomorrowStr; }

  updateSearchNights();
}

function formatDateISO(d) {
  return d.toISOString().split("T")[0];
}

function countNights(checkInStr, checkOutStr) {
  if (!checkInStr || !checkOutStr) return 1;
  const d1 = new Date(checkInStr);
  const d2 = new Date(checkOutStr);
  const diffTime = d2.getTime() - d1.getTime();
  const days = Math.round(diffTime / (1000 * 3600 * 24));
  return days > 0 ? days : 1;
}

function countWeekendNights(checkInStr, checkOutStr) {
  if (!checkInStr || !checkOutStr) return 0;
  const d1 = new Date(checkInStr);
  const d2 = new Date(checkOutStr);
  let count = 0;
  let curr = new Date(d1);
  while (curr < d2) {
    const day = curr.getDay(); // 0 is Sunday, 5 is Friday, 6 is Saturday
    if (day === 5 || day === 6) {
      count++;
    }
    curr.setDate(curr.getDate() + 1);
  }
  return count;
}

// ------------------------------------------------------------- ROOMS CATALOG
function renderRooms() {
  const grid = document.getElementById("rooms-grid");
  if (!grid) return;

  const filtered = appState.rooms.filter(room => {
    if (appState.activeFilter !== "ALL" && room.type !== appState.activeFilter) return false;
    if (appState.activeFloor !== "ALL" && room.floor !== parseInt(appState.activeFloor)) return false;
    if (appState.activeStatus !== "ALL" && room.status !== appState.activeStatus) return false;
    return true;
  });

  // Update counts
  const countAll = document.getElementById("count-all");
  const countStd = document.getElementById("count-standard");
  const countDel = document.getElementById("count-deluxe");
  const countSte = document.getElementById("count-suite");

  if (countAll) countAll.textContent = appState.rooms.length;
  if (countStd) countStd.textContent = appState.rooms.filter(r => r.type === "Standard").length;
  if (countDel) countDel.textContent = appState.rooms.filter(r => r.type === "Deluxe").length;
  if (countSte) countSte.textContent = appState.rooms.filter(r => r.type === "Suite").length;

  if (filtered.length === 0) {
    grid.innerHTML = `
      <div style="grid-column: 1/-1; text-align: center; padding: 3rem; color: var(--text-muted);">
        <p style="font-size: 1.2rem; margin-bottom: 0.5rem;">No rooms match the selected criteria.</p>
        <button class="btn btn-outline btn-sm" onclick="resetFilters()">Reset Filters</button>
      </div>
    `;
    return;
  }

  grid.innerHTML = filtered.map(room => {
    const badgeClass = room.status === "AVAILABLE" ? "badge-available" : (room.status === "BOOKED" ? "badge-booked" : "badge-maintenance");
    const isBookable = room.status === "AVAILABLE";

    return `
      <div class="room-card" id="room-card-${room.roomNumber}">
        <div class="room-card-media">
          <img src="${ROOM_IMAGES[room.type]}" alt="${room.type} Room" class="room-card-img" loading="lazy">
          <span class="room-card-badge ${badgeClass}">${room.status}</span>
          <span class="room-card-floor">Floor ${room.floor}</span>
        </div>
        <div class="room-card-body">
          <div class="room-header-row">
            <span class="room-category">${room.type} Class</span>
            <span class="muted-text" style="font-size: 0.8rem;">Max ${room.maxGuests} Guests</span>
          </div>
          <h3 class="room-number">Room ${room.roomNumber}</h3>
          <div class="room-amenities">
            ${room.amenities.map(a => `<span class="amenity-chip">✓ ${a}</span>`).join("")}
          </div>
          <div class="room-card-footer">
            <div class="room-price-wrap">
              <span class="price-val">₹${room.price.toLocaleString("en-IN")}</span>
              <span class="price-sub">per night</span>
            </div>
            ${isBookable 
              ? `<button class="btn btn-primary btn-sm" onclick="openBookingModal(${room.roomNumber})">Reserve Room</button>`
              : `<button class="btn btn-ghost btn-sm" disabled style="opacity: 0.6; cursor: not-allowed;">Unavailable</button>`
            }
          </div>
        </div>
      </div>
    `;
  }).join("");
}

function filterRoomCards(type) {
  appState.activeFilter = type;
  document.querySelectorAll("#room-filters .filter-tab").forEach(tab => {
    tab.classList.toggle("active", tab.getAttribute("data-filter") === type);
  });
  renderRooms();
}

function filterRoomsByFloor(floor) {
  appState.activeFloor = floor;
  renderRooms();
}

function filterRoomsByStatus(status) {
  appState.activeStatus = status;
  renderRooms();
}

function resetFilters() {
  appState.activeFilter = "ALL";
  appState.activeFloor = "ALL";
  appState.activeStatus = "ALL";
  const floorSelect = document.getElementById("floor-filter");
  const statusSelect = document.getElementById("status-filter");
  if (floorSelect) floorSelect.value = "ALL";
  if (statusSelect) statusSelect.value = "ALL";
  filterRoomCards("ALL");
}

// ------------------------------------------------------------- SMART SEARCH & MATCH
function updateSearchNights() {
  const dIn = document.getElementById("search-checkin")?.value;
  const dOut = document.getElementById("search-checkout")?.value;
  const count = countNights(dIn, dOut);
  const badge = document.getElementById("search-nights-count");
  if (badge) badge.textContent = count;
}

function adjustGuestCount(delta) {
  const input = document.getElementById("search-guests");
  const display = document.getElementById("search-guests-val");
  if (!input || !display) return;
  let val = parseInt(input.value) + delta;
  if (val < 1) val = 1;
  if (val > 4) val = 4;
  input.value = val;
  display.textContent = val;
}

function updateBudgetDisplay(val) {
  const display = document.getElementById("search-budget-display");
  if (display) display.textContent = `₹${parseInt(val).toLocaleString("en-IN")}`;
}

function applyHeroSearch() {
  const heroIn = document.getElementById("hero-checkin")?.value;
  const heroOut = document.getElementById("hero-checkout")?.value;
  const heroType = document.getElementById("hero-type")?.value;
  const heroGuests = document.getElementById("hero-guests")?.value;

  if (heroIn) document.getElementById("search-checkin").value = heroIn;
  if (heroOut) document.getElementById("search-checkout").value = heroOut;
  if (heroType) document.getElementById("search-type").value = heroType;
  if (heroGuests) {
    document.getElementById("search-guests").value = heroGuests;
    document.getElementById("search-guests-val").textContent = heroGuests;
  }
  updateSearchNights();
  executeSmartSearch();
  scrollToSection("search-section");
}

function isRoomAvailableForDates(roomNumber, checkInStr, checkOutStr) {
  const room = appState.rooms.find(r => r.roomNumber === roomNumber);
  if (!room || room.status === "MAINTENANCE") return false;

  const reqIn = new Date(checkInStr);
  const reqOut = new Date(checkOutStr);

  // Check overlap with active bookings
  for (const b of appState.bookings) {
    if (b.roomNumber === roomNumber && b.bookingStatus === "CONFIRMED") {
      const bIn = new Date(b.checkIn);
      const bOut = new Date(b.checkOut);
      // Overlap if reqIn < bOut and reqOut > bIn
      if (reqIn < bOut && reqOut > bIn) {
        return false;
      }
    }
  }
  return true;
}

function executeSmartSearch(e) {
  if (e) e.preventDefault();

  const checkIn = document.getElementById("search-checkin")?.value;
  const checkOut = document.getElementById("search-checkout")?.value;
  const prefType = document.getElementById("search-type")?.value || "ALL";
  const guests = parseInt(document.getElementById("search-guests")?.value || "2");
  const budget = parseInt(document.getElementById("search-budget")?.value || "6000");

  const resultsList = document.getElementById("matched-rooms-list");
  if (!resultsList) return;

  // Compute matches
  const matches = [];

  for (const room of appState.rooms) {
    const isAvailable = isRoomAvailableForDates(room.roomNumber, checkIn, checkOut);
    if (!isAvailable) continue;

    // Algorithm Match Score (0 - 100)
    let score = 50;

    // Type match (up to 25 pts)
    if (prefType === "ALL") {
      score += 20;
    } else if (room.type === prefType) {
      score += 25;
    } else {
      score -= 10;
    }

    // Capacity match (up to 20 pts)
    if (room.maxGuests >= guests) {
      if (room.maxGuests === guests) score += 20;
      else if (room.maxGuests === guests + 1) score += 15;
      else score += 10;
    } else {
      // Room too small
      continue;
    }

    // Budget match (up to 25 pts)
    if (room.price <= budget) {
      const diff = budget - room.price;
      const budgetBonus = Math.min(25, Math.floor(15 + (diff / 500)));
      score += budgetBonus;
    } else {
      score -= 25;
    }

    score = Math.max(10, Math.min(99, score));
    matches.push({ room, score });
  }

  // Sort descending by score
  matches.sort((a, b) => b.score - a.score);

  const heading = document.getElementById("results-count-heading");
  if (heading) heading.textContent = `Ranked Matches (${matches.length} Available)`;

  if (matches.length === 0) {
    resultsList.innerHTML = `
      <div style="text-align: center; padding: 3rem; background: var(--bg-card); border-radius: var(--radius-lg); border: 1px solid var(--border-subtle);">
        <p style="font-size: 1.1rem; color: var(--text-muted); margin-bottom: 0.5rem;">No available rooms match your exact criteria.</p>
        <p style="font-size: 0.85rem; color: var(--text-dim);">Try adjusting your dates or increasing your budget.</p>
      </div>
    `;
    return;
  }

  resultsList.innerHTML = matches.map((m, idx) => {
    const isTopMatch = idx === 0;
    const badgeClass = isTopMatch ? "badge-perfect" : "";
    const badgeText = isTopMatch ? `★ BEST MATCH (${m.score}%)` : `${m.score}% MATCH`;

    return `
      <div class="match-item-card" id="match-card-${m.room.roomNumber}">
        <div class="match-img-wrap">
          <img src="${ROOM_IMAGES[m.room.type]}" alt="${m.room.type}" class="match-img">
        </div>
        <div class="match-details">
          <span class="match-score-badge ${badgeClass}">${badgeText}</span>
          <h4>Room ${m.room.roomNumber} – ${m.room.type} Suite</h4>
          <p class="muted-text" style="font-size: 0.85rem; margin-bottom: 0.4rem;">
            Floor ${m.room.floor} • Accommodates up to ${m.room.maxGuests} Guests
          </p>
          <div style="font-size: 0.8rem; color: #cbd5e1;">
            ${m.room.amenities.join(" • ")}
          </div>
        </div>
        <div class="match-actions">
          <div class="room-price-wrap" style="text-align: right;">
            <span class="price-val">₹${m.room.price.toLocaleString("en-IN")}</span>
            <span class="price-sub">per night</span>
          </div>
          <button class="btn btn-primary btn-sm" onclick="openBookingModalForDates(${m.room.roomNumber}, '${checkIn}', '${checkOut}', ${guests})">
            Select Room
          </button>
        </div>
      </div>
    `;
  }).join("");
}

// ------------------------------------------------------------- BOOKING & PRICING
function openBookingModal(roomNumber) {
  const room = appState.rooms.find(r => r.roomNumber === roomNumber);
  if (!room) return;
  appState.selectedRoom = room;

  // Set default dates from search or today
  const sIn = document.getElementById("search-checkin")?.value;
  const sOut = document.getElementById("search-checkout")?.value;

  openBookingModalForDates(roomNumber, sIn, sOut, 2);
}

function openBookingModalForDates(roomNumber, checkIn, checkOut, guests) {
  const room = appState.rooms.find(r => r.roomNumber === roomNumber);
  if (!room) return;
  appState.selectedRoom = room;

  const modal = document.getElementById("booking-modal");
  const modalTitle = document.getElementById("modal-title");
  const modalSubtitle = document.getElementById("modal-room-subtitle");
  const previewImg = document.getElementById("preview-room-img");
  const previewType = document.getElementById("preview-room-type");
  const previewTitle = document.getElementById("preview-room-title");
  const previewAmenities = document.getElementById("preview-amenities");
  const bIn = document.getElementById("book-checkin");
  const bOut = document.getElementById("book-checkout");
  const bGuests = document.getElementById("book-guests");

  if (modalTitle) modalTitle.textContent = `Reserve Room ${room.roomNumber}`;
  if (modalSubtitle) modalSubtitle.textContent = `${room.type} Category • Floor ${room.floor} • ₹${room.price.toLocaleString("en-IN")}/night`;
  if (previewImg) previewImg.src = ROOM_IMAGES[room.type];
  if (previewType) previewType.textContent = `${room.type} Class`;
  if (previewTitle) previewTitle.textContent = `Room ${room.roomNumber}`;
  if (previewAmenities) previewAmenities.textContent = room.amenities.join(" • ");

  if (bIn && checkIn) bIn.value = checkIn;
  if (bOut && checkOut) bOut.value = checkOut;
  if (bGuests) bGuests.value = guests || 2;

  // Clear payment fields
  const cardNum = document.getElementById("card-number");
  const upiId = document.getElementById("upi-id");
  const errBox = document.getElementById("payment-error-alert");
  if (cardNum) cardNum.value = "";
  if (upiId) upiId.value = "";
  if (errBox) errBox.style.display = "none";

  selectPaymentMethod("UPI");
  recalculatePrice();

  if (modal) modal.classList.add("open");
}

function closeBookingModal() {
  const modal = document.getElementById("booking-modal");
  if (modal) modal.classList.remove("open");
}

function handleModalBackdropClick(e) {
  if (e.target.id === "booking-modal") closeBookingModal();
}

function handlePhoneLoyaltyLookup(phone) {
  const cleanPhone = phone.trim();
  const banner = document.getElementById("loyalty-banner");
  const badge = document.getElementById("loyalty-badge");
  const text = document.getElementById("loyalty-text");
  if (!banner || !badge || !text) return;

  if (cleanPhone.length < 10) {
    badge.className = "loyalty-badge";
    badge.textContent = "REGULAR";
    text.textContent = "Enter your 10-digit phone number to check SmartStay Loyalty tier discounts.";
    recalculatePrice();
    return;
  }

  // Count past confirmed bookings for this phone
  const pastBookings = appState.bookings.filter(b => b.phone === cleanPhone && b.bookingStatus === "CONFIRMED").length;

  if (pastBookings >= 5) {
    badge.className = "loyalty-badge tier-gold";
    badge.textContent = "GOLD VIP";
    text.textContent = `Welcome back! Gold Tier unlocked (${pastBookings} previous stays): 10% Loyalty Discount applied!`;
  } else if (pastBookings >= 2) {
    badge.className = "loyalty-badge tier-silver";
    badge.textContent = "SILVER VIP";
    text.textContent = `Welcome back! Silver Tier unlocked (${pastBookings} previous stays): 5% Loyalty Discount applied!`;
  } else {
    badge.className = "loyalty-badge";
    badge.textContent = "REGULAR";
    text.textContent = "Welcome! Book 2 stays to unlock Silver 5% off, 5 stays for Gold 10% off.";
  }

  recalculatePrice();
}

function getLoyaltyDiscountPercent(phone) {
  if (!phone || phone.trim().length < 10) return 0;
  const count = appState.bookings.filter(b => b.phone === phone.trim() && b.bookingStatus === "CONFIRMED").length;
  if (count >= 5) return 10;
  if (count >= 2) return 5;
  return 0;
}

function recalculatePrice() {
  if (!appState.selectedRoom) return;

  const room = appState.selectedRoom;
  const dIn = document.getElementById("book-checkin")?.value;
  const dOut = document.getElementById("book-checkout")?.value;
  const phone = document.getElementById("book-phone")?.value || "";

  const nights = countNights(dIn, dOut);
  const baseCost = room.price * nights;

  // Weekend Surcharge (+10% on room rate for Friday and Saturday nights)
  const weekendNights = countWeekendNights(dIn, dOut);
  const weekendSurcharge = Math.round(room.price * 0.10) * weekendNights;

  // Long Stay Discount (-10% of base cost for stays of 5+ nights)
  const longStayDiscount = nights >= 5 ? Math.round(baseCost * 0.10) : 0;

  // Loyalty Tier Discount
  const loyaltyPercent = getLoyaltyDiscountPercent(phone);
  const loyaltyDiscount = Math.round((baseCost * loyaltyPercent) / 100);

  // Optional Services
  const srvBreakfast = document.getElementById("srv-breakfast")?.checked;
  const srvPickup = document.getElementById("srv-pickup")?.checked;
  const srvBed = document.getElementById("srv-bed")?.checked;

  let servicesCost = 0;
  if (srvBreakfast) servicesCost += 300 * nights;
  if (srvPickup) servicesCost += 800;
  if (srvBed) servicesCost += 500 * nights;

  const grandTotal = Math.max(0, baseCost + weekendSurcharge - longStayDiscount - loyaltyDiscount + servicesCost);

  // Update UI Elements
  const elNights = document.getElementById("calc-nights");
  const elRate = document.getElementById("calc-rate");
  const elBase = document.getElementById("calc-base-cost");
  const elTotal = document.getElementById("calc-grand-total");
  const elCash = document.getElementById("cash-amount-display");

  if (elNights) elNights.textContent = nights;
  if (elRate) elRate.textContent = room.price.toLocaleString("en-IN");
  if (elBase) elBase.textContent = `₹${baseCost.toLocaleString("en-IN")}`;
  if (elTotal) elTotal.textContent = `₹${grandTotal.toLocaleString("en-IN")}`;
  if (elCash) elCash.textContent = grandTotal.toLocaleString("en-IN");

  // Weekend line
  const lineWeekend = document.getElementById("line-weekend");
  if (lineWeekend) {
    lineWeekend.style.display = weekendSurcharge > 0 ? "flex" : "none";
    document.getElementById("calc-weekend-nights").textContent = weekendNights;
    document.getElementById("calc-weekend-surcharge").textContent = `+₹${weekendSurcharge.toLocaleString("en-IN")}`;
  }

  // Long stay line
  const lineLongStay = document.getElementById("line-longstay");
  if (lineLongStay) {
    lineLongStay.style.display = longStayDiscount > 0 ? "flex" : "none";
    document.getElementById("calc-longstay-discount").textContent = `-₹${longStayDiscount.toLocaleString("en-IN")}`;
  }

  // Loyalty line
  const lineLoyalty = document.getElementById("line-loyalty");
  if (lineLoyalty) {
    lineLoyalty.style.display = loyaltyDiscount > 0 ? "flex" : "none";
    document.getElementById("calc-loyalty-percent").textContent = loyaltyPercent;
    document.getElementById("calc-loyalty-discount").textContent = `-₹${loyaltyDiscount.toLocaleString("en-IN")}`;
  }

  // Services line
  const lineServices = document.getElementById("line-services");
  if (lineServices) {
    lineServices.style.display = servicesCost > 0 ? "flex" : "none";
    document.getElementById("calc-services-cost").textContent = `+₹${servicesCost.toLocaleString("en-IN")}`;
  }

  return {
    nights,
    baseCost,
    weekendNights,
    weekendSurcharge,
    longStayDiscount,
    loyaltyDiscount,
    servicesCost,
    grandTotal
  };
}

function selectPaymentMethod(method) {
  appState.activePaymentMethod = method;
  document.querySelectorAll(".payment-tab").forEach(tab => {
    tab.classList.toggle("active", tab.getAttribute("data-method") === method);
  });

  const upiField = document.getElementById("payment-field-upi");
  const cardField = document.getElementById("payment-field-card");
  const cashField = document.getElementById("payment-field-cash");

  if (upiField) upiField.style.display = method === "UPI" ? "block" : "none";
  if (cardField) cardField.style.display = method === "Card" ? "block" : "none";
  if (cashField) cashField.style.display = method === "Cash" ? "block" : "none";
}

function formatCardInput(input) {
  let val = input.value.replace(/\D/g, "");
  val = val.substring(0, 16);
  const parts = [];
  for (let i = 0; i < val.length; i += 4) {
    parts.push(val.substring(i, i + 4));
  }
  input.value = parts.join(" ");
}

function executeBooking() {
  const name = document.getElementById("book-name")?.value.trim();
  const phone = document.getElementById("book-phone")?.value.trim();
  const email = document.getElementById("book-email")?.value.trim();
  const checkIn = document.getElementById("book-checkin")?.value;
  const checkOut = document.getElementById("book-checkout")?.value;
  const guests = parseInt(document.getElementById("book-guests")?.value || "2");
  const errBox = document.getElementById("payment-error-alert");

  if (errBox) errBox.style.display = "none";

  if (!name) return showPaymentError("Please enter the guest full name.");
  if (!phone || phone.length < 10) return showPaymentError("Please enter a valid 10-digit mobile number.");
  if (!email || !email.includes("@")) return showPaymentError("Please enter a valid email address.");
  if (!checkIn || !checkOut) return showPaymentError("Please select both check-in and check-out dates.");

  const dIn = new Date(checkIn);
  const dOut = new Date(checkOut);
  if (dOut <= dIn) return showPaymentError("Check-out date must be after check-in date.");

  const room = appState.selectedRoom;
  if (!room) return showPaymentError("No room selected.");

  // Check availability
  if (!isRoomAvailableForDates(room.roomNumber, checkIn, checkOut)) {
    return showPaymentError(`Room ${room.roomNumber} is no longer available for these dates.`);
  }

  // Validate payment
  let paymentDetails = "";
  if (appState.activePaymentMethod === "UPI") {
    const upi = document.getElementById("upi-id")?.value.trim();
    const upiRegex = /^[A-Za-z0-9._-]{2,}@[A-Za-z]{2,}$/;
    if (!upi || !upiRegex.test(upi)) {
      return showPaymentError("Invalid UPI ID format. Example: yourname@okaxis");
    }
    paymentDetails = `UPI (${upi})`;
  } else if (appState.activePaymentMethod === "Card") {
    const card = document.getElementById("card-number")?.value.replace(/\s+/g, "");
    if (!card || card.length !== 16 || !/^\d{16}$/.test(card)) {
      return showPaymentError("Please enter a 16-digit card number.");
    }
    // SmartStay Core Demo Feature: card ending in 0000 declines!
    if (card.endsWith("0000")) {
      return showPaymentError("Transaction Declined: Simulated bank authorization failed (Card ending in 0000 is rejected by test policy). Please use another card or UPI.");
    }
    paymentDetails = `Card ****${card.substring(12)}`;
  } else {
    paymentDetails = "Cash on Arrival";
  }

  // Pricing calculations
  const priceData = recalculatePrice();

  // Create or retrieve Customer
  let customer = appState.customers.find(c => c.phone === phone);
  if (!customer) {
    const customerId = `C${1001 + appState.customers.length}`;
    customer = { customerId, name, phone, email };
    appState.customers.push(customer);
    saveState("customers");
  } else {
    customer.name = name;
    customer.email = email;
    saveState("customers");
  }

  // Create Booking
  const bookingId = `SS${1001 + appState.bookings.length}`;
  const txnId = `TXN${10001 + appState.payments.length}`;

  const selectedServices = [];
  if (document.getElementById("srv-breakfast")?.checked) selectedServices.push("Breakfast");
  if (document.getElementById("srv-pickup")?.checked) selectedServices.push("Airport Pickup");
  if (document.getElementById("srv-bed")?.checked) selectedServices.push("Extra Bed");

  const newBooking = {
    bookingId,
    customerId: customer.customerId,
    guestName: name,
    phone,
    email,
    roomNumber: room.roomNumber,
    roomType: room.type,
    floor: room.floor,
    checkIn,
    checkOut,
    nights: priceData.nights,
    guests,
    services: selectedServices,
    pricing: priceData,
    bookingStatus: "CONFIRMED",
    paymentStatus: appState.activePaymentMethod === "Cash" ? "PENDING_CASH" : "PAID",
    paymentMethod: appState.activePaymentMethod,
    paymentDetails,
    txnId,
    timestamp: new Date().toISOString()
  };

  appState.bookings.push(newBooking);
  saveState("bookings");

  // Create Payment record
  const newPayment = {
    txnId,
    bookingId,
    amount: priceData.grandTotal,
    method: appState.activePaymentMethod,
    details: paymentDetails,
    status: appState.activePaymentMethod === "Cash" ? "PENDING" : "SUCCESS",
    timestamp: new Date().toISOString()
  };
  appState.payments.push(newPayment);
  saveState("payments");

  // Update room status if booking is current
  const today = formatDateISO(new Date());
  if (checkIn <= today && checkOut > today) {
    room.status = "BOOKED";
    saveState("rooms");
  }

  closeBookingModal();
  renderRooms();
  renderStatistics();
  renderAdmin();
  showTicketModal(newBooking);
  showToast(`Booking ${bookingId} confirmed successfully!`, "success");
}

function showPaymentError(msg) {
  const errBox = document.getElementById("payment-error-alert");
  if (errBox) {
    errBox.textContent = msg;
    errBox.style.display = "block";
  }
}

// ------------------------------------------------------------- CONFIRMATION TICKET
function showTicketModal(booking) {
  const modal = document.getElementById("ticket-modal");
  document.getElementById("ticket-booking-id").textContent = booking.bookingId;
  document.getElementById("ticket-txn-id").textContent = booking.txnId;
  document.getElementById("ticket-guest-name").textContent = booking.guestName;
  document.getElementById("ticket-guest-phone").textContent = booking.phone;
  document.getElementById("ticket-room").textContent = `Room ${booking.roomNumber} (${booking.roomType})`;
  document.getElementById("ticket-dates").textContent = `${booking.checkIn} to ${booking.checkOut} (${booking.nights} night${booking.nights > 1 ? 's' : ''})`;
  document.getElementById("ticket-services").textContent = booking.services.length > 0 ? booking.services.join(", ") : "None";
  document.getElementById("ticket-amount").textContent = `₹${booking.pricing.grandTotal.toLocaleString("en-IN")}`;

  if (modal) modal.classList.add("open");
}

function closeTicketModalAndShowBookings() {
  const modal = document.getElementById("ticket-modal");
  if (modal) modal.classList.remove("open");
  showAllBookings();
  scrollToSection("bookings-section");
}

function handleTicketBackdropClick(e) {
  if (e.target.id === "ticket-modal") {
    e.target.classList.remove("open");
  }
}

// ------------------------------------------------------------- MY BOOKINGS & CANCEL
function handleLookupKeyup(e) {
  if (e.key === "Enter") lookupBooking();
}

function lookupBooking() {
  const query = document.getElementById("lookup-query")?.value.trim();
  const area = document.getElementById("bookings-display-area");
  if (!area) return;

  if (!query) {
    return showAllBookings();
  }

  const results = appState.bookings.filter(b => 
    b.bookingId.toLowerCase() === query.toLowerCase() ||
    b.phone.includes(query) ||
    b.guestName.toLowerCase().includes(query.toLowerCase())
  );

  renderBookingsList(results, `Search results for "${query}"`);
}

function showAllBookings() {
  renderBookingsList(appState.bookings, "All Recorded Reservations");
}

function renderBookingsList(list, title) {
  const area = document.getElementById("bookings-display-area");
  if (!area) return;

  if (list.length === 0) {
    area.innerHTML = `
      <div style="grid-column: 1/-1; text-align: center; padding: 3rem; background: var(--bg-card); border-radius: var(--radius-lg); border: 1px solid var(--border-subtle);">
        <p style="font-size: 1.1rem; color: var(--text-muted); margin-bottom: 0.5rem;">No reservations found.</p>
        <p style="font-size: 0.85rem; color: var(--text-dim);">Check your Booking ID or phone number, or make a new reservation.</p>
      </div>
    `;
    return;
  }

  area.innerHTML = list.map(b => {
    const isConfirmed = b.bookingStatus === "CONFIRMED";
    const statusBadge = isConfirmed ? "badge-confirmed" : "badge-cancelled";

    return `
      <div class="booking-item-card" id="booking-card-${b.bookingId}">
        <div class="booking-card-header">
          <div>
            <span class="booking-id-tag">${b.bookingId}</span>
            <div style="font-size: 0.75rem; color: var(--text-dim);">${b.guestName}</div>
          </div>
          <span class="status-badge ${statusBadge}">${b.bookingStatus}</span>
        </div>
        <div class="booking-info-grid">
          <div class="booking-info-item">
            <label>Assigned Room</label>
            <strong>Room ${b.roomNumber} (${b.roomType})</strong>
          </div>
          <div class="booking-info-item">
            <label>Dates (${b.nights} nights)</label>
            <span>${b.checkIn} → ${b.checkOut}</span>
          </div>
          <div class="booking-info-item">
            <label>Payment Method</label>
            <span>${b.paymentDetails}</span>
          </div>
          <div class="booking-info-item">
            <label>Total Billed</label>
            <strong class="text-gold">₹${b.pricing.grandTotal.toLocaleString("en-IN")}</strong>
          </div>
        </div>
        ${b.services.length > 0 ? `
          <div style="font-size: 0.8rem; color: var(--text-muted);">
            <strong>Extras:</strong> ${b.services.join(", ")}
          </div>
        ` : ''}
        <div class="booking-card-footer">
          <span style="font-size: 0.75rem; color: var(--text-dim);">TXN: ${b.txnId}</span>
          ${isConfirmed ? `
            <button class="btn btn-outline btn-sm" style="color: var(--color-danger); border-color: rgba(239, 68, 68, 0.4);" onclick="confirmCancelBooking('${b.bookingId}')">
              Cancel Reservation
            </button>
          ` : `
            <span style="font-size: 0.8rem; color: var(--color-danger);">Refund Processed</span>
          `}
        </div>
      </div>
    `;
  }).join("");
}

function confirmCancelBooking(bookingId) {
  const booking = appState.bookings.find(b => b.bookingId === bookingId);
  if (!booking) return;

  const confirmMsg = `Cancel reservation ${bookingId} for ${booking.guestName}?\n\nA full refund of ₹${booking.pricing.grandTotal.toLocaleString("en-IN")} will be simulated and Room ${booking.roomNumber} will become available again.`;
  
  if (confirm(confirmMsg)) {
    booking.bookingStatus = "CANCELLED";
    booking.paymentStatus = "REFUNDED";
    saveState("bookings");

    // Add refund payment log
    const refundTxn = {
      txnId: `TXN${10001 + appState.payments.length}`,
      bookingId: booking.bookingId,
      amount: -booking.pricing.grandTotal,
      method: booking.paymentMethod,
      details: `Refund for ${booking.bookingId}`,
      status: "REFUNDED",
      timestamp: new Date().toISOString()
    };
    appState.payments.push(refundTxn);
    saveState("payments");

    // Refresh Room Status
    const room = appState.rooms.find(r => r.roomNumber === booking.roomNumber);
    if (room && room.status === "BOOKED") {
      room.status = "AVAILABLE";
      saveState("rooms");
    }

    renderRooms();
    renderStatistics();
    renderAdmin();
    showAllBookings();
    showToast(`Reservation ${bookingId} cancelled and refunded.`, "success");
  }
}

// ------------------------------------------------------------- LIVE STATISTICS
function renderStatistics() {
  const totalRooms = appState.rooms.length;
  const availRooms = appState.rooms.filter(r => r.status === "AVAILABLE").length;
  const bookedRooms = appState.rooms.filter(r => r.status === "BOOKED").length;
  const maintRooms = appState.rooms.filter(r => r.status === "MAINTENANCE").length;

  const totalRes = appState.bookings.length;
  const confirmedRes = appState.bookings.filter(b => b.bookingStatus === "CONFIRMED").length;
  const cancelledRes = appState.bookings.filter(b => b.bookingStatus === "CANCELLED").length;

  // Live Occupancy Rate
  const occupancyRate = totalRooms > 0 ? Math.round((bookedRooms / totalRooms) * 100) : 0;

  // Live Revenue
  const totalRevenue = appState.bookings
    .filter(b => b.bookingStatus === "CONFIRMED")
    .reduce((sum, b) => sum + (b.pricing?.grandTotal || 0), 0);

  // Category counts
  const countByType = { Standard: 0, Deluxe: 0, Suite: 0 };
  for (const b of appState.bookings) {
    if (b.bookingStatus === "CONFIRMED" && countByType[b.roomType] !== undefined) {
      countByType[b.roomType]++;
    }
  }

  // Most popular category
  let mostPop = "Standard";
  let maxCount = -1;
  for (const [type, cnt] of Object.entries(countByType)) {
    if (cnt > maxCount) {
      maxCount = cnt;
      mostPop = type;
    }
  }

  // Update elements
  const elTotalRooms = document.getElementById("stat-total-rooms");
  const elAvail = document.getElementById("stat-avail-count");
  const elBooked = document.getElementById("stat-booked-count");
  const elOccVal = document.getElementById("stat-occupancy-rate");
  const elOccBar = document.getElementById("stat-occupancy-bar");
  const elMaint = document.getElementById("stat-maintenance-count");
  const elRev = document.getElementById("stat-total-revenue");
  const elTotalRes = document.getElementById("stat-total-reservations");
  const elConf = document.getElementById("stat-confirmed-count");
  const elCanc = document.getElementById("stat-cancelled-count");
  const elPop = document.getElementById("stat-popular-type");

  if (elTotalRooms) elTotalRooms.textContent = totalRooms;
  if (elAvail) elAvail.textContent = availRooms;
  if (elBooked) elBooked.textContent = bookedRooms;
  if (elOccVal) elOccVal.textContent = `${occupancyRate}%`;
  if (elOccBar) elOccBar.style.width = `${occupancyRate}%`;
  if (elMaint) elMaint.textContent = maintRooms;
  if (elRev) elRev.textContent = `₹${totalRevenue.toLocaleString("en-IN")}`;
  if (elTotalRes) elTotalRes.textContent = totalRes;
  if (elConf) elConf.textContent = confirmedRes;
  if (elCanc) elCanc.textContent = cancelledRes;
  if (elPop) elPop.textContent = `${mostPop} (${maxCount > 0 ? maxCount : 0} stays)`;

  // Bar chart widths
  const maxBar = Math.max(1, countByType.Standard, countByType.Deluxe, countByType.Suite);
  const barStd = document.getElementById("chart-bar-standard");
  const barDel = document.getElementById("chart-bar-deluxe");
  const barSte = document.getElementById("chart-bar-suite");

  if (barStd) barStd.style.width = `${(countByType.Standard / maxBar) * 100}%`;
  if (barDel) barDel.style.width = `${(countByType.Deluxe / maxBar) * 100}%`;
  if (barSte) barSte.style.width = `${(countByType.Suite / maxBar) * 100}%`;

  const valStd = document.getElementById("chart-val-standard");
  const valDel = document.getElementById("chart-val-deluxe");
  const valSte = document.getElementById("chart-val-suite");

  if (valStd) valStd.textContent = countByType.Standard;
  if (valDel) valDel.textContent = countByType.Deluxe;
  if (valSte) valSte.textContent = countByType.Suite;

  // Welcome Dashboard updates
  const wdAvail = document.getElementById("wd-avail-rooms");
  const wdActive = document.getElementById("wd-active-bookings");
  const wdRev = document.getElementById("wd-total-revenue");
  const wdOcc = document.getElementById("wd-occupancy-rate");
  const wdStd = document.getElementById("wd-std-count");
  const wdDel = document.getElementById("wd-del-count");
  const wdSte = document.getElementById("wd-ste-count");

  if (wdAvail) wdAvail.textContent = `${availRooms} / ${totalRooms}`;
  if (wdActive) wdActive.textContent = confirmedRes;
  if (wdRev) wdRev.textContent = `₹${totalRevenue.toLocaleString("en-IN")}`;
  if (wdOcc) wdOcc.textContent = `${occupancyRate}%`;

  const availStd = appState.rooms.filter(r => r.type === "Standard" && r.status === "AVAILABLE").length;
  const availDel = appState.rooms.filter(r => r.type === "Deluxe" && r.status === "AVAILABLE").length;
  const availSte = appState.rooms.filter(r => r.type === "Suite" && r.status === "AVAILABLE").length;

  if (wdStd) wdStd.textContent = `${availStd} Rooms Available`;
  if (wdDel) wdDel.textContent = `${availDel} Rooms Available`;
  if (wdSte) wdSte.textContent = `${availSte} Rooms Available`;
}

// ------------------------------------------------------------- WELCOME DASHBOARD
function initWelcomeDashboard() {
  function updateClock() {
    const now = new Date();
    const clockEl = document.getElementById("welcome-live-clock");
    const greetEl = document.getElementById("welcome-time-greeting");

    if (clockEl) {
      const options = { weekday: 'short', month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit', second: '2-digit' };
      clockEl.textContent = now.toLocaleDateString('en-US', options);
    }

    if (greetEl) {
      const hour = now.getHours();
      let greeting = "WELCOME TO SMARTSTAY";
      if (hour < 12) greeting = "GOOD MORNING • WELCOME TO SMARTSTAY";
      else if (hour < 17) greeting = "GOOD AFTERNOON • WELCOME TO SMARTSTAY";
      else greeting = "GOOD EVENING • WELCOME TO SMARTSTAY";
      greetEl.textContent = greeting;
    }
  }

  updateClock();
  setInterval(updateClock, 1000);
}

// ------------------------------------------------------------- ADMIN OPERATIONS
function renderAdmin() {
  const roomsBody = document.getElementById("admin-rooms-tbody");
  const custBody = document.getElementById("admin-customers-tbody");
  const custCount = document.getElementById("admin-customer-count");

  if (custCount) custCount.textContent = `${appState.customers.length} Guests`;

  if (roomsBody) {
    roomsBody.innerHTML = appState.rooms.map(room => {
      const isMaint = room.status === "MAINTENANCE";
      const isBooked = room.status === "BOOKED";

      return `
        <tr>
          <td><strong>${room.roomNumber}</strong></td>
          <td>${room.type}</td>
          <td>Floor ${room.floor}</td>
          <td>₹${room.price.toLocaleString("en-IN")}</td>
          <td>
            <span class="stat-pill ${room.status === 'AVAILABLE' ? 'pill-available' : (isBooked ? 'pill-booked' : 'pill-cancelled')}">
              ${room.status}
            </span>
          </td>
          <td>
            ${!isBooked ? `
              <button class="btn btn-sm ${isMaint ? 'btn-primary' : 'btn-ghost'}" onclick="toggleRoomMaintenance(${room.roomNumber})">
                ${isMaint ? 'Make Available' : 'Set Maintenance'}
              </button>
            ` : `<span style="font-size: 0.75rem; color: var(--text-dim);">Currently Booked</span>`}
          </td>
        </tr>
      `;
    }).join("");
  }

  if (custBody) {
    custBody.innerHTML = appState.customers.length === 0 
      ? `<tr><td colspan="5" style="text-align: center; color: var(--text-dim); padding: 1.5rem;">No customer records yet.</td></tr>`
      : appState.customers.map(c => {
          const count = appState.bookings.filter(b => b.phone === c.phone && b.bookingStatus === "CONFIRMED").length;
          let tier = "Regular (0%)";
          if (count >= 5) tier = "Gold VIP (10%)";
          else if (count >= 2) tier = "Silver VIP (5%)";

          return `
            <tr>
              <td><code>${c.customerId}</code></td>
              <td><strong>${c.name}</strong></td>
              <td>${c.phone}</td>
              <td><span class="badge-gold">${tier}</span></td>
              <td>${count} Confirmed Stay(s)</td>
            </tr>
          `;
        }).join("");
  }
}

function toggleRoomMaintenance(roomNumber) {
  const room = appState.rooms.find(r => r.roomNumber === roomNumber);
  if (!room) return;

  room.status = room.status === "MAINTENANCE" ? "AVAILABLE" : "MAINTENANCE";
  saveState("rooms");
  renderRooms();
  renderStatistics();
  renderAdmin();
  showToast(`Room ${roomNumber} updated to ${room.status}.`, "success");
}

function openAddRoomModal() {
  const modal = document.getElementById("add-room-modal");
  if (modal) modal.classList.add("open");
}

function closeAddRoomModal() {
  const modal = document.getElementById("add-room-modal");
  if (modal) modal.classList.remove("open");
}

function handleAddRoomBackdropClick(e) {
  if (e.target.id === "add-room-modal") closeAddRoomModal();
}

function handleNewRoomTypeChange(type) {
  const floorInput = document.getElementById("new-room-floor");
  if (!floorInput) return;
  if (type === "Standard") floorInput.value = 1;
  else if (type === "Deluxe") floorInput.value = 2;
  else if (type === "Suite") floorInput.value = 3;
}

function executeAddRoom(e) {
  e.preventDefault();
  const type = document.getElementById("new-room-type").value;
  const roomNum = parseInt(document.getElementById("new-room-number").value);
  const floor = parseInt(document.getElementById("new-room-floor").value);

  if (appState.rooms.some(r => r.roomNumber === roomNum)) {
    alert(`Room ${roomNum} already exists! Please choose another number.`);
    return;
  }

  let price = 2000, maxGuests = 2, amenities = ["Wi-Fi", "TV", "AC"];
  if (type === "Deluxe") {
    price = 3500; maxGuests = 3; amenities = ["Wi-Fi", "TV", "AC", "Mini Bar"];
  } else if (type === "Suite") {
    price = 5000; maxGuests = 4; amenities = ["Wi-Fi", "TV", "AC", "Mini Bar", "Living Area"];
  }

  const newRoom = { roomNumber: roomNum, type, floor, price, maxGuests, amenities, status: "AVAILABLE" };
  appState.rooms.push(newRoom);
  appState.rooms.sort((a, b) => a.roomNumber - b.roomNumber);

  saveState("rooms");
  closeAddRoomModal();
  renderRooms();
  renderStatistics();
  renderAdmin();
  showToast(`Room ${roomNum} (${type}) added successfully!`, "success");
}

// ------------------------------------------------------------- UTILITIES
function showToast(message, type = "success") {
  const container = document.getElementById("toast-container");
  if (!container) return;

  const toast = document.createElement("div");
  toast.className = `toast toast-${type}`;
  toast.innerHTML = `<span>${type === 'success' ? '✓' : '⚠'}</span> <div>${message}</div>`;
  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = "0";
    toast.style.transform = "translateX(50px)";
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}

function scrollToSection(id) {
  const el = document.getElementById(id);
  if (el) el.scrollIntoView({ behavior: "smooth" });
}

function setupSmoothScrolling() {
  document.querySelectorAll(".nav-link").forEach(link => {
    link.addEventListener("click", e => {
      document.querySelectorAll(".nav-link").forEach(l => l.classList.remove("active"));
      link.classList.add("active");
    });
  });
}
