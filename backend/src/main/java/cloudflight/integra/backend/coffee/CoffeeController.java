package cloudflight.integra.backend.coffee;

import cloudflight.integra.backend.coffee.model.CoffeeDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/coffees")
@Tag(name = "Coffee", description = "Endpoints for managing coffees")
public class CoffeeController {
    private final CoffeeService service;
    private final CoffeeMapper mapper;

    public CoffeeController(CoffeeService service, CoffeeMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "Get all coffees", description = "Retrieves a list of all available coffee items.")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved list of coffees",
            content =
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CoffeeDto.class))))
    public List<CoffeeDto> getAll() {
        return service.getAll().stream().map(mapper::toDto).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a coffee by ID", description = "Retrieves a single coffee item by its unique identifier.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved the coffee",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = CoffeeDto.class))),
                @ApiResponse(responseCode = "404", description = "Coffee not found", content = @Content)
            })
    public CoffeeDto getById(
            @Parameter(description = "ID of the coffee to be retrieved", required = true) @PathVariable Long id) {
        return service.getById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @Operation(summary = "Create a new coffee", description = "Adds a new coffee item to the system.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Coffee successfully created",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = CoffeeDto.class))),
                @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
            })
    public ResponseEntity<CoffeeDto> create(
            @Parameter(description = "Coffee object to create", required = true) @RequestBody CoffeeDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(service.create(mapper.toEntity(dto))));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing coffee", description = "Updates the coffee details for the given ID.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Coffee successfully updated",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = CoffeeDto.class))),
                @ApiResponse(responseCode = "404", description = "Coffee not found", content = @Content),
                @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
            })
    public CoffeeDto update(
            @Parameter(description = "ID of the coffee to be updated", required = true) @PathVariable Long id,
            @Parameter(description = "Updated coffee object data", required = true) @RequestBody CoffeeDto dto) {
        return service.update(id, mapper.toEntity(dto))
                .map(mapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a coffee", description = "Removes a coffee item by its unique identifier.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Coffee successfully deleted", content = @Content),
                @ApiResponse(responseCode = "404", description = "Coffee not found", content = @Content)
            })
    public void delete(
            @Parameter(description = "ID of the coffee to be deleted", required = true) @PathVariable Long id) {
        service.delete(id);
    }
}
