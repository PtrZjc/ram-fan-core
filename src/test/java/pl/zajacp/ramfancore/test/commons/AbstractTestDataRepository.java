package pl.zajacp.ramfancore.test.commons;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.TableImpl;

@AllArgsConstructor
public abstract class AbstractTestDataRepository {

    protected final DSLContext jooq;
    protected final TableImpl<?> TABLE;

    public void clearDatabase() {
        jooq.truncate(TABLE).cascade().execute();
    }

    public abstract void insertData(long lowerIdBound, long upperIdBoundIncl);
}
