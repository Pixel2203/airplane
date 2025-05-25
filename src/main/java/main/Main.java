
package main;

import baggage.Baggage;
import baggage.BaggageStatus;
import components.Booking;
import components.CentralControlModule;
import components.IReceiveable;
import components.checkInCounter.CheckInCounter;
import components.conveyorBelt.ConveyorBelt;
import components.conveyorBelt.IConveyorBelt;
import components.gripper.Gripper;
import components.scanningArea.ScanningArea;
import components.sortingArea.SortingArea;
import tag.Gate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        CentralControlModule controlModule = CentralControlModule.builder().build();
        CheckInCounter checkInCounter = new CheckInCounter(controlModule);

        // Station after ConveyorBelt

        Gripper gripperA = new Gripper(controlModule);
        Gripper gripperB = new Gripper(controlModule);
        Gripper gripperC = new Gripper(controlModule);

        ConveyorBelt gateABelt = ConveyorBelt.builder().name("gate A belt").nextStation(gripperA).status(BaggageStatus.GATE_A_CONVEYOR).controlModule(controlModule).build();
        ConveyorBelt gateBBelt = ConveyorBelt.builder().name("gate B belt").nextStation(gripperB).status(BaggageStatus.GATE_B_CONVEYOR).controlModule(controlModule).build();
        ConveyorBelt gateCBelt = ConveyorBelt.builder().name("gate C belt").nextStation(gripperC).status(BaggageStatus.GATE_C_CONVEYOR).controlModule(controlModule).build();
        Map<Gate, ConveyorBelt> gateBeltMap = new HashMap<>();
        gateBeltMap.put(Gate.A, gateABelt);
        gateBeltMap.put(Gate.B, gateBBelt);
        gateBeltMap.put(Gate.C, gateCBelt);
        SortingArea sortingArea = new SortingArea(controlModule, gateBeltMap);
        ScanningArea scanningArea = ScanningArea.builder().controlModule(controlModule).nextStation(sortingArea).build();

        IConveyorBelt conveyorBeltToScanningArea = ConveyorBelt.builder()
                .nextStation(scanningArea)
                .name("Entry Conveyor Belt")
                .controlModule(controlModule)
                .status(BaggageStatus.BAGGAGE_TAKEN)
                .build();



        Booking[] bookings = {
                Booking.builder()
                        .bookingDate(LocalDate.now().minusDays(2))
                        .destination("Berlin")
                        .departureGate(Gate.B)
                        .departureTime(LocalDateTime.now().plusHours(5))
                        .build(),

                Booking.builder()
                        .bookingDate(LocalDate.now().minusWeeks(1))
                        .destination("Munich")
                        .departureGate(Gate.C)
                        .departureTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(30))
                        .build(),

                Booking.builder()
                        .bookingDate(LocalDate.now())
                        .destination("Hamburg")
                        .departureGate(Gate.A)
                        .departureTime(LocalDateTime.now().plusHours(2))
                        .build(),

                Booking.builder()
                        .bookingDate(LocalDate.now().minusDays(5))
                        .destination("Stuttgart")
                        .departureGate(Gate.B)
                        .departureTime(LocalDateTime.now().plusDays(3).withHour(8).withMinute(0))
                        .build(),

                Booking.builder()
                        .bookingDate(LocalDate.now().plusDays(1))
                        .destination("Cologne")
                        .departureGate(Gate.C)
                        .departureTime(LocalDateTime.now().plusDays(1).withHour(14).withMinute(45))
                        .build()
        };

        for (Booking booking : bookings) {
            Baggage baggage = Baggage.builder().build();
            checkInCounter.checkIn(baggage, booking);
            conveyorBeltToScanningArea.receiveBaggage(baggage);
        }




    }
}
