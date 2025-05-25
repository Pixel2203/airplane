// src/main/java/baggage/Baggage.java
package baggage;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tag.Tag;

@Getter
@Setter
@Builder
@ToString
public class Baggage {
    private Tag tag;
    private BaggageStatus status;
}