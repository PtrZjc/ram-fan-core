package pl.zajacp.ramfancore.test.commons;

import java.util.List;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.TableImpl;

import static pl.zajacp.ramfancore.model.tables.Character.CHARACTER;
import static pl.zajacp.ramfancore.model.tables.Episode.EPISODE;
import static pl.zajacp.ramfancore.model.tables.Location.LOCATION;

@AllArgsConstructor
public class TestDataRepository {

    private final DSLContext jooq;

    private final List<TableImpl<?>> TABLES = List.of(CHARACTER, EPISODE, LOCATION);

    public void clearDatabase() {
        TABLES.forEach(table -> jooq.truncate(table).cascade().execute());
    }

}
