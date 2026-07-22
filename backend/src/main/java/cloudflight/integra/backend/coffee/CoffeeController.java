package cloudflight.integra.backend.coffee;

import cloudflight.integra.backend.coffee.model.CoffeeDto;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/coffees")
public class CoffeeController {
    private final CoffeeService service;
    private final CoffeeMapper mapper;

    public CoffeeController(CoffeeService service, CoffeeMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<CoffeeDto> getAll() {
        return service.getAll().stream().map(mapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public CoffeeDto getById(@PathVariable Long id) {
        return service.getById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<CoffeeDto> create(@RequestBody CoffeeDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(service.create(mapper.toEntity(dto))));
    }

    @PutMapping("/{id}")
    public CoffeeDto update(@PathVariable Long id, @RequestBody CoffeeDto dto) {
        return service.update(id, mapper.toEntity(dto))
                .map(mapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
