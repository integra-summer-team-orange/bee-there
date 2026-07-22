package cloudflight.integra.backend.dbcheck;

import static org.assertj.core.api.Assertions.assertThat;

import cloudflight.integra.backend.dbcheck.model.CoffeeBean;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class CoffeeBeanRepositoryTest {

    @Autowired
    private CoffeeBeanRepository coffeeBeanRepository;

    @Test
    void savesAndReadsBackACoffeeBean() {
        CoffeeBean saved = coffeeBeanRepository.save(new CoffeeBean("Arabica"));

        CoffeeBean found = coffeeBeanRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getOrigin()).isEqualTo("Arabica");
    }
}
