package pl.zajacp.ramfancore.infrastructure.db;

import lombok.Setter;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Configuration
public class PersistenceConfiguration {

    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public DefaultDSLContext dsl(JooqProperties jooqProps) {
        return new DefaultDSLContext(dataSource(), SQLDialect.valueOf(jooqProps.sqlDialect));
    }

    @Component
    @EnableConfigurationProperties
    @ConfigurationProperties(prefix = "spring.jooq")
    @Setter
    private class JooqProperties {
        private String sqlDialect;
    }

    @Bean
    public ExceptionTranslator exceptionTransformer() {
        return new ExceptionTranslator();
    }
}
