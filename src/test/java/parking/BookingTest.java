package parking;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class BookingTest {
    private static final LocalDateTime START = LocalDateTime.of(2026, 10, 1, 9, 0);
    private static final LocalDateTime END = START.plusHours(2);

    private Vehicle vehicle;
    private ParkingSlot slot;
    private Booking booking;

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle(7, VehicleType.CAR, 100.0);
        slot = new ParkingSlot("R-12", ParkingSlotType.REGULAR);
        booking = new Booking(42, vehicle, slot, START, END, 20.0);
    }

    @Test
    void constructorPreservesBookingDetailsAndStartsActive() {
        assertAll(
                () -> assertEquals(42, booking.getBookingId()),
                () -> assertSame(vehicle, booking.getVehicle()),
                () -> assertSame(slot, booking.getParkingSlot()),
                () -> assertEquals(START, booking.getStartTime()),
                () -> assertEquals(END, booking.getEndTime()),
                () -> assertEquals(20.0, booking.getAmount()),
                () -> assertEquals(BookingStatus.ACTIVE, booking.getBookingStatus()));
    }

    @Test
    void completeBookingChangesStatusToCompleted() {
        booking.completeBooking();
        assertEquals(BookingStatus.COMPLETED, booking.getBookingStatus());
    }

    @Test
    void cancelBookingChangesStatusToCancelled() {
        booking.cancelBooking();
        assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus());
    }

    @Test
    void repeatingCompleteBookingKeepsCompletedStatus() {
        booking.completeBooking();
        booking.completeBooking();
        assertEquals(BookingStatus.COMPLETED, booking.getBookingStatus());
    }

    @Test
    void repeatingCancelBookingKeepsCancelledStatus() {
        booking.cancelBooking();
        booking.cancelBooking();
        assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus());
    }

    @Test
    @Tag("known-defect")
    void completedBookingCannotBeCancelled() {
        booking.completeBooking();

        booking.cancelBooking();

        assertEquals(BookingStatus.COMPLETED, booking.getBookingStatus(),
                "A completed booking is terminal and must not become cancelled");
    }

    @Test
    @Tag("known-defect")
    void completedBookingRemainsCompletedAfterRepeatedCancellationAttempts() {
        booking.completeBooking();

        booking.cancelBooking();
        booking.cancelBooking();

        assertEquals(BookingStatus.COMPLETED, booking.getBookingStatus(),
                "Repeated cancellation attempts must not overwrite a completed booking");
    }

    @Test
    @Tag("known-defect")
    void cancelledBookingCannotBeCompleted() {
        booking.cancelBooking();

        booking.completeBooking();

        assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus(),
                "A cancelled booking is terminal and must not become completed");
    }

    @Test
    @Tag("known-defect")
    void cancelledBookingRemainsCancelledAfterRepeatedCompletionAttempts() {
        booking.cancelBooking();

        booking.completeBooking();
        booking.completeBooking();

        assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus(),
                "Repeated completion attempts must not overwrite a cancelled booking");
    }

    @Test
    void toStringIncludesBookingIdentityAmountAndStatus() {
        String description = booking.toString();
        assertAll(
                () -> assertTrue(description.contains("bookingId=42")),
                () -> assertTrue(description.contains("amount=20.0")),
                () -> assertTrue(description.contains("bookingStatus=ACTIVE")));
    }
}
