package components.scanningArea;

import baggage.Baggage;
import baggage.BaggageStatus;
import baggage.IBaggageTracking;
import components.Booking;
import components.IReceiveable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tag.Gate;
import tag.Tag;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScanningAreaTest {

    @Mock
    private IBaggageTracking mockCentralControlModule;

    @Mock
    private IReceiveable mockNextStation;

    private ScanningArea scanningArea;

    @Captor
    private ArgumentCaptor<Tag> tagCaptor;

    @Captor
    private ArgumentCaptor<BaggageStatus> statusCaptor;

    @Captor
    private ArgumentCaptor<Baggage> baggageCaptor;

    @BeforeEach
    void setUp() {
        scanningArea = ScanningArea.builder()
                .controlModule(mockCentralControlModule)
                .nextStation(mockNextStation)
                .build();
    }

    @Test
    void testScanBaggage_Secure() {
        // 1. Arrange
        String expectedDestination = "Tokyo";
        Gate expectedGate = Gate.A;

        Tag testTag = Tag.builder().destination(expectedDestination).gate(expectedGate).build();
        Baggage testBaggage = Baggage.builder().tag(testTag).build();

        Booking matchingBooking = Booking.builder()
                .destination(expectedDestination)
                .departureGate(expectedGate)
                .bookingDate(LocalDate.now())
                .departureTime(LocalDateTime.now().plusHours(3))
                .build();

        // Make the scan secure by returning a matching booking
        when(mockCentralControlModule.getBookingByTag(testTag)).thenReturn(Optional.of(matchingBooking));

        // 2. Act
        scanningArea.receive(testBaggage);

        // 3. Assert
        // Verify status update to FIRST_SCAN_POINT
        verify(mockCentralControlModule).updateBaggageStatus(tagCaptor.capture(), statusCaptor.capture());
        assertSame(testTag, tagCaptor.getValue());
        assertEquals(BaggageStatus.FIRST_SCAN_POINT, statusCaptor.getValue());

        // Verify baggage is passed to the next station
        verify(mockNextStation).receive(baggageCaptor.capture());
        assertSame(testBaggage, baggageCaptor.getValue());
    }

    @Test
    void testScanBaggage_Suspicious() {
        // 1. Arrange
        Tag testTag = Tag.builder().destination("Unknown").gate(Gate.C).build();
        Baggage testBaggage = Baggage.builder().tag(testTag).build();

        // Make the scan suspicious by returning no booking for the tag
        when(mockCentralControlModule.getBookingByTag(testTag)).thenReturn(Optional.empty());

        // 2. Act
        scanningArea.receive(testBaggage);

        // 3. Assert
        // Verify status update to ERROR
        verify(mockCentralControlModule).updateBaggageStatus(tagCaptor.capture(), statusCaptor.capture());
        assertSame(testTag, tagCaptor.getValue());
        assertEquals(BaggageStatus.ERROR, statusCaptor.getValue());

        // Verify baggage is NOT passed to the next station
        verify(mockNextStation, never()).receive(any(Baggage.class));
    }
}
