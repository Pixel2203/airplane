package tag;
import lombok.Builder;

import java.util.UUID;

@Builder
public class Tag {

    @Builder.Default
    private String code = UUID.randomUUID().toString();
    private Gate destination;


}
