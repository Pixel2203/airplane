// src/main/java/components/IGateBelt.java
package components.gateBelt;

import baggage.Baggage;

public interface IGateBelt {
    void receiveBaggage(Baggage baggage);
    void transferToGripper(Baggage baggage);
}