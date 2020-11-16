package pl.zajacp.ramfancore.test.infrastructure;

import javax.sql.DataSource;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class JooqTestConfig {

    @Primary
    @Bean
    public DataSource testContainerDataSource() {
        return DataSourceBuilder.create()
                .url(TestContainer.getInstance().getJdbcUrl())
                .password(TestContainer.getInstance().getPassword())
                .username(TestContainer.getInstance().getUsername())
                .build();
    }

    @Primary
    @Bean
    public DefaultDSLContext testDsl() {
        return new DefaultDSLContext(testContainerDataSource(), SQLDialect.valueOf("POSTGRES"));
    }
}