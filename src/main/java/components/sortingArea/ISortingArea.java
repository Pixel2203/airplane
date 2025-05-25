// src/main/java/components/ISortingArea.java
package components.sortingArea;

import baggage.Baggage;

public interface ISortingArea {
    String sortBaggage(Baggage baggage); // Returns the name of the conveyor it's sorted to
}