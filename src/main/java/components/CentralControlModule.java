package components;// src/main/java/components.CentralControlModule.java

import baggage.Baggage;
import baggage.BaggageStatus;
import baggage.IBaggageTracking;
import components.conveyorBelt.ConveyorBelt;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import tag.Tag;

import java.util.*;

@Slf4j
@Builder
public class CentralControlModule implements IBaggageTracking {

    @Builder.Default
    private final Map<String, BaggageStatus> baggageStatusMap = new HashMap<>();
    @Builder.Default
    private final Map<String , Boolean> conveyorStatus = new HashMap<>(); // Key: conveyorName, Value: isFull
    @Builder.Default
    private final Map<ConveyorBelt, Deque<Baggage>> channelBuffer = new HashMap<>(); // Key: channelName, Value: Queue of barcodeTags
    @Builder.Default
    private final Map<String, Booking> checkedInBookings = new HashMap<>();

    @Override
    public void registerBaggage(Tag tag, Booking booking) {
        baggageStatusMap.put(tag.getCode() , BaggageStatus.CHECKED_IN);
        log.debug("Control Module: Registered baggage {}", tag.getCode());
        checkedInBookings.put(tag.getCode(), booking);
    }

    @Override
    public void updateBaggageStatus(Tag tag, BaggageStatus newStatus) {
        if(baggageStatusMap.containsKey(tag.getCode())) {
            baggageStatusMap.put(tag.getCode(), newStatus);
            log.info("Control Module: Updating baggage {}", tag.getCode());
        }
    }

    @Override
    public Optional<Booking> getBookingByTag(Tag tag) {
        return Optional.of(this.checkedInBookings.get(tag.getCode()));
    }

    @Override
    public void removeBaggage(Tag tag) {
        baggageStatusMap.remove(tag.getCode());
    }

    @Override
    public boolean isConveyorFull(String conveyorBeltName) {
        if(this.conveyorStatus.containsKey(conveyorBeltName)) {
            return this.conveyorStatus.get(conveyorBeltName);
        };
        return false;
    }

    @Override
    public void setConveyorStatus(String conveyorBeltName, boolean isFull) {
        conveyorStatus.put(conveyorBeltName, isFull);
        log.info("Control Module: Conveyor status set to isFull={}", isFull);
    }

    @Override
    public void addBufferedBaggage(ConveyorBelt belt, Baggage baggage) {
        Deque<Baggage> list = this.channelBuffer.get(belt);
        if(list == null) {
            list = new ArrayDeque<>();
        }
        list.offer(baggage);
        this.channelBuffer.put(belt, list);
        log.debug("Control Module: Added baggage {} to buffer.", baggage.getTag().getCode());
    }

    @Override
    public Optional<Baggage> removeOldestBufferedBaggage(ConveyorBelt conveyorBelt) {
        Deque<Baggage> list = this.channelBuffer.get(conveyorBelt);

        if(list.isEmpty()) return Optional.empty();
        return Optional.of(list.poll());
    }
}