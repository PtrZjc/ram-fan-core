package pl.zajacp.ramfancore.data.fetcher;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class DataInitializer {

    private final CharacterFetcher characterFetcher;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Initializing characters in the database");
        characterFetcher.fetchDataAndSaveInDb();
    }
}