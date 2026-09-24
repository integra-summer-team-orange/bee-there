package cloudflight.integra.backend.reservation;

import cloudflight.integra.backend.authentication.config.SecurityUtils;
import cloudflight.integra.backend.exceptions.ErrorResponse;
import cloudflight.integra.backend.reservation.model.Reservation;
import cloudflight.integra.backend.reservation.model.ReservationRequestDto;
import cloudflight.integra.backend.reservation.model.ReservationResponseDto;
import cloudflight.integra.backend.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller that exposes operations for managing reservations.
 */
@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservations", description = "Endpoints for creating, retrieving, and cancelling reservations.")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;

    /**
     * Creates a new reservation controller.
     *
     * @param reservationService the reservation service
     * @param reservationMapper the reservation mapper
     */
    public ReservationController(ReservationService reservationService, ReservationMapper reservationMapper) {
        this.reservationService = reservationService;
        this.reservationMapper = reservationMapper;
    }

    /**
     * Retrieves a paginated list of reservations belonging to the authenticated user.
     *
     * @param pageNumber the zero-based page index to retrieve
     * @param pageSize the maximum number of reservations to return per page
     * @param venueId optional venue identifier used to filter reservations
     * @return a response containing the requested page of reservations
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Gets a page of reservations",
            description = "Returns the authenticated user's reservations as organizer. "
                    + "Optionally filters reservations by venue.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Paginated reservations retrieved successfully"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid pagination parameters",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error occurred",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<Page<ReservationResponseDto>> getAllReservations(
            @Parameter(description = "Number of the desired page (0-based index)", example = "0", required = true)
                    @RequestParam(defaultValue = "0")
                    int pageNumber,
            @Parameter(description = "Size of page", example = "10", required = true) @RequestParam(defaultValue = "10")
                    int pageSize,
            @Parameter(description = "Optional venue ID used to filter reservations", example = "1")
                    @RequestParam(required = false)
                    Long venueId) {
        User user = SecurityUtils.getCurrentUser();

        Page<Reservation> reservations = reservationService.getAll(pageNumber, pageSize, user.getId(), venueId);

        return ResponseEntity.ok(reservations.map(reservationMapper::toDto));
    }

    /**
     * Retrieves a reservation by its unique identifier.
     *
     * @param id the identifier of the reservation
     * @return a response containing the requested reservation
     */
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get a reservation by ID",
            description = "Retrieves a single reservation by its unique identifier.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Reservation found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ReservationResponseDto.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Reservation not found",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<ReservationResponseDto> getReservationById(
            @Parameter(description = "ID of the reservation to be retrieved", required = true) @PathVariable Long id) {
        Reservation reservation = reservationService.getById(id);

        return ResponseEntity.ok(reservationMapper.toDto(reservation));
    }

    /**
     * Creates a new reservation for the authenticated user.
     *
     * @param dto the reservation data
     * @return a response containing the created reservation
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Create a new reservation",
            description = "Creates a reservation for the authenticated user. "
                    + "The organizer is taken from the authenticated user and is not provided in the request.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Reservation successfully created",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ReservationResponseDto.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid reservation data or overlapping reservation exists",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Referenced resource or venue not found",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<ReservationResponseDto> createReservation(@RequestBody @Valid ReservationRequestDto dto) {
        User user = SecurityUtils.getCurrentUser();

        Reservation reservation = reservationMapper.toEntity(dto);
        reservation.setOrganizer(user);
        Reservation createdReservation = reservationService.create(reservation);

        return ResponseEntity.status(HttpStatus.CREATED).body(reservationMapper.toDto(createdReservation));
    }

    /**
     * Cancels a reservation.
     *
     * @param id the identifier of the reservation to cancel
     * @return a response indicating that the reservation was successfully cancelled
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Cancel a reservation",
            description = "Cancels a reservation. Only the organizer of the reservation can cancel it.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "204",
                        description = "Reservation successfully cancelled",
                        content = @Content),
                @ApiResponse(
                        responseCode = "403",
                        description = "User is not the organizer of the reservation",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Reservation not found",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<Void> deleteReservation(
            @Parameter(description = "ID of the reservation to be cancelled", required = true) @PathVariable Long id) {
        User user = SecurityUtils.getCurrentUser();

        reservationService.delete(id, user.getId());

        return ResponseEntity.noContent().build();
    }
}
