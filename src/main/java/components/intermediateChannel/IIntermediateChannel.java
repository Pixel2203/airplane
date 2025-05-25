// src/main/java/components/IIntermediateChannel.java
package components.intermediateChannel;

import baggage.Baggage;

public interface IIntermediateChannel {
    void bufferBaggage(Baggage baggage);
    void releaseOldestBaggage(String targetConveyorName);
}