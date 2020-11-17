package pl.zajacp.ramfancore.data.fetcher

import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.spock.Testcontainers
import pl.zajacp.ramfancore.test.infrastructure.JooqTestConfig
import pl.zajacp.ramfancore.test.infrastructure.TestContainer
import spock.lang.Specification

@Testcontainers
@SpringBootTest(classes = JooqTestConfig.class)
@ContextConfiguration(initializers = TestContainer.TestContainersInitializer.class)
class IntegrationJooqSpec extends Specification {

    @Autowired
    private final DSLContext jooq;

    def "check if context is loading"() {
        expect:
        true
    }

}