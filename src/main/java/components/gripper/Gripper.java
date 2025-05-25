// src/main/java/components/Gripper.java
package components.gripper;

import baggage.Baggage;
import baggage.BaggageStatus;
import baggage.IBaggageTracking;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class Gripper implements IGripper {
    private final IBaggageTracking controlModule;

    @Override
    public void gripBaggage(Baggage baggage) {
        log.info("Gripper: Gripping baggage {} from Gate Belt.", baggage.getBarcodeTag());
        Optional<Baggage> trackedBaggage = controlModule.getBaggage(baggage.getBarcodeTag());

        if (trackedBaggage.isPresent()) {
            // Simulate last read
            log.info("Gripper: Last read barcode for baggage {}", baggage.getBarcodeTag());
            controlModule.updateBaggageStatus(baggage.getBarcodeTag(), BaggageStatus.COMPLETED);
            controlModule.removeBaggage(baggage.getBarcodeTag());
            log.info("Gripper: Baggage {} successfully transferred to ground container and removed from system.", baggage.getBarcodeTag());
        } else {
            log.error("Gripper: Could not find baggage {} in system to complete processing.", baggage.getBarcodeTag());
        }
    }
}