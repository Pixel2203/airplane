// src/main/java/components/ConveyorBelt.java
package components.conveyorBelt;

import baggage.Baggage;
import baggage.BaggageStatus;
import baggage.IBaggageTracking;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ConveyorBelt implements IConveyorBelt {
    private final String name;
    private final IBaggageTracking controlModule;

    @Override
    public void receiveBaggage(Baggage baggage) {
        log.info("Conveyor Belt {}: Box received {}", name, baggage.getBarcodeTag());
        controlModule.updateBaggageLocation(baggage.getBarcodeTag(), name);
        controlModule.updateBaggageStatus(baggage.getBarcodeTag(), BaggageStatus.INCOMING_CONVEYOR);
    }

    @Override
    public void transferBaggage(Baggage baggage, String nextComponentName) {
        log.info("Conveyor Belt {}: Transferring baggage {} to {}", name, baggage.getBarcodeTag(), nextComponentName);
        // In a real system, this would involve physical transfer and the next component receiving it.
        // For simulation, we just update the location.
        controlModule.updateBaggageLocation(baggage.getBarcodeTag(), nextComponentName);
    }
}