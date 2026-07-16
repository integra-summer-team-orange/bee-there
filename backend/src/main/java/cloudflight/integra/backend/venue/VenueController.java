package cloudflight.integra.backend.venue;

import cloudflight.integra.backend.exceptions.EntityNotFoundException;
import cloudflight.integra.backend.venue.model.VenueRequestDto;
import cloudflight.integra.backend.venue.model.VenueResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
public class VenueController {
    private final VenueService service;
    private final VenueMapper mapper;

    public VenueController(VenueService service, VenueMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<VenueResponseDto>> getAll() {
        return ResponseEntity.ok(
            service.getAll().stream().map(mapper::toResponseDto).toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenueResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
            service.getById(id).map(mapper::toResponseDto)
            .orElseThrow(() -> new EntityNotFoundException("Venue with id: " + id + " not found!"))
        );
    }

    @PostMapping
    public ResponseEntity<VenueResponseDto> create(@RequestBody @Valid VenueRequestDto dto) {
        VenueResponseDto createdVenue = mapper.toResponseDto(service.create(mapper.toEntity(dto)));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVenue);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VenueResponseDto> update(@PathVariable Long id, @RequestBody @Valid VenueRequestDto dto) {
        return ResponseEntity.ok(
            mapper.toResponseDto(service.update(id, mapper.toEntity(dto)))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
