package cloudflight.integra.backend.integration;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cloudflight.integra.backend.resources.ResourceRepository;
import cloudflight.integra.backend.resources.model.ResourceDto;
import cloudflight.integra.backend.user.model.User;
import cloudflight.integra.backend.venue.VenueRepository;
import cloudflight.integra.backend.venue.model.Venue;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class ResourceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private VenueRepository venueRepository;

    private Long validVenueId;

    @AfterEach
    void tearDown() {
        resourceRepository.deleteAll();
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
        ResourceDto request = new ResourceDto(
                null,
                validVenueId,
                "Main Court",
                "Basketball",
                "Full court indoor basketball",
                "INDOOR_SPORT",
                20,
                new BigDecimal("50.00"));

        String response = mockMvc.perform(authed(post("/api/resources"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Main Court"))
                .andExpect(jsonPath("$.venueId").value(validVenueId))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        // 2. READ (GET by ID)
        mockMvc.perform(authed(get("/api/resources/{id}", id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Main Court"));

        // 3. UPDATE (PUT)
        ResourceDto updateRequest = new ResourceDto(
                id,
                validVenueId,
                "Main Court Updated",
                "Volleyball",
                "Net adjusted for volleyball",
                "INDOOR_SPORT",
                24,
                new BigDecimal("60.00"));

        mockMvc.perform(authed(put("/api/resources/{id}", id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Main Court Updated"))
                .andExpect(jsonPath("$.activityType").value("Volleyball"));

        // 4. DELETE
        mockMvc.perform(authed(delete("/api/resources/{id}", id))).andExpect(status().isNoContent());

        // 5. VERIFY DELETION (GET expecting 404)
        mockMvc.perform(authed(get("/api/resources/{id}", id))).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400ForNonExistentVenueId() throws Exception {
        // Acceptance criteria: creating a resource with a non-existent venue_id returns a clean 400
        Long nonExistentVenueId = 999999L;
        ResourceDto request = new ResourceDto(
                null,
                nonExistentVenueId,
                "Invalid Venue Court",
                "Tennis",
                null,
                "OUTDOOR_SPORT",
                4,
                new BigDecimal("30.00"));

        mockMvc.perform(authed(post("/api/resources"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.messages[0]", containsString("constraint violation")));
    }

    @Test
    void shouldRejectInvalidResourceType() throws Exception {
        // Existing @ValidResourceType validator must continue to pass
        ResourceDto request = new ResourceDto(
                null, validVenueId, "Bad Type Court", "Chess", null, "NOT_A_REAL_TYPE", 4, new BigDecimal("10.00"));

        mockMvc.perform(authed(post("/api/resources"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldRejectNegativeCapacity() throws Exception {
        ResourceDto request = new ResourceDto(
                null,
                validVenueId,
                "Negative Capacity Court",
                "Basketball",
                null,
                "INDOOR_SPORT",
                -5,
                new BigDecimal("10.00"));

        mockMvc.perform(authed(post("/api/resources"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldGetAllResourcesPaged() throws Exception {
        ResourceDto item1 = new ResourceDto(
                null, validVenueId, "Court 1", "Basketball", null, "INDOOR_SPORT", 10, new BigDecimal("20.00"));
        ResourceDto item2 = new ResourceDto(
                null, validVenueId, "Court 2", "Tennis", null, "OUTDOOR_SPORT", 4, new BigDecimal("15.00"));

        mockMvc.perform(authed(post("/api/resources"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item1)));

        mockMvc.perform(authed(post("/api/resources"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item2)));

        mockMvc.perform(authed(get("/api/resources?pageNumber=0&pageSize=10")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.content[*].name", hasItems("Court 1", "Court 2")));
    }
}
