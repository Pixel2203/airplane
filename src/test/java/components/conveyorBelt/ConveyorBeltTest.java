package components.conveyorBelt;

import baggage.Baggage;
import baggage.BaggageStatus;
import baggage.IBaggageTracking;
import components.IReceiveable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tag.Gate;
import tag.Tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ConveyorBeltTest {

    @Mock
    private IBaggageTracking mockControlModule;

    @Mock
    private IReceiveable mockNextStation;

    @Captor
    private ArgumentCaptor<Tag> tagCaptor;

    @Captor
    private ArgumentCaptor<BaggageStatus> statusCaptor;

    @Captor
    private ArgumentCaptor<Baggage> baggageCaptor;

    @Test
    public void testReceiveBaggage() {
        // 1. Arrange
        String conveyorBeltName = "MainConveyor";
        ConveyorBelt conveyorBelt = ConveyorBelt.builder()
                .name(conveyorBeltName)
                .controlModule(mockControlModule)
                .nextStation(mockNextStation)
                .build(); // status field will default to BAGGAGE_TAKEN

        Tag testTag = Tag.builder()
                .destination("Paris")
                .gate(Gate.C)
                .build();
        Baggage testBaggage = Baggage.builder()
                .tag(testTag)
                .build();

        // 2. Act
        conveyorBelt.receiveBaggage(testBaggage);

        // 3. Assert
        // Verify controlModule.updateBaggageStatus() call
        verify(mockControlModule).updateBaggageStatus(tagCaptor.capture(), statusCaptor.capture());
        assertSame(testTag, tagCaptor.getValue(), "The tag passed to updateBaggageStatus should be the testBaggage's tag.");
        assertEquals(BaggageStatus.BAGGAGE_TAKEN, statusCaptor.getValue(), "The status passed to updateBaggageStatus should be BAGGAGE_TAKEN.");

        // Verify nextStation.receive() call (as per IReceiveable interface)
        verify(mockNextStation).receive(baggageCaptor.capture());
        assertSame(testBaggage, baggageCaptor.getValue(), "The baggage passed to the next station should be the testBaggage.");
    }
}
