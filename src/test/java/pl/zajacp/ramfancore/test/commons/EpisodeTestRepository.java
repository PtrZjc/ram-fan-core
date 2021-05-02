package pl.zajacp.ramfancore.test.commons;

import org.jooq.DSLContext;

import static pl.zajacp.ramfancore.model.tables.Episode.EPISODE;
import static pl.zajacp.ramfancore.test.commons.TestDataFactory.DataPath.SQL_INSERT_30_EPISODES;
import static pl.zajacp.ramfancore.test.commons.TestDataFactory.getContent;

public class EpisodeTestRepository extends AbstractTestDataRepository {

    public EpisodeTestRepository(DSLContext jooq) {
        super(jooq, EPISODE);
    }

    public void insertData(long lowerIdBound, long upperIdBoundIncl) {
        assert lowerIdBound > 0 && lowerIdBound < 30;
        assert upperIdBoundIncl > 1 && upperIdBoundIncl <= 30;

        jooq.execute(getContent(SQL_INSERT_30_EPISODES));

        if (lowerIdBound > 1) {
            jooq.delete(EPISODE).where(EPISODE.ID.between(1L, lowerIdBound - 1)).execute();
        }
        if (upperIdBoundIncl < 30) {
            jooq.delete(EPISODE).where(EPISODE.ID.between(upperIdBoundIncl + 1, 30L)).execute();
        }
    }
}
