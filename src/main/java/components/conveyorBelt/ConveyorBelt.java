package components.conveyorBelt;

import baggage.Baggage;
import baggage.BaggageStatus;
import baggage.IBaggageTracking;
import components.IReceiveable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@Getter
public class ConveyorBelt implements IConveyorBelt {
    private final String name;
    private final IBaggageTracking controlModule;

    @Builder.Default
    private final BaggageStatus status = BaggageStatus.BAGGAGE_TAKEN;

    private final IReceiveable nextStation;
    @Override
    public void receiveBaggage(Baggage baggage) {
        log.info("Conveyor Belt {}: Box received {}", name, baggage.getTag().getCode());
        controlModule.updateBaggageStatus(baggage.getTag(), BaggageStatus.BAGGAGE_TAKEN);
        this.transferBaggage(baggage);
    }

    @Override
    public void transferBaggage(Baggage baggage) {
        nextStation.receive(baggage);
    }
}