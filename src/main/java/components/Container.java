package components;

import baggage.Baggage;

import java.util.ArrayList;
import java.util.List;

public class Container {

    List<Baggage> baggageList = new ArrayList<Baggage>();
    public void store(Baggage baggage) {
        this.baggageList.add(baggage);
    }
}
