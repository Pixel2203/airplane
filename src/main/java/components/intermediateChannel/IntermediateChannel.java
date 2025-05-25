// src/main/java/components/IntermediateChannel.java
package components.intermediateChannel;

import baggage.Baggage;
import baggage.BaggageStatus;
import baggage.IBaggageTracking;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class IntermediateChannel implements IIntermediateChannel {
    private final String name;
    private final IBaggageTracking controlModule;

    @Override
    public void bufferBaggage(Baggage baggage) {
        log.info("Intermediate Channel {}: Buffering baggage {}", name, baggage.getBarcodeTag());
        controlModule.addBufferedBaggage(name, baggage.getBarcodeTag());
        controlModule.updateBaggageLocation(baggage.getBarcodeTag(), name);
        controlModule.updateBaggageStatus(baggage.getBarcodeTag(), BaggageStatus.BUFFERED);
    }

    @Override
    public void releaseOldestBaggage(String targetConveyorName) {
        if (!controlModule.isConveyorFull(targetConveyorName)) {
            Optional<String> oldestBarcode = controlModule.removeOldestBufferedBaggage(name);
            if (oldestBarcode.isPresent()) {
                String barcode = oldestBarcode.get();
                controlModule.updateBaggageLocation(barcode, targetConveyorName);
                controlModule.updateBaggageStatus(barcode, BaggageStatus.SORTED); // Back to sorted for transfer
                log.info("Intermediate Channel {}: Released baggage {} to {}", name, barcode, targetConveyorName);
            } else {
                log.debug("Intermediate Channel {}: No baggage to release.", name);
            }
        } else {
            log.debug("Intermediate Channel {}: Target conveyor {} is still full. Cannot release baggage.", name, targetConveyorName);
        }
    }
}