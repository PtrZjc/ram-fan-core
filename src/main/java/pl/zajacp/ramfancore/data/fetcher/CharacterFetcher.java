package pl.zajacp.ramfancore.data.fetcher;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.zajacp.ramfancore.data.model.CharacterDto;

import static java.util.stream.Collectors.toList;
import static pl.zajacp.ramfancore.model.tables.Character.CHARACTER;

@Service
@Slf4j
class CharacterFetcher extends DataFetcher<CharacterDto> {

    private static final String RESOURCE = "character";

    public CharacterFetcher(DSLContext jooq, RestTemplate restTemplate) {
        super(jooq, restTemplate, RESOURCE);
    }

    protected SelectJoinStep<Record1<Long>> selectIdColumnFromRamDataTable() {
        return jooq.select(CHARACTER.ID).from(CHARACTER);
    }

    protected CharacterDto prepareRamDto(Long i, String response) {
        return CharacterDto.builder()
                .id(Long.valueOf(getParamObjectFromResultJson(response, i, "id").toString()))
                .name((String) getParamObjectFromResultJson(response, i, "name"))
                .status((String) getParamObjectFromResultJson(response, i, "status"))
                .species((String) getParamObjectFromResultJson(response, i, "species"))
                .type((String) getParamObjectFromResultJson(response, i, "type"))
                .gender((String) getParamObjectFromResultJson(response, i, "gender"))
                .originId(getIdFromUrl((String) getParamObjectFromResultJson(response, i, "origin.url")))
                .locationId(getIdFromUrl((String) getParamObjectFromResultJson(response, i, "location.url")))
                .episodeIds(((List<String>) getParamObjectFromResultJson(response, i, "episode"))
                        .stream()
                        .map(this::getIdFromUrl)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(toList()))
                .imageBytes(restTemplate.getForObject((String) getParamObjectFromResultJson(response, i, "image"), byte[].class))
                .build();
    }

    protected void insertDtoIntoRamTable(List<CharacterDto> characters) {
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