// src/main/java/components/SortingArea.java
package components.sortingArea;

import baggage.Baggage;
import baggage.BaggageStatus;
import baggage.IBaggageTracking;
import components.IReceiveable;
import components.conveyorBelt.ConveyorBelt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tag.Gate;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class SortingArea implements ISortingArea, IReceiveable {
    private final IBaggageTracking controlModule;

    private final Map<Gate, ConveyorBelt> destinationToConveyorMap;


    @Override
    public void sortBaggage(Baggage baggage) {
        Gate departueGate = baggage.getTag().getGate();
        ConveyorBelt targetConveyor = destinationToConveyorMap.get(departueGate);

        if (targetConveyor == null) {
            log.error("Sorting Area: No conveyor found for destination {}. Rerouting to error handling.", baggage.getTag().getGate());
            controlModule.updateBaggageStatus(baggage.getTag(), BaggageStatus.ERROR);

        }

        if (controlModule.isConveyorFull(targetConveyor.getName())) {
            log.warn("Sorting Area: Target conveyor {} is full for baggage {}. Rerouting to Intermediate Channel.", targetConveyor, baggage.getTag().getCode());
            ConveyorBelt alternativeBelt = destinationToConveyorMap.get(departueGate);
            controlModule.addBufferedBaggage(alternativeBelt, baggage);
            controlModule.updateBaggageStatus(baggage.getTag(), BaggageStatus.BUFFERED);

        } else {
            log.info("Sorting Area: Directing baggage {} to {}", baggage.getTag().getCode(), departueGate.toString());
            controlModule.updateBaggageStatus(baggage.getTag(), BaggageStatus.SORTED);
            targetConveyor.receiveBaggage(baggage);
        }
    }

    @Override
    public void receive(Baggage bag) {
        this.sortBaggage(bag);
    }
}