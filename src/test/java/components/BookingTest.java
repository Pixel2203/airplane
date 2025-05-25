package components;

import org.junit.jupiter.api.Test;
import tag.Gate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingTest {

    @Test
    public void testBookingProperties() {
        // 1. Define expected values
        String expectedDestination = "New York";
        LocalDate expectedBookingDate = LocalDate.of(2024, Month.MARCH, 15);
        LocalDateTime expectedDepartureTime = LocalDateTime.of(2024, Month.APRIL, 1, 10, 30);
        Gate expectedGate = Gate.A;

        // 2. Create a Booking instance using the builder
        Booking booking = Booking.builder()
                .destination(expectedDestination)
                .bookingDate(expectedBookingDate)
                .departureTime(expectedDepartureTime)
                .departureGate(expectedGate)
                .build();

        // 3. Assert that the retrieved properties match the set values
        assertEquals(expectedDestination, booking.getDestination(), "Destination should match");
        assertEquals(expectedBookingDate, booking.getBookingDate(), "Booking date should match");
        assertEquals(expectedDepartureTime, booking.getDepartureTime(), "Departure time should match");
        assertEquals(expectedGate, booking.getDepartureGate(), "Departure gate should match");
    }
}
