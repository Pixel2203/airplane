package components;// src/main/java/components.CentralControlModule.java

import baggage.Baggage;
import baggage.BaggageStatus;
import baggage.IBaggageTracking;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class CentralControlModule implements IBaggageTracking {
    private final Map<String, Baggage> baggageMap; // Key: barcodeTag, Value: Baggage object
    private final Map<String, Boolean> conveyorStatus; // Key: conveyorName, Value: isFull
    private final Map<String, Deque<String>> intermediateChannelBuffers; // Key: channelName, Value: Queue of barcodeTags

    public CentralControlModule() {
        this.baggageMap = new HashMap<>();
        this.conveyorStatus = new HashMap<>();
        this.intermediateChannelBuffers = new HashMap<>();
    }

    @Override
    public void registerBaggage(Baggage baggage) {
        baggageMap.put(baggage.getBarcodeTag(), baggage);
        log.debug("Control Module: Registered baggage {}", baggage.getBarcodeTag());
    }

    @Override
    public void updateBaggageLocation(String barcodeTag, String newLocation) {
        Optional.ofNullable(baggageMap.get(barcodeTag)).ifPresent(baggage -> {
            baggage.setCurrentLocation(newLocation);
            log.debug("Control Module: Updated baggage {} location to {}", barcodeTag, newLocation);
        });
    }

    @Override
    public void updateBaggageStatus(String barcodeTag, BaggageStatus newStatus) {
        Optional.ofNullable(baggageMap.get(barcodeTag)).ifPresent(baggage -> {
            baggage.setStatus(newStatus);
            log.debug("Control Module: Updated baggage {} status to {}", barcodeTag, newStatus);
        });
    }

    @Override
    public Optional<Baggage> getBaggage(String barcodeTag) {
        return Optional.ofNullable(baggageMap.get(barcodeTag));
    }

    @Override
    public void removeBaggage(String barcodeTag) {
        baggageMap.remove(barcodeTag);
        log.debug("Control Module: Removed baggage {} from system.", barcodeTag);
    }

    @Override
    public boolean isConveyorFull(String conveyorName) {
        return conveyorStatus.getOrDefault(conveyorName, false);
    }

    @Override
    public void setConveyorStatus(String conveyorName, boolean isFull) {
        conveyorStatus.put(conveyorName, isFull);
        log.info("Control Module: Conveyor {} status set to isFull={}", conveyorName, isFull);
    }

    @Override
    public void addBufferedBaggage(String intermediateChannelName, String barcodeTag) {
        intermediateChannelBuffers.computeIfAbsent(intermediateChannelName, k -> new ArrayDeque<>()).offer(barcodeTag);
        log.debug("Control Module: Added baggage {} to {} buffer.", barcodeTag, intermediateChannelName);
    }

    @Override
    public Optional<String> removeOldestBufferedBaggage(String intermediateChannelName) {
        Deque<String> buffer = intermediateChannelBuffers.get(intermediateChannelName);
        if (buffer != null && !buffer.isEmpty()) {
            String barcode = buffer.poll();
            log.debug("Control Module: Removed oldest baggage {} from {} buffer.", barcode, intermediateChannelName);
            return Optional.of(barcode);
        }
        return Optional.empty();
    }
}