// src/main/java/components/GateBelt.java
package components.gateBelt;

import baggage.Baggage;
import baggage.IBaggageTracking;
import components.gripper.IGripper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class GateBelt implements IGateBelt {
    private final String name;
    private final IBaggageTracking controlModule;
    private final IGripper gripper;

    @Override
    public void receiveBaggage(Baggage baggage) {
        log.info("Gate Belt {}: Baggage {} received.", name, baggage.getBarcodeTag());
        controlModule.updateBaggageLocation(baggage.getBarcodeTag(), name);
    }

    @Override
    public void transferToGripper(Baggage baggage) {
        log.info("Gate Belt {}: Transferring baggage {} to gripper.", name, baggage.getBarcodeTag());
        gripper.gripBaggage(baggage);
    }
}