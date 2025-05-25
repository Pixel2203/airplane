// src/main/java/components/IConveyorBelt.java
package components.conveyorBelt;

import baggage.Baggage;

public interface IConveyorBelt {
    void receiveBaggage(Baggage baggage);
    void transferBaggage(Baggage baggage);
}