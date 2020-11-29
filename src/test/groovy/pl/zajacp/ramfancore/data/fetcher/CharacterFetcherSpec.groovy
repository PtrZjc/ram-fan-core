package pl.zajacp.ramfancore.data.fetcher


import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import org.testcontainers.spock.Testcontainers
import pl.zajacp.ramfancore.infrastructure.api.ApiConfig
import pl.zajacp.ramfancore.infrastructure.db.ChangeLogConfiguration
import pl.zajacp.ramfancore.test.commons.TestDataRepository
import pl.zajacp.ramfancore.test.infrastructure.JooqTestConfig
import pl.zajacp.ramfancore.test.infrastructure.TestContainer
import pl.zajacp.ramfancore.test.infrastructure.WiremockConfig
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Title

import java.nio.charset.StandardCharsets

import static pl.zajacp.ramfancore.model.tables.Character.CHARACTER

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

    def JPG_HEADER_IDENTIFIER = "JFIF"

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

        then: "All characters are saved in database"
        jooq.select(CHARACTER.ID)
                .from(CHARACTER)
                .orderBy(CHARACTER.ID.asc())
                .fetch("id") == 1L..30L

        and: "First character is Rick"
        jooq.selectFrom(CHARACTER)
                .where(CHARACTER.ID.eq(1L))
                .fetchAny("name") == "Rick Sanchez"

        and: "Second character is Morty"
        jooq.selectFrom(CHARACTER)
                .where(CHARACTER.ID.eq(2L))
                .fetchAny("name") == "Morty Smith"

        and: "Each character has proper avatar image saved"
        def avatarFileHeaders = jooq.select(CHARACTER.IMAGE)
                .from(CHARACTER)
                .fetch("image")
                .collect { avatarImage -> getFileHeaderText(avatarImage) }

        avatarFileHeaders.every { it.contains JPG_HEADER_IDENTIFIER } //jpg unique identifier in file header
    }

    def "Character fetch when database is already filled"() {
        given: "All characters are initially present in database"
        testRepository.insertCharacters(1L, 30L)
        def initialRecords = jooq.select().from(CHARACTER).fetch().collect()

        when: "A character fetch is performed"
        characterFetcher.fetchDataAndSaveInDb()

        then: "No record is changed"
        initialRecords == jooq.select().from(CHARACTER).fetch().collect()
    }

    def "Character fetch when some characters are already present"() {
        expect: "A partially filled database"
        testRepository.insertCharacters(10L, 25L)
        def initialRecords = jooq.selectFrom(CHARACTER).orderBy(CHARACTER.ID.asc()).fetch().collect()

        when: "A character fetch is performed"
        characterFetcher.fetchDataAndSaveInDb()

        then: "All characters are saved in database"
        jooq.select(CHARACTER.ID)
                .from(CHARACTER)
                .orderBy(CHARACTER.ID.asc())
                .fetch("id") == 1L..30L

        and: "No update was made on records present initially"
        initialRecords == jooq.selectFrom(CHARACTER)
                .where(CHARACTER.ID.between(10L, 25L))
                .orderBy(CHARACTER.ID.asc()).fetch().collect()
    }

    private static def getFileHeaderText(byte[] bytes) {
        return new String(bytes, 0, 20, StandardCharsets.UTF_8)
    }
}