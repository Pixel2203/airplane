// src/main/java/components/ICheckInCounter.java
package components.checkInCounter;

import baggage.Baggage;

public interface ICheckInCounter {
    Baggage checkInBaggage(String barcodeTag, String destination);
}