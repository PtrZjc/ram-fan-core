package pl.zajacp.ramfancore.data.fetcher;

import com.jayway.jsonpath.JsonPath;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.LongStream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.zajacp.ramfancore.data.fetcher.model.CharacterDto;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static pl.zajacp.ramfancore.model.tables.Character.CHARACTER;

@Service
@AllArgsConstructor
@Slf4j
class CharacterFetcher {

    private final DSLContext jooq;
    private final RestTemplate restTemplate;

    private final String RESOURCE = "/character/";
    private final int PAGE_SIZE = 20;

    void fetchDataAndSaveInDb() {

        Set<Long> existingIds = jooq
                .select(CHARACTER.ID).from(CHARACTER)
                .fetch().stream()
                .map(record -> (Long) record.get(0))
                .collect(toSet());

        Long objectCount = Long.valueOf(
                JsonPath.parse(restTemplate.getForObject(RESOURCE, String.class)).read("$.info.count").toString());

        Map<Long, List<Long>> missingRecordsByPageNumber = LongStream.rangeClosed(1, objectCount).boxed()
                .filter(id -> !existingIds.contains(id))
                .collect(groupingBy(id -> (id - 1) / PAGE_SIZE + 1, mapping(id -> id % PAGE_SIZE - 1, toList())));

        missingRecordsByPageNumber.forEach(this::fetchAndSaveInDb);
    }

    private void fetchAndSaveInDb(Long pageNumber, List<Long> recordIndex) {
        String response = restTemplate.getForObject(RESOURCE + "/?page=" +
                pageNumber.toString(), String.class);

        List<CharacterDto> characters = new ArrayList<>();
        recordIndex.forEach(i -> characters.add(getCharacterDto(i, response)));
        insertCharactersIntoDb(characters);

        //TODO - relations
        //characterToEpisodeRelations.put(character.getId(), character.getEpisodeIds());
    }

    private Optional<Long> getIdFromUrl(String url) {
        if (!"".equals(url)) {
            String[] segments = url.split("/");
            return Optional.of(Long.valueOf(segments[segments.length - 1]));
        }
        return Optional.empty();
    }

    private CharacterDto getCharacterDto(Long i, String response) {
        return CharacterDto.builder()
                .id(Long.valueOf(getResultObjectValue(response, i, "id").toString()))
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
                        .collect(toList()))
                .imageBytes(restTemplate.getForObject((String) getResultObjectValue(response, i, "image"), byte[].class))
                .build();
    }

    private Object getResultObjectValue(String json, Long index, String paramName) {
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