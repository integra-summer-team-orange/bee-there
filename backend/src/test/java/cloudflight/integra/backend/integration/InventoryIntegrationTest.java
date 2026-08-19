package cloudflight.integra.backend.integration;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cloudflight.integra.backend.inventory.InventoryRepository;
import cloudflight.integra.backend.inventory.model.InventoryDto;
import cloudflight.integra.backend.user.model.User;
import cloudflight.integra.backend.venue.VenueRepository;
import cloudflight.integra.backend.venue.model.Venue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class InventoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private VenueRepository venueRepository;

    private Long validVenueId;

    @AfterEach
    void tearDown() {
        inventoryRepository.deleteAll();
        venueRepository.deleteAll();
    }

    @BeforeEach
    void setUp() {
        User manager = userRepository.findById(adminUserId).orElseThrow();

        Venue venue = new Venue();
        venue.setManagedBy(manager);
        venue.setName("Test Venue");
        venue.setDescription("Test");
        venue.setAddress("Test Cluj");

        venue = venueRepository.save(venue);
        validVenueId = venue.getId();
    }

    @Test
    void shouldProveFullRoundTrip() throws Exception {
        // 1. CREATE (POST)
        InventoryDto request = new InventoryDto(null, validVenueId, "Test Item", 100, 50);

        String response = mockMvc.perform(authed(post("/api/inventory"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.totalQuantity").value(100))
                .andExpect(jsonPath("$.availableQuantity").value(50))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        // 2. READ (GET by ID)
        mockMvc.perform(authed(get("/api/inventory/{id}", id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Test Item"));

        // 3. UPDATE (PUT)
        InventoryDto updateRequest = new InventoryDto(id, validVenueId, "Updated Item", 200, 150);

        mockMvc.perform(authed(put("/api/inventory/{id}", id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Item"))
                .andExpect(jsonPath("$.totalQuantity").value(200));

        // 4. DELETE
        mockMvc.perform(authed(delete("/api/inventory/{id}", id))).andExpect(status().isNoContent());

        // 5. VERIFY DELETION (GET expecting 404)
        mockMvc.perform(authed(get("/api/inventory/{id}", id))).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400ForNonExistentVenueId() throws Exception {
        // Acceptance Criteria: creating inventory with a non-existent venue_id returns a clean 400
        Long nonExistentVenueId = 999999L;
        InventoryDto request = new InventoryDto(null, nonExistentVenueId, "Invalid Venue Item", 10, 5);

        mockMvc.perform(authed(post("/api/inventory"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldFireCrossFieldValidatorWhenAvailableExceedsTotal() throws Exception {
        // Acceptance Criteria: available_quantity <= total_quantity validator still fires correctly
        InventoryDto request =
                new InventoryDto(null, validVenueId, "Bad Quantities Item", 5, 10); // available (10) > total (5)

        mockMvc.perform(authed(post("/api/inventory"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.messages").isArray());
    }

    @Test
    void shouldRejectNegativeQuantities() throws Exception {
        InventoryDto request = new InventoryDto(null, validVenueId, "Negative Item", -10, -5);

        mockMvc.perform(authed(post("/api/inventory"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldGetAllInventoriesPaged() throws Exception {
        InventoryDto item1 = new InventoryDto(null, validVenueId, "Item 1", 10, 10);
        InventoryDto item2 = new InventoryDto(null, validVenueId, "Item 2", 20, 20);

        mockMvc.perform(authed(post("/api/inventory"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item1)));

        mockMvc.perform(authed(post("/api/inventory"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item2)));

        mockMvc.perform(authed(get("/api/inventory?pageNumber=0&pageSize=10")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.content[*].name", hasItems("Item 1", "Item 2")));
    }
}
