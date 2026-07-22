package cloudflight.integra.backend.venue.model;

import java.time.LocalDateTime;

public class Venue {
    private Long id;
    // TODO: Replace with @ManyToOne when JPA is introduced
    private Long managedBy;
    private String name;
    private String description;
    private String address;
    private LocalDateTime createdAt;

    public Venue() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getManagedBy() {
        return managedBy;
    }

    public void setManagedBy(Long managedBy) {
        this.managedBy = managedBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
