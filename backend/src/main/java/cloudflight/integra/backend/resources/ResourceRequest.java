package cloudflight.integra.backend.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public class ResourceRequest {

    @NotNull(message = "Venue ID is required")
    private Long venueId;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Activity type cannot be blank")
    private String activityType;

    private String activityDescription;

    @ValidResourceType
    private String type;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be a positive integer")
    private Integer capacity;

    @NotNull(message = "Hourly rate is required")
    @PositiveOrZero(message = "Hourly rate must be zero or positive")
    private BigDecimal hourlyRate;

    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }
    public String getActivityDescription() { return activityDescription; }
    public void setActivityDescription(String activityDescription) { this.activityDescription = activityDescription; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }

}
