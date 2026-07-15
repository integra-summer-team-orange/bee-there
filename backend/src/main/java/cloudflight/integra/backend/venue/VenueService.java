package cloudflight.integra.backend.venue;

import cloudflight.integra.backend.venue.model.Venue;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VenueService {
    private final VenueRepository repository;

    public VenueService(VenueRepository repository) {
        this.repository = repository;
    }

    public List<Venue> getAll() {
        return repository.findAll();
    }

    public Optional<Venue> getById(Long id) {
        return repository.findById(id);
    }

    public Venue create(Venue venue) {
        return repository.save(venue);
    }

    public Venue update(Long id, Venue venue) {
        venue.setId(id);
        return repository.update(venue);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
