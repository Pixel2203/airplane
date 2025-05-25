// src/main/java/components/ScanningArea.java
package components.scanningArea;

import baggage.Baggage;
import baggage.BaggageStatus;
import baggage.IBaggageTracking;
import components.Booking;
import components.IReceiveable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import tag.Tag;

@Slf4j
@Builder
public class ScanningArea implements IScanningArea, IReceiveable {
    private final IBaggageTracking controlModule;
    private final IReceiveable nextStation;


    @Override
    public boolean scanBaggage(Baggage baggage) {
        log.info("Scanning Area: Scanning baggage {}", baggage.getTag().getCode());

        if (isBaggageTagValid(baggage)) {
            controlModule.updateBaggageStatus(baggage.getTag(), BaggageStatus.FIRST_SCAN_POINT);
            log.info("Scanning Area: Baggage {} successfully scanned and matched.", baggage.getTag().getCode());
            return true;
        } else {
            log.error("Scanning Area: Discrepancy found for baggage {}. Stopping belt.", baggage.getTag().getCode());
            controlModule.updateBaggageStatus(baggage.getTag(), BaggageStatus.ERROR);
            return false;
        }
    }

    private boolean isBaggageTagValid(Baggage baggage) {
        Tag tag = baggage.getTag();
        var foundBookingForTag = controlModule.getBookingByTag(tag);
        if(foundBookingForTag.isEmpty()) return false;

        Booking booking = foundBookingForTag.get();

        return booking.getDepartureGate() == tag.getGate() && booking.getDestination().equals(tag.getDestination());

    }

    @Override
    public void receive(Baggage bag) {
        if(this.scanBaggage(bag)) {
            nextStation.receive(bag);
        }
    }
}