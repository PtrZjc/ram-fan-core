package pl.zajacp.ramfancore.data.fetcher

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import org.testcontainers.spock.Testcontainers
import pl.zajacp.ramfancore.infrastructure.api.ApiConfig
import pl.zajacp.ramfancore.infrastructure.db.ChangeLogConfiguration
import pl.zajacp.ramfancore.model.tables.Character
import pl.zajacp.ramfancore.test.commons.TestDataRepository
import pl.zajacp.ramfancore.test.infrastructure.JooqTestConfig
import pl.zajacp.ramfancore.test.infrastructure.TestContainer
import pl.zajacp.ramfancore.test.infrastructure.WiremockConfig
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Title

import static pl.zajacp.ramfancore.model.tables.Character.*

@Title("Validate fetching character data from external api and saving it to db")
@Narrative("""
This test relies on prepared wiremock stubs where total count of all records is 30.
To check complete fetch and validate scenario, the prepared records span across two
20-record pages.
""")
@Subject(CharacterFetcher)

@Testcontainers
@SpringBootTest(classes = [JooqTestConfig.class, WiremockConfig.class, ApiConfig.class, ChangeLogConfiguration.class])
@AutoConfigureWireMock(port = 9998)
@ContextConfiguration(initializers = TestContainer.TestContainersInitializer.class)
class CharacterFetcherSpec extends Specification {

    @Autowired
    DSLContext jooq;

    @Autowired
    RestTemplate restTemplate;

    TestDataRepository testRepository

    CharacterFetcher characterFetcher

    def setup() {
        testRepository = new TestDataRepository(jooq)
        characterFetcher = new CharacterFetcher(jooq, restTemplate)
        testRepository.clearDatabase()
    }

    def "Complete character fetch starting from empty database"() {
        expect: "An initially empty database"
        jooq.select().from(CHARACTER).fetch().size() == 0

        when: "A character fetch is performed"
        characterFetcher.fetchDataAndSaveInDb()

        then: "30 records are saved in database"
        jooq.select().from(CHARACTER).fetch().size() == 30

        then: "Character ids are complete"

        then: "First character is Rick"

        then: "Second character is Morty"

        then: "Each character has proper avatar image saved"

    }
}