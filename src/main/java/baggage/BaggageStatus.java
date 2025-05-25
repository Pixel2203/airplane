// src/main/java/baggage/BaggageStatus.java
package baggage;

public enum BaggageStatus {
    CHECKED_IN,
    BAGGAGE_TAKEN,
    FIRST_SCAN_POINT,
    SORTED,
    BUFFERED,
    CHECKED, // For security scans
    COMPLETED,
    ERROR,
    GATE_A_CONVEYOR,
    GATE_B_CONVEYOR,
    GATE_C_CONVEYOR,
}