package cloudflight.integra.backend.resources;

import cloudflight.integra.backend.resources.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing resource entities.
 */
@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {}
