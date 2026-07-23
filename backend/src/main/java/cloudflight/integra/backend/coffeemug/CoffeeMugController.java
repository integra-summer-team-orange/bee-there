package cloudflight.integra.backend.coffeemug;

import cloudflight.integra.backend.coffeemug.model.CoffeeMugDto;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller that exposes CRUD operations for managing coffee mugs.
 */
@RestController
@RequestMapping("/api/coffeemugs")
public class CoffeeMugController {
    private final CoffeeMugService service;
    private final CoffeeMugMapper mapper;

    /**
     * Creates a new coffee mug controller.
     *
     * @param service the coffee mug service
     * @param mapper the coffee mug mapper
     */
    public CoffeeMugController(CoffeeMugService service, CoffeeMugMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /**
     * Retrieves all coffee mugs.
     *
     * @return the list of all coffee mugs
     */
    @GetMapping
    public List<CoffeeMugDto> getAll() {
        return service.getAll().stream().map(mapper::toDto).toList();
    }

    /**
     * Retrieves a coffee mug by its unique identifier.
     *
     * @param id the identifier of the coffee mug to retrieve
     * @return the requested coffee mug
     * @throws ResponseStatusException if no coffee mug with the specified identifier exists
     */
    @GetMapping("/{id}")
    public CoffeeMugDto getById(@PathVariable Long id) {
        return service.getById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * Creates a new coffee mug.
     *
     * @param dto the coffee mug data used to create the new coffee mug
     * @return a response containing the created coffee mug
     */
    @PostMapping
    public ResponseEntity<CoffeeMugDto> create(@RequestBody CoffeeMugDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(service.create(mapper.toEntity(dto))));
    }

    /**
     * Updates an existing coffee mug.
     *
     * @param id the identifier of the coffee mug to update
     * @param dto the updated coffee mug data
     * @return the updated coffee mug
     * @throws ResponseStatusException if no coffee mug with the specified identifier exists
     */
    @PutMapping("/{id}")
    public CoffeeMugDto update(@PathVariable Long id, @RequestBody CoffeeMugDto dto) {
        return service.update(id, mapper.toEntity(dto))
                .map(mapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    /**
     * Deletes a coffee mug by its unique identifier.
     *
     * @param id the identifier of the coffee mug to delete
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
