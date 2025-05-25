// src/main/java/components/SortingArea.java
package components.sortingArea;

import baggage.Baggage;
import baggage.BaggageStatus;
import baggage.IBaggageTracking;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@AllArgsConstructor
public class SortingArea implements ISortingArea {
    private final IBaggageTracking controlModule;
    private final Map<String, String> destinationToConveyorMap; // e.g., {"JFK": "OutputConveyor1"}

    @Override
    public String sortBaggage(Baggage baggage) {
        String destination = baggage.getDestination();
        String targetConveyor = destinationToConveyorMap.get(destination);

        if (targetConveyor == null) {
            log.error("Sorting Area: No conveyor found for destination {}. Rerouting to error handling.", destination);
            controlModule.updateBaggageStatus(baggage.getBarcodeTag(), BaggageStatus.ERROR);
            return null;
        }

        if (controlModule.isConveyorFull(targetConveyor)) {
            log.warn("Sorting Area: Target conveyor {} is full for baggage {}. Rerouting to Intermediate Channel.", targetConveyor, baggage.getBarcodeTag());
            controlModule.addBufferedBaggage("IntermediateChannel", baggage.getBarcodeTag());
            controlModule.updateBaggageLocation(baggage.getBarcodeTag(), "Intermediate Channel");
            controlModule.updateBaggageStatus(baggage.getBarcodeTag(), BaggageStatus.BUFFERED);
            return "Intermediate Channel";
        } else {
            log.info("Sorting Area: Directing baggage {} to {}", baggage.getBarcodeTag(), targetConveyor);
            controlModule.updateBaggageLocation(baggage.getBarcodeTag(), targetConveyor);
            controlModule.updateBaggageStatus(baggage.getBarcodeTag(), BaggageStatus.SORTED);
            return targetConveyor;
        }
    }
}