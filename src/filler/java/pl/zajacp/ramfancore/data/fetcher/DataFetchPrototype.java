package pl.zajacp.ramfancore.data.fetcher;

import com.jayway.jsonpath.JsonPath;
import org.jooq.DSLContext;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import pl.zajacp.ramfancore.data.fetcher.model.CharacterDto;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.jooq.codegen.maven.example.tables.Character.CHARACTER;

@SpringBootTest
class DataFetchPrototype {

    @Autowired
    private DSLContext jooq;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String LINK = "https://rickandmortyapi.com/api/character";

    private final int pageSize = 20;

    @Test
    void prototype() throws JSONException {

//        Map<Integer, List<Integer>> characterToEpisodeRelations = new HashMap<>();

        Set<Integer> existingIds = jooq
                .select(CHARACTER.ID).from(CHARACTER)
                .fetch().stream()
                .map(record -> (Integer) record.get(0))
                .collect(Collectors.toSet());

        Integer objectCount = JsonPath.parse(restTemplate.getForObject(LINK, String.class)).read("$.info.count");

        Map<Integer, List<Integer>> missingRecordsByPageNumber = IntStream.rangeClosed(1, objectCount).boxed()
                .filter(id -> !existingIds.contains(id))
                .collect(Collectors.groupingBy(id -> (id - 1) / pageSize + 1,
                        Collectors.mapping(id -> id % pageSize - 1, Collectors.toList())));

        missingRecordsByPageNumber.forEach(this::fetchAndSaveInDb);
    }

    private void fetchAndSaveInDb(Integer pageNumber, List<Integer> recordIndex) {
        String response = restTemplate.getForObject(LINK + "/?page=" +
                pageNumber.toString(), String.class);

        List<CharacterDto> characters = new ArrayList<>();
        recordIndex.forEach(i -> characters.add(getCharacterDto(i, response)));
        insertCharactersIntoDb(characters);

        //TODO - relations
        //characterToEpisodeRelations.put(character.getId(), character.getEpisodeIds());
    }

    private Optional<Integer> getIdFromUrl(String url) {
        if (!"".equals(url)) {
            String[] segments = url.split("/");
            return Optional.of(Integer.parseInt(segments[segments.length - 1]));
        }
        return Optional.empty();
    }

    private CharacterDto getCharacterDto(Integer i, String response) {
        return CharacterDto.builder()
                .id((Integer) getResultObjectValue(response, i, "id"))
                .name((String) getResultObjectValue(response, i, "name"))
                .status((String) getResultObjectValue(response, i, "status"))
                .species((String) getResultObjectValue(response, i, "species"))
                .type((String) getResultObjectValue(response, i, "type"))
                .gender((String) getResultObjectValue(response, i, "gender"))
                .originId(getIdFromUrl((String) getResultObjectValue(response, i, "origin.url")))
                .locationId(getIdFromUrl((String) getResultObjectValue(response, i, "location.url")))
                .episodeIds(((List<String>) getResultObjectValue(response, i, "episode"))
                        .stream()
                        .map(this::getIdFromUrl)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList()))
                .imageBytes(restTemplate.getForObject((String) getResultObjectValue(response, i, "image"), byte[].class))
                .build();
    }

    private Object getResultObjectValue(String json, Integer index, String paramName) {
        return JsonPath.parse(json).read(new StringBuilder()
                .append("$.results.[")
                .append(index.toString())
                .append("].")
                .append(paramName)
                .toString());
    }

    private void insertCharactersIntoDb(List<CharacterDto> characters) {
        var multipleInsert = jooq.insertInto(CHARACTER, CHARACTER.ID, CHARACTER.NAME, CHARACTER.STATUS, CHARACTER.SPECIES,
                CHARACTER.TYPE, CHARACTER.GENDER, CHARACTER.ORIGIN_ID, CHARACTER.LOCATION_ID, CHARACTER.IMAGE);
        for (CharacterDto character : characters) {
            multipleInsert = multipleInsert.values(
                    character.getId(),
                    character.getName(),
                    character.getStatus(),
                    character.getSpecies(),
                    character.getType(),
                    character.getGender(),
                    character.getOriginId().orElseGet(() -> null),
                    character.getLocationId().orElseGet(() -> null),
                    character.getImageBytes()
            );
        }
        multipleInsert.execute();
    }
}