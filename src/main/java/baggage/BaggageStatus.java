// src/main/java/baggage/BaggageStatus.java
package baggage;

public enum BaggageStatus {
    CHECKED_IN,
    INCOMING_CONVEYOR,
    FIRST_SCAN_POINT,
    SORTED,
    BUFFERED,
    CHECKED, // For security scans
    COMPLETED,
    ERROR
}