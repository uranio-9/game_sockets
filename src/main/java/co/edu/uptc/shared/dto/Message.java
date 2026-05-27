package co.edu.uptc.shared.dto;

/**
 * Minimal type-only holder used for dispatching incoming JSON.
 * Parse any raw JSON string into this first, read {@code type}, then
 * parse again into the specific concrete DTO.
 */
public class Message {
    private String type;

    public String getType() { return type; }
    public void   setType(String type) { this.type = type; }
}
