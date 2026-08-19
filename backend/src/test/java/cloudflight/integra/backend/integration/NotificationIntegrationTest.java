package cloudflight.integra.backend.integration;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cloudflight.integra.backend.notification.NotificationRepository;
import cloudflight.integra.backend.notification.model.NotificationDto;
import cloudflight.integra.backend.notification.model.NotificationType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class NotificationIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @AfterEach
    void tearDown() {
        notificationRepository.deleteAll();
    }

    private NotificationDto createNotificationDto() {
        return new NotificationDto(null, adminUserId, null, NotificationType.REMINDER, "test", null, false);
    }

    @Test
    void shouldProveFullRoundTrip() throws Exception {

        // 1. CREATE
        NotificationDto request = createNotificationDto();

        String response = mockMvc.perform(authed(post("/api/notifications"))
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
        mockMvc.perform(authed(get("/api/notifications/{id}", id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.type").value(NotificationType.REMINDER.name()))
                .andExpect(jsonPath("$.message").value("test"));

        // 3. UPDATE
        NotificationDto updateRequest = new NotificationDto(
                id, adminUserId, null, NotificationType.WEATHER_ALERT, "updated", localDateTime, true);

        mockMvc.perform(authed(put("/api/notifications/{id}", id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.type").value(NotificationType.WEATHER_ALERT.name()))
                .andExpect(jsonPath("$.message").value("updated"));

        // 4. DELETE
        mockMvc.perform(authed(delete("/api/notifications/{id}", id))).andExpect(status().isNoContent());

        // 5. VERIFY DELETE
        mockMvc.perform(authed(get("/api/notifications/{id}", id))).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400NonExistentUserId() throws Exception {

        Long nonExistentUserId = 999L;

        NotificationDto request = new NotificationDto(
                null, nonExistentUserId, null, NotificationType.REMINDER, "Invalid notification", null, false);

        mockMvc.perform(authed(post("/api/notifications"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldGetAllNotificationsPaged() throws Exception {

        NotificationDto notification1 = new NotificationDto(
                null, adminUserId, null, NotificationType.WEATHER_ALERT, "Notification 1", null, false);

        NotificationDto notification2 =
                new NotificationDto(null, adminUserId, null, NotificationType.REMINDER, "Notification 2", null, false);

        mockMvc.perform(authed(post("/api/notifications"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notification1)))
                .andExpect(status().isCreated());

        mockMvc.perform(authed(post("/api/notifications"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notification2)))
                .andExpect(status().isCreated());

        mockMvc.perform(authed(get("/api/notifications?pageNumber=0&pageSize=10")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.content[*].message", hasItems("Notification 1", "Notification 2")));
    }
}
