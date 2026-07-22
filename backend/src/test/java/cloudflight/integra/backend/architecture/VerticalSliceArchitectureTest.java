package cloudflight.integra.backend.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@Tag("architecture")
class VerticalSliceArchitectureTest {

    private static final String BASE_PACKAGE = "cloudflight.integra.backend";

    private final JavaClasses imported = new ClassFileImporter().importPackages(BASE_PACKAGE);

    @Test
    void controllersShouldNotDependOnRepositories() {
        ArchRuleDefinition.noClasses()
                .that()
                .areAnnotatedWith(RestController.class)
                .should()
                .dependOnClassesThat()
                .areAnnotatedWith(Repository.class)
                .check(imported);
    }

    @Test
    void servicesShouldNotDependOnControllersOrDtos() {
        ArchRuleDefinition.noClasses()
                .that()
                .areAnnotatedWith(Service.class)
                .should()
                .dependOnClassesThat()
                .areAnnotatedWith(RestController.class)
                .orShould()
                .dependOnClassesThat()
                .haveSimpleNameEndingWith("Dto")
                .check(imported);
    }

    @Test
    void repositoriesShouldNotDependOnServicesOrControllersOrDtos() {
        ArchRuleDefinition.noClasses()
                .that()
                .areAnnotatedWith(Repository.class)
                .should()
                .dependOnClassesThat()
                .areAnnotatedWith(Service.class)
                .orShould()
                .dependOnClassesThat()
                .areAnnotatedWith(RestController.class)
                .orShould()
                .dependOnClassesThat()
                .haveSimpleNameEndingWith("Dto")
                .check(imported);
    }

    @Test
    void controllersShouldNotDependOnOtherSlicesControllers() {
        ArchRuleDefinition.noClasses()
                .that()
                .areAnnotatedWith(RestController.class)
                .should()
                .dependOnClassesThat()
                .areAnnotatedWith(RestController.class)
                .check(imported);
    }

    @Test
    void repositoriesShouldNotDependOnOtherSlicesRepositories() {
        ArchRuleDefinition.noClasses()
                .that()
                .areAnnotatedWith(Repository.class)
                .should()
                .dependOnClassesThat()
                .areAnnotatedWith(Repository.class)
                .check(imported);
    }

    @Test
    void mappersShouldNotDependOnRepositories() {
        ArchRuleDefinition.noClasses()
                .that()
                .haveSimpleNameEndingWith("Mapper")
                .should()
                .dependOnClassesThat()
                .areAnnotatedWith(Repository.class)
                .check(imported);
    }

    @Test
    void mappersShouldNotDependOnServices() {
        ArchRuleDefinition.noClasses()
                .that()
                .haveSimpleNameEndingWith("Mapper")
                .should()
                .dependOnClassesThat()
                .areAnnotatedWith(Service.class)
                .check(imported);
    }
}
