package components;

import lombok.Builder;
import lombok.Getter;
import tag.Gate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
public class Booking {
    private String destination;
    private LocalDate bookingDate;
    private LocalDateTime departureTime;
    private Gate departureGate;
}
