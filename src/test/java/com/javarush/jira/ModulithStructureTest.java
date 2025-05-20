package com.javarush.jira;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

@Disabled
@ActiveProfiles("test")
@SpringBootTest
public class ModulithStructureTest {


    @Disabled("This structure is not modulith")
    @Test
    void verifyModularStructure() {
        ApplicationModules modules = ApplicationModules.of(JiraRushApplication.class);
        modules.verify();
        modules.forEach(System.out::println);
    }

    @Test
    void generateDocumentation() throws IOException {
        ApplicationModules modules = ApplicationModules.of(JiraRushApplication.class);
        new Documenter(modules)
                .writeDocumentation()
                .writeIndividualModulesAsPlantUml();
    }
}
