package cloudflight.integra.backend.integration;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cloudflight.integra.backend.notification.NotificationRepository;
import cloudflight.integra.backend.notification.model.NotificationDto;
import cloudflight.integra.backend.notification.model.NotificationType;
import cloudflight.integra.backend.user.UserRepository;
import cloudflight.integra.backend.user.model.Role;
import cloudflight.integra.backend.user.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
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
public class NotificationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    Long savedUserId;

    @BeforeEach
    void setup() {

        notificationRepository.deleteAll();
        userRepository.deleteAll();
        User user = new User("test", "test@gmail.com", "password_hash", "0777777777", Role.ADMIN);

        User savedUser = userRepository.save(user);

        savedUserId = savedUser.getId();
    }

    private NotificationDto createNotificationDto() {
        return new NotificationDto(null, savedUserId, null, NotificationType.REMINDER, "test", null, false);
    }

    @Test
    void shouldProveFullRoundTrip() throws Exception {

        // 1. CREATE
        NotificationDto request = createNotificationDto();

        String response = mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.type").value(NotificationType.REMINDER.name()))
                .andExpect(jsonPath("$.message").value("test"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        LocalDateTime localDateTime = LocalDateTime.parse(
                objectMapper.readTree(response).get("sentAt").asText());

        // 2. READ
        mockMvc.perform(get("/api/notifications/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.type").value(NotificationType.REMINDER.name()))
                .andExpect(jsonPath("$.message").value("test"));

        // 3. UPDATE
        NotificationDto updateRequest = new NotificationDto(
                id, savedUserId, null, NotificationType.WEATHER_ALERT, "updated", localDateTime, true);

        mockMvc.perform(put("/api/notifications/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.type").value(NotificationType.WEATHER_ALERT.name()))
                .andExpect(jsonPath("$.message").value("updated"));

        // 4. DELETE
        mockMvc.perform(delete("/api/notifications/{id}", id)).andExpect(status().isNoContent());

        // 5. VERIFY DELETE
        mockMvc.perform(get("/api/notifications/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400NonExistentUserId() throws Exception {

        Long nonExistentUserId = 999L;

        NotificationDto request = new NotificationDto(
                null, nonExistentUserId, null, NotificationType.REMINDER, "Invalid notification", null, false);

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldGetAllNotificationsPaged() throws Exception {

        NotificationDto notification1 = new NotificationDto(
                null, savedUserId, null, NotificationType.WEATHER_ALERT, "Notification 1", null, false);

        NotificationDto notification2 =
                new NotificationDto(null, savedUserId, null, NotificationType.REMINDER, "Notification 2", null, false);

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notification1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notification2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/notifications?pageNumber=0&pageSize=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.content[*].message", hasItems("Notification 1", "Notification 2")));
    }
}
