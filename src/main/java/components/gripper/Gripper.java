// src/main/java/components/Gripper.java
package components.gripper;

import baggage.Baggage;
import baggage.BaggageStatus;
import baggage.IBaggageTracking;
import components.Booking;
import components.Container;
import components.IReceiveable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tag.Tag;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class Gripper implements IGripper, IReceiveable {
    private final IBaggageTracking controlModule;
    private final Container container = new Container();
    @Override
    public void gripBaggage(Baggage baggage) {
        log.info("Gripper: Gripping baggage {} from Gate Belt.", baggage.getTag().getCode());
        log.info("Gripper: Last read barcode for baggage {}", baggage.getTag().getCode());
        if(!isBaggageTagValid(baggage)) {
            log.error("Gripper: Baggage tag is invalid {}.", baggage.getTag().getCode());
            return;
        }
        controlModule.updateBaggageStatus(baggage.getTag(), BaggageStatus.COMPLETED);
        controlModule.removeBaggage(baggage.getTag());
        log.info("Gripper: Baggage {} successfully transferred to ground container and removed from system.", baggage.getTag().getCode());
        container.store(baggage);

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
        this.gripBaggage(bag);
    }
}