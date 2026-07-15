package cloudflight.integra.backend.venue;

import cloudflight.integra.backend.exceptions.EntityNotFoundException;
import cloudflight.integra.backend.venue.model.VenueRequestDto;
import cloudflight.integra.backend.venue.model.VenueResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
    public List<VenueResponseDto> getAll() {
        return service.getAll().stream().map(mapper::toResponseDto).toList();
    }

    @GetMapping("/{id}")
    public VenueResponseDto getById(@PathVariable Long id) {
        return service.getById(id).map(mapper::toResponseDto)
            .orElseThrow(() -> new EntityNotFoundException("Venue with id: " + id + " not found!"));
    }

    @PostMapping
    public ResponseEntity<VenueResponseDto> create(@RequestBody @Valid VenueRequestDto dto) {
        VenueResponseDto createdVenue = mapper.toResponseDto(service.create(mapper.toEntity(dto)));

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(createdVenue.id())
            .toUri();

        return ResponseEntity.created(location).body(createdVenue);
    }

    @PutMapping("/{id}")
    public VenueResponseDto update(@PathVariable Long id, @RequestBody @Valid VenueRequestDto dto) {
        return mapper.toResponseDto(service.update(id, mapper.toEntity(dto)));//?
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
