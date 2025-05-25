package components.sortingArea;

import baggage.Baggage;
import baggage.BaggageStatus;
import baggage.IBaggageTracking;
import components.conveyorBelt.ConveyorBelt; // Mock ConveyorBelt directly
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tag.Gate;
import tag.Tag;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SortingAreaTest {

    @Mock
    private IBaggageTracking mockCentralControlModule;

    @Mock
    private ConveyorBelt mockConveyorGateA; // Mocking ConveyorBelt directly

    @Mock
    private ConveyorBelt mockConveyorGateB;

    @Mock
    private ConveyorBelt mockConveyorGateC;

    private SortingArea sortingArea;

    private Map<Gate, ConveyorBelt> gateBeltMap;

    @Captor
    private ArgumentCaptor<Tag> tagCaptor;

    @Captor
    private ArgumentCaptor<BaggageStatus> statusCaptor;

    @Captor
    private ArgumentCaptor<Baggage> baggageCaptor;

    @BeforeEach
    void setUp() {
        gateBeltMap = new HashMap<>();
        gateBeltMap.put(Gate.A, mockConveyorGateA);
        gateBeltMap.put(Gate.B, mockConveyorGateB);
        gateBeltMap.put(Gate.C, mockConveyorGateC);

        // Configure mock conveyor belts to return names, needed for isConveyorFull check
        when(mockConveyorGateA.getName()).thenReturn("ConveyorA");
        when(mockConveyorGateB.getName()).thenReturn("ConveyorB");
        when(mockConveyorGateC.getName()).thenReturn("ConveyorC");

        sortingArea = new SortingArea(mockCentralControlModule, gateBeltMap);
    }

    @Test
    void testSortBaggageToCorrectGate_GateA_NotFull() {
        // 1. Arrange
        Gate targetGate = Gate.A;
        Tag testTag = Tag.builder().gate(targetGate).destination("FlightToA").build();
        Baggage testBaggage = Baggage.builder().tag(testTag).build();

        // Assume conveyor for Gate A is not full
        when(mockCentralControlModule.isConveyorFull(mockConveyorGateA.getName())).thenReturn(false);

        // 2. Act
        sortingArea.receive(testBaggage);

        // 3. Assert
        // Verify status update to SORTED
        verify(mockCentralControlModule).updateBaggageStatus(tagCaptor.capture(), statusCaptor.capture());
        assertSame(testTag, tagCaptor.getValue(), "Tag in updateBaggageStatus should be the testBaggage's tag.");
        assertEquals(BaggageStatus.SORTED, statusCaptor.getValue(), "Baggage status should be updated to SORTED.");

        // Verify baggage is passed to the correct conveyor belt (Gate A)
        verify(mockConveyorGateA).receiveBaggage(baggageCaptor.capture());
        assertSame(testBaggage, baggageCaptor.getValue(), "Baggage passed to Gate A conveyor should be the testBaggage.");

        // Verify baggage is NOT passed to other conveyor belts
        verify(mockConveyorGateB, never()).receiveBaggage(any(Baggage.class));
        verify(mockConveyorGateC, never()).receiveBaggage(any(Baggage.class));

        // Verify isConveyorFull was checked for the target conveyor
        verify(mockCentralControlModule).isConveyorFull(mockConveyorGateA.getName());
    }

    @Test
    void testSortBaggageToCorrectGate_GateB_WhenFull_ShouldBuffer() {
        // 1. Arrange
        Gate targetGate = Gate.B;
        Tag testTag = Tag.builder().gate(targetGate).destination("FlightToB").build();
        Baggage testBaggage = Baggage.builder().tag(testTag).build();

        // Assume conveyor for Gate B is full
        when(mockCentralControlModule.isConveyorFull(mockConveyorGateB.getName())).thenReturn(true);

        // 2. Act
        sortingArea.receive(testBaggage);

        // 3. Assert
        // Verify status update to BUFFERED
        verify(mockCentralControlModule).updateBaggageStatus(tagCaptor.capture(), statusCaptor.capture());
        assertSame(testTag, tagCaptor.getValue());
        assertEquals(BaggageStatus.BUFFERED, statusCaptor.getValue());

        // Verify addBufferedBaggage was called for the correct conveyor belt (Gate B)
        // The production code uses destinationToConveyorMap.get(departueGate) again for alternativeBelt
        verify(mockCentralControlModule).addBufferedBaggage(eq(mockConveyorGateB), baggageCaptor.capture());
        assertSame(testBaggage, baggageCaptor.getValue());

        // Verify baggage is NOT passed directly to any conveyor belt's receiveBaggage method
        verify(mockConveyorGateA, never()).receiveBaggage(any(Baggage.class));
        verify(mockConveyorGateB, never()).receiveBaggage(any(Baggage.class));
        verify(mockConveyorGateC, never()).receiveBaggage(any(Baggage.class));

        // Verify isConveyorFull was checked
        verify(mockCentralControlModule).isConveyorFull(mockConveyorGateB.getName());
    }

    @Test
    void testSortBaggage_NoConveyorForGate_ShouldError() {
        // 1. Arrange
        Gate unknownGate = Gate.valueOf("A"); // A valid gate but we'll remove its conveyor
        // Create a tag that references a gate not in our map for this test
        Tag testTag = Tag.builder().gate(unknownGate).destination("FlightToNowhere").build();
        Baggage testBaggage = Baggage.builder().tag(testTag).build();

        // Remove Gate A's conveyor for this specific test case
        gateBeltMap.remove(Gate.A);
        SortingArea sortingAreaWithoutGateA = new SortingArea(mockCentralControlModule, gateBeltMap);


        // 2. Act
        sortingAreaWithoutGateA.receive(testBaggage);

        // 3. Assert
        // Verify status update to ERROR
        verify(mockCentralControlModule).updateBaggageStatus(tagCaptor.capture(), statusCaptor.capture());
        assertSame(testTag, tagCaptor.getValue());
        assertEquals(BaggageStatus.ERROR, statusCaptor.getValue());

        // Verify baggage is NOT passed to any conveyor belt
        verify(mockConveyorGateA, never()).receiveBaggage(any(Baggage.class)); // Gate A conveyor was removed
        verify(mockConveyorGateB, never()).receiveBaggage(any(Baggage.class));
        verify(mockConveyorGateC, never()).receiveBaggage(any(Baggage.class));

        // Verify no buffering attempt
        verify(mockCentralControlModule, never()).addBufferedBaggage(any(ConveyorBelt.class), any(Baggage.class));
        // Verify no isConveyorFull check if no target conveyor
        verify(mockCentralControlModule, never()).isConveyorFull(anyString());
    }
}
