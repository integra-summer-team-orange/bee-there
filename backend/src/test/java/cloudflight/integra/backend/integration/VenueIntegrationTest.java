package cloudflight.integra.backend.integration;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cloudflight.integra.backend.user.UserRepository;
import cloudflight.integra.backend.venue.VenueRepository;
import cloudflight.integra.backend.venue.model.VenueDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class VenueIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long validUserId;
    private String token;

    @AfterEach
    void tearDown() {
        venueRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    void setUp() throws Exception {

        String registerRequest = """
            {
                "name": "Test Manager",
                "email": "manager@example.com",
                "password": "password",
                "phone": "0123456789",
                "role": "ADMIN"
            }
            """;

        String registerResponse = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequest))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        validUserId = objectMapper.readTree(registerResponse).get("id").asLong();

        String loginRequest = """
            {
                "email": "manager@example.com",
                "password": "password"
            }
            """;

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        token = objectMapper.readTree(loginResponse).get("token").asText();
    }

    private VenueDto createValidVenueDto(String name) {
        return new VenueDto(null, validUserId, name, "Test Description", "123 Test St", null);
    }

    @Test
    void shouldCreateVenue() throws Exception {
        VenueDto request = createValidVenueDto("Main Hall");

        mockMvc.perform(post("/api/venues")
                        .header("Authorization", "Bearer " + token)
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
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetVenueById() throws Exception {
        VenueDto createRequest = createValidVenueDto("Conference Room");

        String response = mockMvc.perform(post("/api/venues")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/venues/{id}", id).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Conference Room"))
                .andExpect(jsonPath("$.managedBy").value(validUserId));
    }

    @Test
    void shouldUpdateVenue() throws Exception {
        VenueDto createRequest = createValidVenueDto("Old Name");

        String response = mockMvc.perform(post("/api/venues")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        VenueDto updateRequest = new VenueDto(null, validUserId, "New Name", "New Desc", "New Address", null);

        mockMvc.perform(put("/api/venues/{id}", id)
                        .header("Authorization", "Bearer " + token)
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
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteVenue() throws Exception {
        VenueDto createRequest = createValidVenueDto("To Delete");

        String response = mockMvc.perform(post("/api/venues")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/venues/{id}", id).header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/venues/{id}", id).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentVenue() throws Exception {
        mockMvc.perform(delete("/api/venues/{id}", 99999L).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllVenues() throws Exception {
        VenueDto venue1 = createValidVenueDto("Venue 1");
        VenueDto venue2 = createValidVenueDto("Venue 2");

        mockMvc.perform(post("/api/venues")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(venue1)));

        mockMvc.perform(post("/api/venues")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(venue2)));

        mockMvc.perform(get("/api/venues").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("Venue 1", "Venue 2")));
    }
}
