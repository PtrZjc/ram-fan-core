package pl.zajacp.ramfancore.test.commons;

import io.vavr.Tuple2;
import org.jooq.DSLContext;
import org.jooq.UpdateConditionStep;
import org.jooq.impl.TableImpl;
import pl.zajacp.ramfancore.model.tables.records.CharacterRecord;

import static java.lang.Long.valueOf;
import static java.util.stream.Collectors.toList;
import static pl.zajacp.ramfancore.model.tables.Character.CHARACTER;
import static pl.zajacp.ramfancore.test.commons.TestDataFactory.DataPath.SQL_INSERT_30_CHARACTERS;
import static pl.zajacp.ramfancore.test.commons.TestDataFactory.getCharacterImagesAsBytes;
import static pl.zajacp.ramfancore.test.commons.TestDataFactory.getContent;

public class CharacterTestRepository extends AbstractTestDataRepository {

    public CharacterTestRepository(DSLContext jooq) {
        super(jooq, CHARACTER);
    }

    public void insertData(long lowerIdBound, long upperIdBoundIncl) {
        assert lowerIdBound > 0 && lowerIdBound < 30;
        assert upperIdBoundIncl > 1 && upperIdBoundIncl <= 30;

        jooq.execute(getContent(SQL_INSERT_30_CHARACTERS));
        jooq.batch(
                getCharacterImagesAsBytes(lowerIdBound, upperIdBoundIncl)
                        .stream()
                        .map(this::prepareSingleImageUpdate)
                        .collect(toList()))
                .execute();

        if (lowerIdBound > 1) {
            jooq.delete(CHARACTER).where(CHARACTER.ID.between(1L, lowerIdBound - 1)).execute();
        }
        if (upperIdBoundIncl < 30) {
            jooq.delete(CHARACTER).where(CHARACTER.ID.between(upperIdBoundIncl + 1, 30L)).execute();
        }
    }

    private UpdateConditionStep<CharacterRecord> prepareSingleImageUpdate(Tuple2<Long, byte[]> characterImage) {
        return jooq.update(CHARACTER)
                .set(CHARACTER.IMAGE, characterImage._2)
                .where(CHARACTER.ID.eq(valueOf(characterImage._1)));
    }
}
