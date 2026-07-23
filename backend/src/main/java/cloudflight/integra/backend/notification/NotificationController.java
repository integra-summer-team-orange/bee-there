package cloudflight.integra.backend.notification;

import cloudflight.integra.backend.exceptions.EntityNotFoundException;
import cloudflight.integra.backend.notification.model.NotificationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing notifications.
 */
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "API endpoints for managing system and user notifications")
public class NotificationController {
    private final NotificationService service;
    private final NotificationMapper mapper;

    /**
     * Constructs a new NotificationController.
     *
     * @param notificationService the service for managing notifications
     * @param notificationMapper  the mapper for converting between entities and DTOs
     */
    public NotificationController(NotificationService notificationService, NotificationMapper notificationMapper) {
        this.service = notificationService;
        this.mapper = notificationMapper;
    }

    /**
     * Retrieves all notifications.
     *
     * @return a list of all notifications.
     */
    @GetMapping
    @Operation(summary = "Get all notifications", description = "Retrieves a list of all available notifications.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved list of notifications",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = NotificationDto.class)))
        ),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<List<NotificationDto>> getAll() {
        List<NotificationDto> notifications =
                service.findAll().stream().map(mapper::toDto).toList();
        return ResponseEntity.ok(notifications);
    }

    /**
     * Retrieves a notification by its ID.
     *
     * @param id the ID of the notification to retrieve
     * @return the notification with the given ID
     * @throws EntityNotFoundException if no notification is found with a matching ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a notification by ID", description = "Retrieves a single notification by its unique identifier.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved the notification",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input data or ID format", content = @Content),
        @ApiResponse(responseCode = "404", description = "Notification not found", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<NotificationDto> getById(
            @Parameter(description = "ID of the notification to be retrieved", example = "1", required = true) @PathVariable Long id) {
        return service.findById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id: " + id));
    }

    /**
     * Creates a new notification.
     *
     * @param dto the DTO containing the notification data
     * @return the created notification
     */
    @PostMapping
    @Operation(summary = "Create a new notification", description = "Adds a new notification to the system. Allowed values for the 'type' field: REMINDER, WEATHER_ALERT, JOIN_REQUEST, SYSTEM.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Notification successfully created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<NotificationDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        name = "Valid Notification Request",
                        value = "{\n  \"recipientId\": 12,\n  \"reservationId\": 5,\n  \"type\": \"REMINDER\",\n  \"message\": \"Your reservation starts in 1 hour.\",\n  \"read\": false\n}"
                    )
                )
            )
            @Valid @RequestBody NotificationDto dto) {
        var notification = mapper.toEntity(dto);
        var created = service.create(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(created));
    }

    /**
     * Updates an existing notification.
     *
     * @param id  the ID of the notification to update
     * @param dto the DTO containing the updated notification data
     * @return the updated notification
     * @throws EntityNotFoundException if no notification is found with a matching ID
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing notification", description = "Updates a notification. Allowed values for the 'type' field: REMINDER, WEATHER_ALERT, JOIN_REQUEST, SYSTEM.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Notification successfully updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input data or ID format", content = @Content),
        @ApiResponse(responseCode = "404", description = "Notification not found", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<NotificationDto> update(
            @Parameter(description = "ID of the notification to be updated", example = "1", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        name = "Valid Notification Update Request",
                        value = "{\n  \"recipientId\": 12,\n  \"reservationId\": 5,\n  \"type\": \"SYSTEM\",\n  \"message\": \"System maintenance at midnight.\",\n  \"read\": true\n}"
                    )
                )
            )
            @Valid @RequestBody NotificationDto dto) {
        var notification = mapper.toEntity(dto);
        return service.update(id, notification)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id: " + id));
    }

    /**
     * Marks a notification as read.
     *
     * @param id the ID of the notification to mark as read
     * @return the updated notification
     * @throws EntityNotFoundException if no notification is found with a matching ID
     */
    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark notification as read", description = "Updates the status of a specific notification to read.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Notification successfully marked as read",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationDto.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid ID format", content = @Content),
        @ApiResponse(responseCode = "404", description = "Notification not found", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<NotificationDto> markAsRead(
            @Parameter(description = "ID of the notification to mark as read", example = "1", required = true) @PathVariable Long id) {
        return service.markAsRead(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id: " + id));
    }

    /**
     * Deletes a notification by its ID.
     *
     * @param id the ID of the notification to delete
     * @throws EntityNotFoundException if no notification is found with a matching ID
     * @return a response status no content if it was successful
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a notification", description = "Removes a notification by its unique identifier.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Notification successfully deleted", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid ID format", content = @Content),
        @ApiResponse(responseCode = "404", description = "Notification not found", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the notification to be deleted", example = "1", required = true) @PathVariable Long id) {
        if (!service.delete(id)) {
            throw new EntityNotFoundException("Notification not found with id: " + id);
        }
        return ResponseEntity.noContent().build();
    }
}
