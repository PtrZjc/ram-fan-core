package pl.zajacp.ramfancore.data.initializer;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.zajacp.ramfancore.data.fetcher.DataFetcher;
import pl.zajacp.ramfancore.data.model.RamDto;

@Component
@AllArgsConstructor
@Slf4j
public class DataInitializer {

    private final Set<DataFetcher<? extends RamDto>> dataFetchers;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        dataFetchers.forEach(fetcher -> {
            log.info("Initializing database with {}", fetcher.getClass().getSimpleName());
            fetcher.fetchDataAndSaveInDb();
        });
    }
}