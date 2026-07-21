package cloudflight.integra.backend.venue;

import cloudflight.integra.backend.venue.model.Venue;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class VenueRepository {
    private final Map<Long, Venue> venues = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public List<Venue> findAll() {
        return new ArrayList<>(venues.values());
    }

    public Optional<Venue> findById(Long id) {
        return Optional.ofNullable(venues.get(id));
    }

    public Venue save(Venue venue) {
        if (venue.getId() == null) {
            venue.setId(idGen.getAndIncrement());
            venue.setCreatedAt(LocalDateTime.now());
        }
        venues.put(venue.getId(), venue);
        return venue;
    }

    public Venue update(Venue venue) {
        venues.put(venue.getId(), venue);
        return venue;
    }

    public void deleteById(Long id) {
        venues.remove(id);
    }
}
