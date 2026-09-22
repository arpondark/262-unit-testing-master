package parking;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ParkingSlotTest {
    private static final LocalDateTime REQUEST_START = LocalDateTime.of(2026, 10, 1, 10, 0);
    private static final LocalDateTime REQUEST_END = REQUEST_START.plusHours(2);

    private ParkingSlot slot;

    @BeforeEach
    void setUp() {
        slot = new ParkingSlot("R-12", ParkingSlotType.REGULAR);
    }

    @Test
    void constructorCreatesActiveSlotWithEmptyBookingsAndWallet() {
        assertAll(
                () -> assertEquals("R-12", slot.getSlotId()),
                () -> assertEquals(ParkingSlotType.REGULAR, slot.getSlotType()),
                () -> assertTrue(slot.isActive()),
                () -> assertNotNull(slot.getWallet()),
                () -> assertEquals(0.0, slot.getBalance()),
                () -> assertTrue(slot.getBookings().isEmpty()));
    }

    @Test
    void balanceReflectsFundsHeldBySlotWallet() {
        slot.getWallet().addFunds(35.0);

        assertEquals(35.0, slot.getBalance());
    }

    @ParameterizedTest(name = "{0} in {1} should be compatible: {2}")
    @MethodSource("compatibilityCases")
    void compatibilityFollowsVehicleAndSlotTypeMatrix(
            VehicleType vehicleType, ParkingSlotType slotType, boolean expected) {
        ParkingSlot candidate = new ParkingSlot("test-slot", slotType);

        assertEquals(expected, candidate.isCompatible(vehicleType, REQUEST_START, REQUEST_END));
    }

    private static Stream<Arguments> compatibilityCases() {
        return Stream.of(
                Arguments.of(VehicleType.MOTORCYCLE, ParkingSlotType.COMPACT, true),
                Arguments.of(VehicleType.MOTORCYCLE, ParkingSlotType.REGULAR, true),
                Arguments.of(VehicleType.MOTORCYCLE, ParkingSlotType.LARGE, true),
                Arguments.of(VehicleType.MOTORCYCLE, ParkingSlotType.HANDICAPPED, false),
                Arguments.of(VehicleType.CAR, ParkingSlotType.COMPACT, false),
                Arguments.of(VehicleType.CAR, ParkingSlotType.REGULAR, true),
                Arguments.of(VehicleType.CAR, ParkingSlotType.LARGE, true),
                Arguments.of(VehicleType.CAR, ParkingSlotType.HANDICAPPED, false),
                Arguments.of(VehicleType.BUS, ParkingSlotType.COMPACT, false),
                Arguments.of(VehicleType.BUS, ParkingSlotType.REGULAR, false),
                Arguments.of(VehicleType.BUS, ParkingSlotType.LARGE, true),
                Arguments.of(VehicleType.BUS, ParkingSlotType.HANDICAPPED, false),
                Arguments.of(VehicleType.BICYCLE, ParkingSlotType.COMPACT, true),
                Arguments.of(VehicleType.BICYCLE, ParkingSlotType.REGULAR, true),
                Arguments.of(VehicleType.BICYCLE, ParkingSlotType.LARGE, true),
                Arguments.of(VehicleType.BICYCLE, ParkingSlotType.HANDICAPPED, true),
                Arguments.of(VehicleType.MICROCAR, ParkingSlotType.COMPACT, true),
                Arguments.of(VehicleType.MICROCAR, ParkingSlotType.REGULAR, true),
                Arguments.of(VehicleType.MICROCAR, ParkingSlotType.LARGE, false),
                Arguments.of(VehicleType.MICROCAR, ParkingSlotType.HANDICAPPED, false),
                Arguments.of(VehicleType.TRUCK, ParkingSlotType.COMPACT, false),
                Arguments.of(VehicleType.TRUCK, ParkingSlotType.REGULAR, false),
                Arguments.of(VehicleType.TRUCK, ParkingSlotType.LARGE, false),
                Arguments.of(VehicleType.TRUCK, ParkingSlotType.HANDICAPPED, false));
    }

    @Test
    void inactiveSlotIsIncompatibleEvenWhenTypeAndTimeWouldOtherwiseMatch() {
        slot.deactivate();

        assertAll(
                () -> assertFalse(slot.isActive()),
                () -> assertFalse(slot.isCompatible(VehicleType.CAR, REQUEST_START, REQUEST_END)));
    }

    @Test
    void activateMakesADeactivatedSlotCompatibleAgain() {
        slot.deactivate();
        slot.activate();

        assertAll(
                () -> assertTrue(slot.isActive()),
                () -> assertTrue(slot.isCompatible(VehicleType.CAR, REQUEST_START, REQUEST_END)));
    }

    @Test
    void slotWithoutBookingsIsAvailable() {
        assertTrue(slot.isAvailable(REQUEST_START, REQUEST_END));
    }

    @ParameterizedTest(name = "existing booking from {0} to {1} overlaps the request")
    @MethodSource("overlappingBookingCases")
    void overlappingBookingMakesSlotUnavailable(LocalDateTime bookingStart, LocalDateTime bookingEnd) {
        addBooking(bookingStart, bookingEnd);

        assertAll(
                () -> assertFalse(slot.isAvailable(REQUEST_START, REQUEST_END)),
                () -> assertFalse(slot.isCompatible(VehicleType.CAR, REQUEST_START, REQUEST_END)));
    }

    @ParameterizedTest(name = "an overlapping booking blocks {0} from an otherwise compatible {1} slot")
    @MethodSource("compatibleVehicleAndSlotCases")
    void overlappingBookingMakesEveryCompatibleVehicleTypeIncompatible(
            VehicleType vehicleType, ParkingSlotType slotType) {
        ParkingSlot candidate = new ParkingSlot("occupied-slot", slotType);
        Vehicle existingVehicle = new Vehicle(99, VehicleType.CAR, 100.0);
        candidate.getBookings().add(
                new Booking(1, existingVehicle, candidate, REQUEST_START, REQUEST_END, 20.0));

        assertFalse(candidate.isCompatible(vehicleType, REQUEST_START, REQUEST_END));
    }

    private static Stream<Arguments> compatibleVehicleAndSlotCases() {
        return Stream.of(
                Arguments.of(VehicleType.MOTORCYCLE, ParkingSlotType.COMPACT),
                Arguments.of(VehicleType.CAR, ParkingSlotType.REGULAR),
                Arguments.of(VehicleType.BUS, ParkingSlotType.LARGE),
                Arguments.of(VehicleType.BICYCLE, ParkingSlotType.HANDICAPPED),
                Arguments.of(VehicleType.MICROCAR, ParkingSlotType.COMPACT));
    }

    private static Stream<Arguments> overlappingBookingCases() {
        return Stream.of(
                Arguments.of(REQUEST_START.minusHours(1), REQUEST_START.plusHours(1)),
                Arguments.of(REQUEST_END.minusHours(1), REQUEST_END.plusHours(1)),
                Arguments.of(REQUEST_START.plusMinutes(30), REQUEST_END.minusMinutes(30)),
                Arguments.of(REQUEST_START.minusHours(1), REQUEST_END.plusHours(1)),
                Arguments.of(REQUEST_START, REQUEST_END));
    }

    @Test
    void bookingEndingAtRequestedStartDoesNotOverlap() {
        addBooking(REQUEST_START.minusHours(2), REQUEST_START);

        assertTrue(slot.isAvailable(REQUEST_START, REQUEST_END));
    }

    @Test
    void bookingStartingAtRequestedEndDoesNotOverlap() {
        addBooking(REQUEST_END, REQUEST_END.plusHours(2));

        assertTrue(slot.isAvailable(REQUEST_START, REQUEST_END));
    }

    @Test
    void availabilityChecksEveryStoredBooking() {
        addBooking(REQUEST_START.minusHours(3), REQUEST_START.minusHours(1));
        addBooking(REQUEST_START.plusMinutes(15), REQUEST_START.plusMinutes(45));

        assertFalse(slot.isAvailable(REQUEST_START, REQUEST_END));
    }

    private void addBooking(LocalDateTime start, LocalDateTime end) {
        Vehicle vehicle = new Vehicle(1, VehicleType.CAR, 100.0);
        slot.getBookings().add(new Booking(1, vehicle, slot, start, end, 20.0));
    }
}
