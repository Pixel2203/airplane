// src/main/java/components/CheckInCounter.java
package components.checkInCounter;

import baggage.Baggage;
import baggage.BaggageStatus;
import baggage.IBaggageTracking;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tag.Gate;
import tag.Tag;

@Slf4j
@AllArgsConstructor
public class CheckInCounter implements ICheckInCounter {
    private final IBaggageTracking controlModule;

    @Override
    public Baggage checkInBaggage(Gate destination) {
        Tag tag = Tag.builder().destination(destination).build();

        Baggage baggage = Baggage.builder()
                .tag(tag)
                .currentLocation("Check-in Counter")
                .status(BaggageStatus.CHECKED_IN)
                .build();
        controlModule.registerBaggage(baggage);
        log.info("Baggage {} checked in at Check-in Counter, destination: {}", barcodeTag, destination);
        return baggage;
    }
}