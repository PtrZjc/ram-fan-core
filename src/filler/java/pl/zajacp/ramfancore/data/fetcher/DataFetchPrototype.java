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
import java.util.stream.LongStream;

import static org.jooq.codegen.maven.example.tables.Character.CHARACTER;

@SpringBootTest
class DataFetchPrototype {

    @Autowired
    private DSLContext jooq;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String LINK = "https://rickandmortyapi.com/api/character";

    @Test
    void prototype() throws JSONException {

//        Map<Integer, List<Integer>> characterToEpisodeRelations = new HashMap<>();

        var nextPage = LINK;
        var pageNum = 0;
        var pages = 0;
        do {
            String response = restTemplate.getForObject(nextPage, String.class);

            nextPage = JsonPath.parse(response).read("$.info.next");
            pages = JsonPath.parse(response).read("$.info.pages");

            List<Integer> idListAtPage = JsonPath.parse(response).read("$.results..id");
            List<CharacterDto> characters = new ArrayList<>();

            LongStream.range(0, idListAtPage.size()).boxed()
                    .forEach(i -> characters.add(getCharacterDto(i, response)));

            characters.forEach(this::insertCharactersIntoDb);

            //TODO - relations
            //characterToEpisodeRelations.put(character.getId(), character.getEpisodeIds());

            System.out.println(characters);
            pageNum++;
        } while (nextPage != null);
    }

    private Optional<Integer> getIdFromUrl(String url) {
        if (!"".equals(url)) {
            String[] segments = url.split("/");
            return Optional.of(Integer.parseInt(segments[segments.length - 1]));
        }
        return Optional.empty();
    }

    private CharacterDto getCharacterDto(Long i, String response) {
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

    private Object getResultObjectValue(String json, Long index, String paramName) {
        return JsonPath.parse(json).read(new StringBuilder()
                .append("$.results.[")
                .append(index.toString())
                .append("].")
                .append(paramName)
                .toString());
    }

    private void insertCharactersIntoDb(CharacterDto characterDto) {
        jooq.insertInto(CHARACTER)
                .set(CHARACTER.ID, characterDto.getId())
                .set(CHARACTER.NAME, characterDto.getName())
                .set(CHARACTER.STATUS, characterDto.getStatus())
                .set(CHARACTER.SPECIES, characterDto.getSpecies())
                .set(CHARACTER.TYPE, characterDto.getType())
                .set(CHARACTER.GENDER, characterDto.getGender())
                .set(CHARACTER.ORIGIN_ID, characterDto.getOriginId().orElseGet(() -> null))
                .set(CHARACTER.LOCATION_ID, characterDto.getLocationId().orElseGet(() -> null))
                .set(CHARACTER.IMAGE, characterDto.getImageBytes())
                .execute();
    }
}