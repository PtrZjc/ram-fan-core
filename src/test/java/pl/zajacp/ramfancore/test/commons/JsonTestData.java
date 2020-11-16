package pl.zajacp.ramfancore.test.commons;

import io.vavr.control.Try;
import java.nio.file.Paths;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static java.nio.file.Files.readAllLines;

public class JsonTestData {

    @Getter
    @AllArgsConstructor
    public enum JsonResponse {
        API_CHARACTER_PAGE_1("target/test-classes/data/ext/character-page1.json"),
        API_CHARACTER_PAGE_2("target/test-classes/data/ext/character-page2.json"),
        API_EPISODE_PAGE_1("target/test-classes/data/ext/episode-page1.json"),
        API_EPISODE_PAGE_2("target/test-classes/data/ext/episode-page2.json"),
        API_LOCATION_PAGE_1("target/test-classes/data/ext/location-page1.json"),
        API_LOCATION_PAGE_2("target/test-classes/data/ext/location-page2.json");

        private String filePath;
    }

    public static String getJsonResponse(JsonResponse jsonResponse) {
        return contentOfFile(jsonResponse.getFilePath());
    }

    private static String contentOfFile(String path) {
        return Try.of(() -> String.join("\n", readAllLines(Paths.get(path))))
                .getOrElseThrow((Supplier<RuntimeException>) RuntimeException::new);
    }
}
