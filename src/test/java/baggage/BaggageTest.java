package baggage;

import org.junit.jupiter.api.Test;
import tag.Tag;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BaggageTest {

    @Test
    public void testBaggageProperties() {
        // 1. Create a Tag instance
        String expectedDestination = "Gate A1";
        Tag testTag = Tag.builder()
                .destination(expectedDestination)
                .build();

        // 2. Create a Baggage instance
        BaggageStatus expectedStatus = BaggageStatus.CHECKED_IN;
        Baggage baggage = Baggage.builder()
                .tag(testTag)
                .status(expectedStatus)
                .build();

        // 3. Assert properties
        assertNotNull(baggage.getTag().getCode(), "Baggage ID (Tag code) should not be null");
        assertEquals(expectedDestination, baggage.getTag().getDestination(), "Baggage destination should match");
        assertEquals(expectedStatus, baggage.getStatus(), "Baggage status should match");
    }
}
