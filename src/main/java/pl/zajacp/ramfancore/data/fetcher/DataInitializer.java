package pl.zajacp.ramfancore.data.fetcher;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
            System.out.println("here I will trigger fetching char/episode/location and set up the relations");
    }
}