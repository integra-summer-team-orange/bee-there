package cloudflight.integra.backend.inventory.model;

import java.util.UUID;

public class Inventory {
    private UUID id;
    private UUID venueId;
    private String name;
    private int totalQuantity;
    private int availableQuantity;

    public Inventory() {
    }

    public Inventory(UUID id, UUID venueId, String name, int totalQuantity, int availableQuantity) {
        this.id = id;
        this.venueId = venueId;
        this.name = name;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = availableQuantity;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getVenueId() {
        return venueId;
    }

    public void setVenueId(UUID venueId) {
        this.venueId = venueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }
}
