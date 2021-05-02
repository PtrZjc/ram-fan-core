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
import pl.zajacp.ramfancore.test.commons.AbstractTestDataRepository
import pl.zajacp.ramfancore.test.commons.EpisodeTestRepository
import pl.zajacp.ramfancore.test.infrastructure.JooqTestConfig
import pl.zajacp.ramfancore.test.infrastructure.TestContainer
import pl.zajacp.ramfancore.test.infrastructure.WiremockConfig
import spock.lang.*

import static pl.zajacp.ramfancore.model.tables.Episode.EPISODE


@Title("Validate fetching episode data from external api and saving it to db")
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
class EpisodeFetcherSpec extends Specification {

    @Autowired
    DSLContext jooq;

    @Autowired
    RestTemplate restTemplate;

    EpisodeFetcher episodeFetcher

    AbstractTestDataRepository testRepository

    def setup() {
        testRepository = new EpisodeTestRepository(jooq)
        episodeFetcher = new EpisodeFetcher(jooq, restTemplate)
        testRepository.clearDatabase()
    }

    def "Complete episode fetch starting from empty database"() {
        expect: "An initially empty database"
        jooq.select().from(EPISODE).fetch().size() == 0

        when: "A episode fetch is performed"
        episodeFetcher.fetchDataAndSaveInDb()

        then: "All episodes are saved in database"
        jooq.select(EPISODE.ID)
                .from(EPISODE)
                .orderBy(EPISODE.ID.asc())
                .fetch("id") == 1L..30L

        and: "First episode is Pilot"
        jooq.selectFrom(EPISODE)
                .where(EPISODE.ID.eq(1L))
                .fetchAny("name") == "Pilot"

        and: "Second episode is Lawnmower Dog"
        jooq.selectFrom(EPISODE)
                .where(EPISODE.ID.eq(2L))
                .fetchAny("name") == "Lawnmower Dog"
    }

    def "Episode fetch when database is already filled"() {
        given: "All episodes are initially present in database"
        testRepository.insertData(1L, 30L)
        def initialRecords = jooq.select().from(EPISODE).fetch().collect()

        when: "A episode fetch is performed"
        episodeFetcher.fetchDataAndSaveInDb()

        then: "No record is changed"
        initialRecords == jooq.select().from(EPISODE).fetch().collect()
    }

    def "Episode fetch when some episodes are already present"() {
        expect: "A partially filled database"
        testRepository.insertData(10L, 25L)
        def initialRecords = jooq.selectFrom(EPISODE).orderBy(EPISODE.ID.asc()).fetch().collect()

        when: "A episode fetch is performed"
        episodeFetcher.fetchDataAndSaveInDb()

        then: "All episodes are saved in database"
        jooq.select(EPISODE.ID)
                .from(EPISODE)
                .orderBy(EPISODE.ID.asc())
                .fetch("id") == 1L..30L

        and: "No update was made on records present initially"
        initialRecords == jooq.selectFrom(EPISODE)
                .where(EPISODE.ID.between(10L, 25L))
                .orderBy(EPISODE.ID.asc()).fetch().collect()
    }
}