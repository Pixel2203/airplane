// src/main/java/components/CheckInCounter.java
package components.checkInCounter;

import baggage.Baggage;
import baggage.IBaggageTracking;
import components.Booking;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tag.Gate;
import tag.Tag;

@Slf4j
@AllArgsConstructor
public class CheckInCounter implements ICheckInCounter {
    private final IBaggageTracking controlModule;

    @Override
    public void checkIn(Baggage baggage, Booking booking) {
        Tag tag = Tag.builder()
                .gate(booking.getDepartureGate())
                .destination(booking.getDestination())
                .build();

        baggage.setTag(tag);
        controlModule.registerBaggage(tag, booking);
        log.info("Baggage checked in at Check-in Counter, destination: {}", tag.getDestination());
    }
}