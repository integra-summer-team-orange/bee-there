package cloudflight.integra.backend.coffeemug;

import cloudflight.integra.backend.coffeemug.model.CoffeeMugDto;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/coffeemugs")
public class CoffeeMugController {
    private final CoffeeMugService service;
    private final CoffeeMugMapper mapper;

    public CoffeeMugController(CoffeeMugService service, CoffeeMugMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<CoffeeMugDto> getAll() {
        return service.getAll().stream().map(mapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public CoffeeMugDto getById(@PathVariable Long id) {
        return service.getById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<CoffeeMugDto> create(@RequestBody CoffeeMugDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(service.create(mapper.toEntity(dto))));
    }

    @PutMapping("/{id}")
    public CoffeeMugDto update(@PathVariable Long id, @RequestBody CoffeeMugDto dto) {
        return service.update(id, mapper.toEntity(dto))
                .map(mapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
