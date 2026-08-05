package cloudflight.integra.backend.integration;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cloudflight.integra.backend.user.UserRepository;
import cloudflight.integra.backend.user.model.Role;
import cloudflight.integra.backend.user.model.UserRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    private UserRequestDto buildUserRequest(String name, String email, String password, String phone, Role role) {
        return new UserRequestDto(name, email, password, phone, role);
    }

    private UserRequestDto buildDefaultUserRequest() {
        return buildUserRequest("John Doe", "john.doe@example.com", "!Password123", "0741234567", Role.PARTICIPANT);
    }

    private Long createAndReturnUserId(UserRequestDto request) throws Exception {
        String response = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    void shouldCreateUser() throws Exception {
        UserRequestDto request = buildDefaultUserRequest();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.phone").value("0741234567"))
                .andExpect(jsonPath("$.role").value("PARTICIPANT"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void shouldGetUserById() throws Exception {
        UserRequestDto createRequest =
                buildUserRequest("Jane Doe", "jane.doe@example.com", "!Password123", "0747654321", Role.ADMIN);
        Long id = createAndReturnUserId(createRequest);

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane.doe@example.com"));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        UserRequestDto createRequest =
                buildUserRequest("Update Me", "update.me@example.com", "!Password123", "0741111111", Role.PARTICIPANT);
        Long id = createAndReturnUserId(createRequest);

        UserRequestDto updateRequest = buildUserRequest(
                "Updated Name", "updated.email@example.com", "!NewPassword123", "0742222222", Role.ADMIN);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated.email@example.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        UserRequestDto createRequest =
                buildUserRequest("Delete Me", "delete.me@example.com", "!Password123", "0743333333", Role.PARTICIPANT);
        Long id = createAndReturnUserId(createRequest);

        mockMvc.perform(delete("/api/users/{id}", id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/users/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        UserRequestDto user1 =
                buildUserRequest("User One", "user1@example.com", "!Password123", "0741111111", Role.PARTICIPANT);
        UserRequestDto user2 =
                buildUserRequest("User Two", "user2@example.com", "!Password123", "0742222222", Role.PARTICIPANT);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)));
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2)));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].email", containsInAnyOrder("user1@example.com", "user2@example.com")));
    }

    @Test
    void shouldDenyDuplicateEmail() throws Exception {
        UserRequestDto user1 =
                buildUserRequest("User One", "user1@example.com", "!Password123", "0741111111", Role.PARTICIPANT);
        UserRequestDto user1_1 =
                buildUserRequest("User One (1)", "user1@example.com", "!Password123", "0742222222", Role.PARTICIPANT);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1_1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages[0]").exists());
    }

    @Test
    void shouldNotGetNonExistentUser() throws Exception {
        mockMvc.perform(get("/api/users/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonexistentUser() throws Exception {
        UserRequestDto updateRequest =
                buildUserRequest("Waldo", "waldo@example.com", "!NewPassword123", "0742222222", Role.ADMIN);

        mockMvc.perform(put("/api/users/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotDeleteNonExistentUser() throws Exception {
        mockMvc.perform(delete("/api/users/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldRejectInvalidEmail() throws Exception {
        UserRequestDto request =
                buildUserRequest("Test User", "invalid-email", "!Password123", "0741234567", Role.PARTICIPANT);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectWeakPassword() throws Exception {
        UserRequestDto request =
                buildUserRequest("Test User", "test@example.com", "123", "0741234567", Role.PARTICIPANT);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectEmptyName() throws Exception {
        UserRequestDto request =
                buildUserRequest("", "test@example.com", "!Password123", "0741234567", Role.PARTICIPANT);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotReturnPasswordInResponse() throws Exception {
        UserRequestDto request =
                buildUserRequest("Secure User", "secure@example.com", "!Password123", "0741234567", Role.PARTICIPANT);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }
}
