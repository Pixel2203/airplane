// src/main/java/components/ICheckInCounter.java
package components.checkInCounter;

import baggage.Baggage;
import components.Booking;

public interface ICheckInCounter {
    void checkIn(Baggage baggage, Booking booking);
}