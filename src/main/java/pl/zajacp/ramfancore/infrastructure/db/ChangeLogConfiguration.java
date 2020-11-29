package pl.zajacp.ramfancore.infrastructure.db;

import liquibase.integration.spring.SpringLiquibase;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;

@Configuration
@Slf4j
public class ChangeLogConfiguration {

    @Bean
    public SpringLiquibase getSpringLiquibase(DataSource dataSource, LiquibaseProperties liquibaseProperties) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog(liquibaseProperties.changeLog);
        liquibase.setDataSource(dataSource);
        liquibase.setDefaultSchema(liquibaseProperties.defaultDataSchema);
        liquibase.setLiquibaseSchema(liquibaseProperties.defaultLiquibaseSchema);
        try (Connection connection = dataSource.getConnection()) {
            liquibase.setChangeLogParameters(Collections.singletonMap("user_name", connection.getMetaData().getUserName()));
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return liquibase;
    }

    @Component
    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "spring.liquibase")
    @Setter
    private class LiquibaseProperties {
        private String changeLog;
        private String defaultDataSchema;
        private String defaultLiquibaseSchema;
    }
}