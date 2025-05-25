// src/main/java/components/ScanningArea.java
package components.scanningArea;

import baggage.Baggage;
import baggage.BaggageStatus;
import baggage.IBaggageTracking;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class ScanningArea implements IScanningArea {
    private final IBaggageTracking controlModule;

    @Override
    public boolean scanBaggage(Baggage baggage) {
        log.info("Scanning Area: Scanning baggage {}", baggage.getBarcodeTag());
        Optional<Baggage> trackedBaggage = controlModule.getBaggage(baggage.getBarcodeTag());

        if (trackedBaggage.isPresent() && trackedBaggage.get().getDestination().equals(baggage.getDestination())) {
            controlModule.updateBaggageLocation(baggage.getBarcodeTag(), "First Scan Point");
            controlModule.updateBaggageStatus(baggage.getBarcodeTag(), BaggageStatus.FIRST_SCAN_POINT);
            log.info("Scanning Area: Baggage {} successfully scanned and matched.", baggage.getBarcodeTag());
            return true;
        } else {
            log.error("Scanning Area: Discrepancy found for baggage {}. Stopping belt.", baggage.getBarcodeTag());
            controlModule.updateBaggageStatus(baggage.getBarcodeTag(), BaggageStatus.ERROR);
            return false;
        }
    }
}