package components.checkInCounter;

import baggage.Baggage;
import baggage.BaggageStatus; // Not directly used for assertion on Baggage object, but good for context
import baggage.IBaggageTracking;
import components.Booking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tag.Gate;
import tag.Tag;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckInCounterTest {

    @Mock
    private IBaggageTracking mockControlModule; // Mocking the interface

    @Mock
    private Booking mockBooking;

    @InjectMocks
    private CheckInCounter checkInCounter; // Injects mockControlModule

    @Captor
    private ArgumentCaptor<Tag> tagCaptor;

    @Captor
    private ArgumentCaptor<Booking> bookingCaptor;

    @Test
    public void testCheckInProcess() {
        // 1. Arrange
        // Create a real Baggage object. Its initial status can be null or any other.
        // The CheckInCounter doesn't set the status on this object directly.
        Baggage baggage = Baggage.builder().build(); 

        String expectedDestination = "London";
        Gate expectedGate = Gate.B;

        // Configure the mockBooking
        when(mockBooking.getDestination()).thenReturn(expectedDestination);
        when(mockBooking.getDepartureGate()).thenReturn(expectedGate);

        // 2. Act
        checkInCounter.checkIn(baggage, mockBooking);

        // 3. Assert
        // Verify that the baggage's tag was set by the checkIn method
        assertNotNull(baggage.getTag(), "Baggage tag should be set after check-in.");
        assertEquals(expectedDestination, baggage.getTag().getDestination(), "Tag destination should match booking destination.");
        assertEquals(expectedGate, baggage.getTag().getGate(), "Tag gate should match booking departure gate.");
        assertNotNull(baggage.getTag().getCode(), "Tag code should be generated.");

        // Verify that controlModule.registerBaggage was called correctly
        // The actual status update to CHECKED_IN happens inside the real CentralControlModule's registerBaggage method.
        // Here, we verify the interaction with the mock.
        verify(mockControlModule).registerBaggage(tagCaptor.capture(), bookingCaptor.capture());
        
        Tag capturedTag = tagCaptor.getValue();
        Booking capturedBooking = bookingCaptor.getValue();

        // Check the captured Tag's properties
        assertEquals(expectedDestination, capturedTag.getDestination(), "Captured tag destination is incorrect.");
        assertEquals(expectedGate, capturedTag.getGate(), "Captured tag gate is incorrect.");
        assertNotNull(capturedTag.getCode(), "Captured tag code should not be null.");
        // Ensure the tag passed to registerBaggage is the same instance that was set on the baggage object
        assertSame(baggage.getTag(), capturedTag, "The tag set on baggage and the tag passed to registerBaggage should be the same.");

        // Ensure the booking passed to registerBaggage is the same mockBooking instance
        assertSame(mockBooking, capturedBooking, "The booking passed to registerBaggage should be the same mocked instance.");
    }
}
