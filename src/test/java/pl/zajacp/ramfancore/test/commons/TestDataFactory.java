package pl.zajacp.ramfancore.test.commons;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static java.lang.String.join;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Files.readAllLines;
import static pl.zajacp.ramfancore.test.commons.TestDataFactory.DataPath.JPG_CHARACTER_IMAGE_FOLDER;

public class TestDataFactory {

    @Getter
    @AllArgsConstructor
    public enum DataPath {
        API_CHARACTER_PAGE_1("target/test-classes/__files/ram-external/character-page1.json"),
        API_CHARACTER_PAGE_2("target/test-classes/__files/ram-external/character-page2.json"),
        API_EPISODE_PAGE_1("target/test-classes/__files/ram-external/episode-page1.json"),
        API_EPISODE_PAGE_2("target/test-classes/__files/ram-external/episode-page2.json"),
        API_LOCATION_PAGE_1("target/test-classes/__files/ram-external/location-page1.json"),
        API_LOCATION_PAGE_2("target/test-classes/__files/ram-external/location-page2.json"),
        SQL_INSERT_30_CHARACTERS("target/test-classes/sql/insert-30-characters.sql"),
        JPG_CHARACTER_IMAGE_FOLDER("target/test-classes/__files/ram-external/image/character/");

        private String filePath;
    }

    public static String getContent(DataPath dataPath) {
        return contentOfFile(dataPath.getFilePath());
    }

    private static String contentOfFile(String path) {
        return Try.of(() -> join("\n", readAllLines(Path.of(path))))
                .getOrElseThrow((Supplier<RuntimeException>) RuntimeException::new);
    }

    public static List<Tuple2<Integer, byte[]>> getCharacterImagesAsBytes(int lowerIdBound, int upperIdBoundIncl) {
        return IntStream.rangeClosed(lowerIdBound, upperIdBoundIncl).boxed()
                .map(TestDataFactory::getCharacterImage)
                .collect(Collectors.toList());
    }

    public static byte[] getCharacterImageAsBytes(int characterId) {
        return getByteArrayFromFile(JPG_CHARACTER_IMAGE_FOLDER.getFilePath() + characterId + ".jpg");
    }

    private static Tuple2<Integer, byte[]> getCharacterImage(int characterId) {
        return Tuple.of(characterId, getCharacterImageAsBytes(characterId));
    }

    private static byte[] getByteArrayFromFile(String path) {
        return Try.of(() -> readAllBytes(Paths.get(path)))
                .getOrElseThrow((Supplier<RuntimeException>) RuntimeException::new);
    }
}
