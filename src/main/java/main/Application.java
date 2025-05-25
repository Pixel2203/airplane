// src/main/java/main/Application.java
package main;

import baggage.Baggage;
import baggage.BaggageStatus;
import baggage.IBaggageTracking;
import components.CentralControlModule;
import components.checkInCounter.CheckInCounter;
import components.checkInCounter.ICheckInCounter;
import components.conveyorBelt.ConveyorBelt;
import components.conveyorBelt.IConveyorBelt;
import components.gateBelt.GateBelt;
import components.gateBelt.IGateBelt;
import components.gripper.Gripper;
import components.gripper.IGripper;
import components.intermediateChannel.IIntermediateChannel;
import components.intermediateChannel.IntermediateChannel;
import components.scanningArea.IScanningArea;
import components.scanningArea.ScanningArea;
import components.sortingArea.ISortingArea;
import components.sortingArea.SortingArea;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

@Slf4j
public class Application {
    private static final Random RANDOM = new Random();
    private static final String[] DESTINATIONS = {"JFK", "LAX", "LHR", "FRA", "CDG"};

    public static void main(String... args) {
        // Initialize Central Control Module
        IBaggageTracking controlModule = new CentralControlModule();

        // Initialize Components
        ICheckInCounter checkInCounter = new CheckInCounter(controlModule);
        IConveyorBelt incomingConveyor = new ConveyorBelt("IncomingConveyor", controlModule);
        IScanningArea scanningArea = new ScanningArea(controlModule);
        Map<String, String> destinationToConveyorMap = Map.of(
                "JFK", "OutputConveyor1",
                "LAX", "OutputConveyor2",
                "LHR", "OutputConveyor3",
                "FRA", "OutputConveyor1", // Example of sharing
                "CDG", "OutputConveyor2"
        );
        ISortingArea sortingArea = new SortingArea(controlModule, destinationToConveyorMap);
        IConveyorBelt outputConveyor1 = new ConveyorBelt("OutputConveyor1", controlModule);
        IConveyorBelt outputConveyor2 = new ConveyorBelt("OutputConveyor2", controlModule);
        IConveyorBelt outputConveyor3 = new ConveyorBelt("OutputConveyor3", controlModule);
        IIntermediateChannel intermediateChannel = new IntermediateChannel("IntermediateChannel", controlModule);
        IGripper gripper = new Gripper(controlModule);
        IGateBelt gateBelt = new GateBelt("GateBelt", controlModule, gripper);


        log.info("Baggage Handling System Simulation Started.");

        // Simulate baggage flow for 10 bags
        IntStream.range(0, 10).forEach(i -> {
            String barcode = "BAG-" + String.format("%04d", i);
            String destination = DESTINATIONS[RANDOM.nextInt(DESTINATIONS.length)];

            log.info("\n--- Processing Baggage {} ---", barcode);
            Baggage newBaggage = checkInCounter.checkInBaggage(barcode, destination);
            delay(100);

            incomingConveyor.receiveBaggage(newBaggage);
            delay(100);
            incomingConveyor.transferBaggage(newBaggage, "Scanning Area");
            delay(100);

            boolean scanned = scanningArea.scanBaggage(newBaggage);
            if (scanned) {
                delay(100);
                String sortedTo = sortingArea.sortBaggage(newBaggage);
                delay(100);

                if ("Intermediate Channel".equals(sortedTo)) {
                    log.info("Baggage {} is in Intermediate Channel. Simulating conveyor becoming free.", barcode);
                    // Simulate the target conveyor becoming free after some time
                    String originalTargetConveyor = destinationToConveyorMap.get(destination);
                    if (originalTargetConveyor != null) {
                        controlModule.setConveyorStatus(originalTargetConveyor, true); // Mark as full for demonstration
                        delay(500); // Simulate some delay
                        controlModule.setConveyorStatus(originalTargetConveyor, false); // Mark as free
                        intermediateChannel.releaseOldestBaggage(originalTargetConveyor);
                        delay(100);
                        // Now the baggage should be on its way to the original target conveyor
                        // and eventually to the gate belt
                        Baggage currentBaggage = controlModule.getBaggage(barcode).orElse(null);
                        if (currentBaggage != null && currentBaggage.getStatus() != BaggageStatus.ERROR) {
                             // Assuming it now goes to the gate belt after being released
                            if (originalTargetConveyor.equals("OutputConveyor1")) {
                                outputConveyor1.transferBaggage(currentBaggage, "GateBelt");
                            } else if (originalTargetConveyor.equals("OutputConveyor2")) {
                                outputConveyor2.transferBaggage(currentBaggage, "GateBelt");
                            } else if (originalTargetConveyor.equals("OutputConveyor3")) {
                                outputConveyor3.transferBaggage(currentBaggage, "GateBelt");
                            }
                            delay(100);
                            gateBelt.receiveBaggage(currentBaggage);
                            delay(100);
                            gateBelt.transferToGripper(currentBaggage);
                        }
                    }

                } else if (sortedTo != null) {
                    // Direct transfer to Gate Belt (simplified for simulation)
                    Baggage currentBaggage = controlModule.getBaggage(barcode).orElse(null);
                    if (currentBaggage != null && currentBaggage.getStatus() != BaggageStatus.ERROR) {
                        if (sortedTo.equals("OutputConveyor1")) {
                            outputConveyor1.transferBaggage(currentBaggage, "GateBelt");
                        } else if (sortedTo.equals("OutputConveyor2")) {
                            outputConveyor2.transferBaggage(currentBaggage, "GateBelt");
                        } else if (sortedTo.equals("OutputConveyor3")) {
                            outputConveyor3.transferBaggage(currentBaggage, "GateBelt");
                        }
                        delay(100);
                        gateBelt.receiveBaggage(currentBaggage);
                        delay(100);
                        gateBelt.transferToGripper(currentBaggage);
                    }
                }
            } else {
                log.warn("Baggage {} encountered an error at scanning. Simulation continuing with next bag.", barcode);
            }
            delay(200); // Small pause between bags
        });

        log.info("\nBaggage Handling System Simulation Completed.");
    }

    private static void delay(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Simulation delay interrupted.", e);
        }
    }
}