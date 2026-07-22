package cloudflight.integra.backend.dbcheck;

import cloudflight.integra.backend.dbcheck.model.CoffeeBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoffeeBeanRepository extends JpaRepository<CoffeeBean, Long> {}

