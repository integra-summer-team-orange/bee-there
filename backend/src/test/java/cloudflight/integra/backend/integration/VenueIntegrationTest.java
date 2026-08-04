package cloudflight.integra.backend.integration;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cloudflight.integra.backend.user.UserRepository;
import cloudflight.integra.backend.user.model.Role;
import cloudflight.integra.backend.user.model.User;
import cloudflight.integra.backend.venue.model.VenueDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class VenueIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long validUserId;

    @BeforeEach
    void setUp() {
        // DB is rolled back automatically thanks to @Transactional
        User user = new User("Test Manager", "manager@example.com", "hash", "0123456789", Role.PARTICIPANT);
        user = userRepository.save(user);
        validUserId = user.getId();
    }

    private VenueDto createValidVenueDto(String name) {
        return new VenueDto(null, validUserId, name, "Test Description", "123 Test St", null);
    }

    @Test
    void shouldCreateVenue() throws Exception {
        VenueDto request = createValidVenueDto("Main Hall");

        mockMvc.perform(post("/api/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.managedBy").value(validUserId))
                .andExpect(jsonPath("$.name").value("Main Hall"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.address").value("123 Test St"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldReturnClean400WhenManagedByDoesNotExist() throws Exception {
        VenueDto request = new VenueDto(null, 99999L, "Main Hall", "A large hall", "123 Main St", null);

        mockMvc.perform(post("/api/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("User with id 99999 does not exist"));
    }

    @Test
    void shouldGetVenueById() throws Exception {
        VenueDto createRequest = createValidVenueDto("Conference Room");

        String response = mockMvc.perform(post("/api/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/venues/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Conference Room"))
                .andExpect(jsonPath("$.managedBy").value(validUserId));
    }

    @Test
    void shouldUpdateVenue() throws Exception {
        VenueDto createRequest = createValidVenueDto("Old Name");

        String response = mockMvc.perform(post("/api/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        VenueDto updateRequest = new VenueDto(null, validUserId, "New Name", "New Desc", "New Address", null);

        mockMvc.perform(put("/api/venues/{id}", id)
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

        mockMvc.perform(put("/api/venues/{id}", 99999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteVenue() throws Exception {
        VenueDto createRequest = createValidVenueDto("To Delete");

        String response = mockMvc.perform(post("/api/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/venues/{id}", id)).andExpect(status().isNoContent());

        mockMvc.perform(get("/api/venues/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentVenue() throws Exception {
        mockMvc.perform(delete("/api/venues/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllVenues() throws Exception {
        VenueDto venue1 = createValidVenueDto("Venue 1");
        VenueDto venue2 = createValidVenueDto("Venue 2");

        mockMvc.perform(post("/api/venues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(venue1)));
        mockMvc.perform(post("/api/venues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(venue2)));

        mockMvc.perform(get("/api/venues"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Venue 1", "Venue 2")));
    }
}
