package cloudflight.integra.backend.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cloudflight.integra.backend.authentication.model.LoginRequestDto;
import cloudflight.integra.backend.user.UserRepository;
import cloudflight.integra.backend.user.model.Role;
import cloudflight.integra.backend.user.model.UserRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    protected static final String ADMIN_EMAIL = "admin@example.com";
    protected static final String DEFAULT_PASSWORD = "!Password123";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    protected Long adminUserId;
    protected String adminToken;

    protected record AuthenticatedUser(Long id, String token) {}

    @BeforeEach
    void seedDefaultAdmin() throws Exception {
        userRepository.deleteAll();

        AuthenticatedUser admin = registerAndLogin(ADMIN_EMAIL, DEFAULT_PASSWORD, Role.ADMIN);
        adminUserId = admin.id();
        adminToken = admin.token();
    }

    @AfterEach
    void deleteUsers() {
        userRepository.deleteAll();
    }

    protected AuthenticatedUser registerAndLogin(String email, String password, Role role) throws Exception {
        Long id = register(email, password, role);
        return new AuthenticatedUser(id, login(email, password));
    }

    private Long register(String email, String password, Role role) throws Exception {
        UserRequestDto request = new UserRequestDto("Test " + role.name(), email, password, "0123456789", role.name());

        String response = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    private String login(String email, String password) throws Exception {
        String request = objectMapper.writeValueAsString(new LoginRequestDto(email, password));

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }

    protected MockHttpServletRequestBuilder authed(MockHttpServletRequestBuilder request) {
        return authed(request, adminToken);
    }

    protected MockHttpServletRequestBuilder authed(MockHttpServletRequestBuilder request, String token) {
        return request.header("Authorization", "Bearer " + token);
    }
}
