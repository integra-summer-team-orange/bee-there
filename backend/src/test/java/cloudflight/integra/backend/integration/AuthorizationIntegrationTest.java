package cloudflight.integra.backend.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cloudflight.integra.backend.inventory.model.InventoryDto;
import cloudflight.integra.backend.resources.model.ResourceDto;
import cloudflight.integra.backend.user.model.Role;
import cloudflight.integra.backend.user.model.UserRequestDto;
import cloudflight.integra.backend.venue.VenueRepository;
import cloudflight.integra.backend.venue.model.Venue;
import cloudflight.integra.backend.venue.model.VenueDto;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class AuthorizationIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private VenueRepository venueRepository;

    private AuthenticatedUser participant;
    private AuthenticatedUser venueAdmin;
    private Long adminVenueId;

    @AfterEach
    void tearDown() {
        venueRepository.deleteAll();
    }

    @BeforeEach
    void setUp() throws Exception {
        participant = registerAndLogin("participant@example.com", DEFAULT_PASSWORD, Role.PARTICIPANT);
        venueAdmin = registerAndLogin("venue.admin@example.com", DEFAULT_PASSWORD, Role.VENUE_ADMIN);
        adminVenueId = createVenue("Admin Venue", adminUserId, adminToken);
    }

    private Long createVenue(String name, Long ownerId, String token) throws Exception {
        VenueDto request = new VenueDto(null, ownerId, name, "Test Description", "1 Test St", null);

        String response = mockMvc.perform(authed(post("/api/venues"), token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    void shouldReturn401WhenNoTokenIsSent() throws Exception {
        mockMvc.perform(get("/api/venues")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/resources")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/inventory")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/users")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/notifications")).andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401WhenWritingWithoutToken() throws Exception {
        VenueDto request = new VenueDto(null, adminUserId, "No Token Venue", "Nope", "1 Nope St", null);

        mockMvc.perform(post("/api/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401WhenTokenIsMalformed() throws Exception {
        mockMvc.perform(authed(get("/api/venues"), "not-a-real-jwt")).andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowParticipantToReadVenuesResourcesAndInventory() throws Exception {
        mockMvc.perform(authed(get("/api/venues"), participant.token())).andExpect(status().isOk());
        mockMvc.perform(authed(get("/api/resources"), participant.token())).andExpect(status().isOk());
        mockMvc.perform(authed(get("/api/inventory"), participant.token())).andExpect(status().isOk());
    }

    @Test
    void shouldReturn403WhenParticipantWritesToVenues() throws Exception {
        VenueDto request =
                new VenueDto(null, participant.id(), "Participant Venue", "Not allowed", "1 Denied St", null);

        mockMvc.perform(authed(post("/api/venues"), participant.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        mockMvc.perform(authed(delete("/api/venues/{id}", adminVenueId), participant.token()))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403WhenParticipantWritesToResources() throws Exception {
        ResourceDto request = new ResourceDto(
                null, adminVenueId, "Denied Court", "Basketball", null, "INDOOR_SPORT", 10, new BigDecimal("10.00"));

        mockMvc.perform(authed(post("/api/resources"), participant.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403WhenParticipantWritesToInventory() throws Exception {
        InventoryDto request = new InventoryDto(null, adminVenueId, "Denied Item", 10, 5);

        mockMvc.perform(authed(post("/api/inventory"), participant.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403WhenNonAdminCallsAnEndpointWithoutItsOwnMatcher() throws Exception {
        mockMvc.perform(authed(get("/api/notifications"), participant.token())).andExpect(status().isForbidden());
        mockMvc.perform(authed(get("/api/notifications"), venueAdmin.token())).andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowVenueAdminToWriteVenues() throws Exception {
        VenueDto request = new VenueDto(null, venueAdmin.id(), "Venue Admin Venue", "Allowed", "1 Allowed St", null);

        mockMvc.perform(authed(post("/api/venues"), venueAdmin.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldDenyParticipantUpdatingAnotherUser() throws Exception {
        UserRequestDto request =
                new UserRequestDto("Hijacked Admin", ADMIN_EMAIL, DEFAULT_PASSWORD, "0123456789", Role.ADMIN.name());

        mockMvc.perform(authed(put("/api/users/{id}", adminUserId), participant.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        assertNotEquals(
                "Hijacked Admin",
                userRepository.findById(adminUserId).orElseThrow().getName(),
                "the admin must not have been modified by a participant");
    }

    @Test
    void shouldDenyParticipantDeletingAnotherUser() throws Exception {
        mockMvc.perform(authed(delete("/api/users/{id}", adminUserId), participant.token()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.messages").isArray());

        assertTrue(userRepository.existsById(adminUserId), "the admin must not have been deleted by a participant");
    }

    @Test
    void shouldAllowParticipantUpdatingThemselves() throws Exception {
        UserRequestDto request = new UserRequestDto(
                "Renamed Participant",
                "participant@example.com",
                DEFAULT_PASSWORD,
                "0123456789",
                Role.PARTICIPANT.name());

        mockMvc.perform(authed(put("/api/users/{id}", participant.id()), participant.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Renamed Participant"));
    }

    @Test
    void shouldAllowAdminToUpdateAnyUser() throws Exception {
        UserRequestDto request = new UserRequestDto(
                "Renamed By Admin", "participant@example.com", DEFAULT_PASSWORD, "0123456789", Role.PARTICIPANT.name());

        mockMvc.perform(authed(put("/api/users/{id}", participant.id()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Renamed By Admin"));
    }

    @Test
    void shouldDenyVenueAdminDeletingAVenueItDoesNotOwn() throws Exception {
        mockMvc.perform(authed(delete("/api/venues/{id}", adminVenueId), venueAdmin.token()))
                .andExpect(status().isForbidden());

        assertTrue(venueRepository.existsById(adminVenueId), "the admin's venue must not have been deleted");
    }

    @Test
    void shouldAllowVenueAdminDeletingItsOwnVenue() throws Exception {
        Long ownVenueId = createVenue("Own Venue", venueAdmin.id(), venueAdmin.token());

        mockMvc.perform(authed(delete("/api/venues/{id}", ownVenueId), venueAdmin.token()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldDenyVenueAdminUpdatingAVenueItDoesNotOwnWhileClaimingOwnership() throws Exception {
        VenueDto hijack = new VenueDto(null, venueAdmin.id(), "Taken Over", "hijacked", "1 Evil St", null);

        mockMvc.perform(authed(put("/api/venues/{id}", adminVenueId), venueAdmin.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hijack)))
                .andExpect(status().isForbidden());

        Venue untouched = venueRepository.findById(adminVenueId).orElseThrow();
        assertEquals(
                adminUserId,
                untouched.getManagedBy().getId(),
                "ownership must not be transferable by putting your own id in the payload");
        assertNotEquals("Taken Over", untouched.getName(), "the admin's venue must not have been rewritten");
    }

    @Test
    void shouldAllowVenueAdminUpdatingItsOwnVenue() throws Exception {
        Long ownVenueId = createVenue("Own Venue", venueAdmin.id(), venueAdmin.token());

        VenueDto update = new VenueDto(null, venueAdmin.id(), "Renamed Venue", "Updated", "2 Allowed St", null);

        mockMvc.perform(authed(put("/api/venues/{id}", ownVenueId), venueAdmin.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Renamed Venue"));
    }

    @Test
    void shouldAllowAdminToUpdateAVenueOwnedBySomeoneElse() throws Exception {
        Long ownVenueId = createVenue("Venue Admin Venue", venueAdmin.id(), venueAdmin.token());

        VenueDto update = new VenueDto(null, venueAdmin.id(), "Renamed By Admin", "Updated", "3 Admin St", null);

        mockMvc.perform(authed(put("/api/venues/{id}", ownVenueId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Renamed By Admin"));
    }
}
