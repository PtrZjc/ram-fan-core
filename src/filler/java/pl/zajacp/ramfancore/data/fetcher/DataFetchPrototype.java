package pl.zajacp.ramfancore.data.fetcher;

import com.jayway.jsonpath.JsonPath;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import pl.zajacp.ramfancore.data.fetcher.model.CharacterDto;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

class DataFetchPrototype {

    private final RestTemplate restTemplate = new RestTemplate();
    private String link = "https://rickandmortyapi.com/api/character";

    @Test
    void prototype() throws JSONException {

        Map<Integer, List<Integer>> characterToEpisodeRelations = new HashMap<>();

        var nextPage = link;
        var pageNum = 0;
        var pages = 0;
        do {
            String response = restTemplate.getForObject(nextPage, String.class);

            nextPage = JsonPath.parse(response).read("$.info.next");
            pages = JsonPath.parse(response).read("$.info.pages");

            List<Integer> idListAtPage = JsonPath.parse(response).read("$.results..id");
            List<CharacterDto> characters = new ArrayList<>();

            LongStream.range(0, idListAtPage.size()).boxed()
                    .forEach(i -> {
                        characters.add(CharacterDto.builder()
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
                                .build());
                    });

            //                        characterToEpisodeRelations.put(character.getId(), character.getEpisodeIds());


            System.out.println(characters);
            pageNum++;
        } while (pageNum < 3);
//        } while (nextPage != null);
    }

    private Object getResultObjectValue(String json, Long index, String paramName) {
        return JsonPath.parse(json).read(new StringBuilder()
                .append("$.results.[")
                .append(index.toString())
                .append("].")
                .append(paramName)
                .toString());
    }

    private Optional<Integer> getIdFromUrl(String url) {
        if (!"".equals(url)) {
            String[] segments = url.split("/");
            return Optional.of(Integer.parseInt(segments[segments.length - 1]));
        }
        return Optional.empty();
    }
}