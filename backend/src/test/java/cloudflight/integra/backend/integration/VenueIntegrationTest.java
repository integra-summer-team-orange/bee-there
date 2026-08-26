package cloudflight.integra.backend.integration;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cloudflight.integra.backend.user.model.Role;
import cloudflight.integra.backend.venue.VenueRepository;
import cloudflight.integra.backend.venue.model.VenueDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class VenueIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private VenueRepository venueRepository;

    @AfterEach
    void tearDown() {
        venueRepository.deleteAll();
    }

    private VenueDto createValidVenueDto(String name) {
        return new VenueDto(null, null, null, name, "Test Description", "123 Test St", null);
    }

    private Long createVenue(String name, String token) throws Exception {
        String response = mockMvc.perform(authed(post("/api/venues"), token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidVenueDto(name))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    void shouldCreateVenue() throws Exception {
        VenueDto request = createValidVenueDto("Main Hall");

        mockMvc.perform(authed(post("/api/venues"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.managedBy").value(adminUserId))
                .andExpect(jsonPath("$.managedByName").value("Test ADMIN"))
                .andExpect(jsonPath("$.name").value("Main Hall"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.address").value("123 Test St"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldIgnoreManagedByFromThePayloadAndUseTheCaller() throws Exception {
        VenueDto request = new VenueDto(null, 99999L, "Someone Else", "Main Hall", "A large hall", "123 Main St", null);

        mockMvc.perform(authed(post("/api/venues"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.managedBy").value(adminUserId))
                .andExpect(jsonPath("$.managedByName").value("Test ADMIN"));
    }

    @Test
    void shouldGetVenueById() throws Exception {
        VenueDto createRequest = createValidVenueDto("Conference Room");

        String response = mockMvc.perform(authed(post("/api/venues"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(authed(get("/api/venues/{id}", id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Conference Room"))
                .andExpect(jsonPath("$.managedBy").value(adminUserId));
    }

    @Test
    void shouldUpdateVenue() throws Exception {
        VenueDto createRequest = createValidVenueDto("Old Name");

        String response = mockMvc.perform(authed(post("/api/venues"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        VenueDto updateRequest = new VenueDto(null, null, null, "New Name", "New Desc", "New Address", null);

        mockMvc.perform(authed(put("/api/venues/{id}", id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.description").value("New Desc"))
                .andExpect(jsonPath("$.address").value("New Address"));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentVenue() throws Exception {
        VenueDto updateRequest = createValidVenueDto("Non Existent");

        mockMvc.perform(authed(put("/api/venues/{id}", 99999L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteVenue() throws Exception {
        VenueDto createRequest = createValidVenueDto("To Delete");

        String response = mockMvc.perform(authed(post("/api/venues"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(authed(delete("/api/venues/{id}", id))).andExpect(status().isNoContent());

        mockMvc.perform(authed(get("/api/venues/{id}", id))).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentVenue() throws Exception {
        mockMvc.perform(authed(delete("/api/venues/{id}", 99999L))).andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllVenues() throws Exception {
        VenueDto venue1 = createValidVenueDto("Venue 1");
        VenueDto venue2 = createValidVenueDto("Venue 2");

        mockMvc.perform(authed(post("/api/venues"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(venue1)));

        mockMvc.perform(authed(post("/api/venues"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(venue2)));

        mockMvc.perform(authed(get("/api/venues")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("Venue 1", "Venue 2")));
    }

    @Test
    void shouldListOnlyTheVenuesManagedByTheCaller() throws Exception {
        AuthenticatedUser venueAdmin = registerAndLogin("venue.admin@example.com", DEFAULT_PASSWORD, Role.VENUE_ADMIN);

        createVenue("Admin Venue", adminToken);
        createVenue("Managed Venue", venueAdmin.token());

        mockMvc.perform(authed(get("/api/venues/my"), venueAdmin.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Managed Venue"))
                .andExpect(jsonPath("$.content[0].managedBy").value(venueAdmin.id()));
    }

    @Test
    void shouldReturnAnEmptyPageWhenTheCallerManagesNoVenues() throws Exception {
        AuthenticatedUser participant = registerAndLogin("participant@example.com", DEFAULT_PASSWORD, Role.PARTICIPANT);

        createVenue("Admin Venue", adminToken);

        mockMvc.perform(authed(get("/api/venues/my"), participant.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void shouldKeepTheOriginalOwnerWhenUpdating() throws Exception {
        AuthenticatedUser venueAdmin = registerAndLogin("venue.admin@example.com", DEFAULT_PASSWORD, Role.VENUE_ADMIN);
        Long venueId = createVenue("Managed Venue", venueAdmin.token());

        VenueDto handover = new VenueDto(null, adminUserId, "Test ADMIN", "Managed Venue", "Desc", "Address", null);

        mockMvc.perform(authed(put("/api/venues/{id}", venueId), venueAdmin.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(handover)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.managedBy").value(venueAdmin.id()));
    }
}
