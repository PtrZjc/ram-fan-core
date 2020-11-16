package pl.zajacp.ramfancore.test.infrastructure;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class TestContainer {

    private static PostgreSQLContainer container;

    public static PostgreSQLContainer getInstance() {
        if (container == null) {
            container = new PostgreSQLContainer("postgres:9.6")
                    .withPassword("postgres")
                    .withUsername("postgres");
            container.start();
        }
        return container;
    }

    public static class TestContainersInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            getInstance();
            TestPropertyValues values = TestPropertyValues.of(
                    "spring.datasource.url=" + container.getJdbcUrl(),
                    "spring.datasource.username=" + container.getUsername(),
                    "spring.datasource.password=" + container.getPassword(),
                    "spring.datasource.driver-class-name=org.postgresql.Driver",
                    "spring.jpa.database-platform=org.hibernate.dialect.PostgreSQL9Dialect",
                    "spring.liquibase.default-data-schema=ram_data",
                    "spring.liquibase.default-liquibase-schema=public",
                    "spring.liquibase.change-log=classpath:/db/changelog.xml"
            );
            values.applyTo(configurableApplicationContext);
        }
    }
}
