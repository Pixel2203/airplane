// src/main/java/baggage/IBaggageTracking.java
package baggage;

import java.util.Optional;

public interface IBaggageTracking {
    void registerBaggage(Baggage baggage);
    void updateBaggageLocation(String barcodeTag, String newLocation);
    void updateBaggageStatus(String barcodeTag, BaggageStatus newStatus);
    Optional<Baggage> getBaggage(String barcodeTag);
    void removeBaggage(String barcodeTag);
    boolean isConveyorFull(String conveyorName);
    void setConveyorStatus(String conveyorName, boolean isFull);
    void addBufferedBaggage(String intermediateChannelName, String barcodeTag);
    Optional<String> removeOldestBufferedBaggage(String intermediateChannelName);
}