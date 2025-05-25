package tag;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class Tag {

    @Builder.Default
    private String code = UUID.randomUUID().toString();
    private Gate gate;
    private String destination;


}
