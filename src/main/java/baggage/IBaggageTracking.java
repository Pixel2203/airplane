// src/main/java/baggage/IBaggageTracking.java
package baggage;

import components.Booking;
import components.conveyorBelt.ConveyorBelt;
import tag.Tag;

import java.util.Optional;

public interface IBaggageTracking {

    void removeBaggage(Tag tag);
    boolean isConveyorFull(String conveyorBeltName);
    void registerBaggage(Tag tag, Booking booking);
    void updateBaggageStatus(Tag tag, BaggageStatus newStatus);
    Optional<Booking> getBookingByTag(Tag tag);
    void addBufferedBaggage(ConveyorBelt conveyorBelt, Baggage baggage);
    Optional<Baggage> removeOldestBufferedBaggage(ConveyorBelt conveyorBelt);
    void setConveyorStatus(String conveyorBeltName, boolean isFull);
}