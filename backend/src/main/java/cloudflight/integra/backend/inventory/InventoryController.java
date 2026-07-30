package cloudflight.integra.backend.inventory;

import cloudflight.integra.backend.exceptions.EntityNotFoundException;
import cloudflight.integra.backend.exceptions.ErrorResponse;
import cloudflight.integra.backend.inventory.model.InventoryDto;
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
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * REST Controller for managing inventory-related API endpoints.
 * Handles incoming HTTP requests for creating, reading, updating, and deleting inventory items.
 */
@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventory", description = "API endpoints for managing inventory items")
public class InventoryController {
    private final InventoryService service;
    private final InventoryMapper mapper;

    /**
     * Creates a new inventory controller.
     *
     * @param service the inventory service
     * @param mapper the inventory mapper
     */
    public InventoryController(InventoryService service, InventoryMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /**
     * Retrieves a list of all inventory items present in the system.
     *
     * @return A {@link ResponseEntity} containing a list of {@link InventoryDto} with a 200 OK status.
     */
    @GetMapping
    @Operation(summary = "Get all inventory items", description = "Retrieves a list of all inventory items.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved inventory items",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        array = @ArraySchema(schema = @Schema(implementation = InventoryDto.class)))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class)
                                ))
            })
    public ResponseEntity<List<InventoryDto>> getAll() {
        return ResponseEntity.ok(service.getAll().stream().map(mapper::toDto).toList());
    }

    /**
     * Retrieves the details of a specific inventory item based on its ID.
     *
     * @param id The unique identifier of the requested inventory item.
     * @return A {@link ResponseEntity} containing the {@link InventoryDto} with a 200 OK status.
     * @throws EntityNotFoundException if no inventory item is found matching the provided ID.
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get inventory item by ID",
            description = "Retrieves a single inventory item by its unique identifier.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved the inventory item",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = InventoryDto.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid inventory ID",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class)
                                )),
                @ApiResponse(
                        responseCode = "404",
                        description = "Inventory item not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class)
                                )),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class)
                                ))
            })
    public ResponseEntity<InventoryDto> getById(
            @Parameter(description = "ID of the inventory item to retrieve", example = "1", required = true)
                    @PathVariable
                    Long id) {
        return ResponseEntity.ok(service.getById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Inventory with id: " + id + " not found!")));
    }

    /**
     * Creates a new inventory item based on the provided request payload.
     *
     * @param dto The validated {@link InventoryDto} containing the inventory details.
     * @return A {@link ResponseEntity} containing the created {@link InventoryDto} with a 201 CREATED
     *         status and a Location header.
     */
    @PostMapping
    @Operation(summary = "Create a new inventory item", description = "Creates a new inventory item.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Inventory item successfully created",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = InventoryDto.class),
                                        examples = @ExampleObject(name = "Created Inventory Response", value = """
                            {
                                "id": 1,
                                "venueId": 1,
                                "name": "Chair",
                                "totalQuantity": 100,
                                "availableQuantity": 95
                            }
                            """))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input data",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class)
                                )),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class)
                                ))
            })
    public ResponseEntity<InventoryDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "NotificationDto for create",
                    content =
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NotificationDto.class),
                                    examples = @ExampleObject(name = "Valid Inventory Request", value = """
                    {
                        "venueId": 1,
                        "name": "Chair",
                        "totalQuantity": 100,
                        "availableQuantity": 95
                    }
                    """)))
                    @RequestBody
                    @Valid
                    InventoryDto dto) {
        InventoryDto createdInventory = mapper.toDto(service.create(mapper.toEntity(dto)));
        URI location = UriComponentsBuilder.fromPath("/api/inventory/{id}")
                .buildAndExpand(createdInventory.id())
                .toUri();
        return ResponseEntity.created(location).body(createdInventory);
    }

    /**
     * Updates an existing inventory item entirely with the provided payload.
     *
     * @param id The unique identifier of the inventory item to be updated.
     * @param dto The validated {@link InventoryDto} containing the new data.
     * @return A {@link ResponseEntity} containing the updated {@link InventoryDto} with a 200 OK status.
     * @throws EntityNotFoundException if no inventory item is found matching the provided ID.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an inventory item", description = "Updates an existing inventory item.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Inventory item successfully updated",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = InventoryDto.class),
                                        examples = @ExampleObject(name = "Updated Inventory Response", value = """
                            {
                                "id": 1,
                                "venueId": 1,
                                "name": "Chair",
                                "totalQuantity": 100,
                                "availableQuantity": 90
                            }
                            """))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input data or id",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class)
                                )),
                @ApiResponse(
                        responseCode = "404",
                        description = "Inventory item not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class)
                                )),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class)
                                ))
            })
    public ResponseEntity<InventoryDto> update(
            @Parameter(description = "ID of the inventory item to update", example = "1", required = true) @PathVariable
                    Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "NotificationDto for update",
                    content =
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NotificationDto.class),
                                    examples =
                                            @ExampleObject(
                                                    name = "Valid Inventory Update Request",
                                                    value = """
                    {
                        "venueId": 1,
                        "name": "Chair",
                        "totalQuantity": 100,
                        "availableQuantity": 90
                    }
                    """)))
                    @RequestBody
                    @Valid
                    InventoryDto dto) {
        return ResponseEntity.ok(mapper.toDto(service.update(id, mapper.toEntity(dto))));
    }

    /**
     * Deletes a specific inventory item from the system by its ID.
     *
     * @param id The unique identifier of the inventory item to be removed.
     * @return An empty {@link ResponseEntity} with a 204 NO CONTENT status.
     * @throws EntityNotFoundException if no inventory item is found matching the provided ID.
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete an inventory item",
            description = "Deletes an inventory item by its unique identifier.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "204",
                        description = "Inventory item successfully deleted",
                        content = @Content),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid inventory ID",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class)
                                )),
                @ApiResponse(
                        responseCode = "404",
                        description = "Inventory item not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class)
                                )),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class)
                                ))
            })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the inventory item to delete", example = "1", required = true) @PathVariable
                    Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
