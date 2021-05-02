package pl.zajacp.ramfancore.data.fetcher;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.zajacp.ramfancore.data.model.EpisodeDto;

import static java.util.stream.Collectors.toList;
import static pl.zajacp.ramfancore.model.tables.Episode.EPISODE;

@Service
@Slf4j
class EpisodeFetcher extends DataFetcher<EpisodeDto> {

    private static final String RESOURCE = "episode";

    public EpisodeFetcher(DSLContext jooq, RestTemplate restTemplate) {
        super(jooq, restTemplate, RESOURCE);
    }

    protected SelectJoinStep<Record1<Long>> selectIdColumnFromRamDataTable() {
        return jooq.select(EPISODE.ID).from(EPISODE);
    }

    @SuppressWarnings("unchecked")
    protected EpisodeDto prepareRamDto(Long i, String response) {
        return EpisodeDto.builder()
                .id(Long.valueOf(getParamObjectFromResultJson(response, i, "id").toString()))
                .name((String) getParamObjectFromResultJson(response, i, "name"))
                .airDate((String) getParamObjectFromResultJson(response, i, "air_date"))
                .episode((String) getParamObjectFromResultJson(response, i, "episode"))
                .characterIds(((List<String>) getParamObjectFromResultJson(response, i, "characters"))
                        .stream()
                        .map(this::getIdFromUrl)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(toList()))
                .build();
    }

    protected void insertDtoIntoRamTable(List<EpisodeDto> episodes) {
        var multipleInsert = jooq.insertInto(EPISODE, EPISODE.ID, EPISODE.NAME, EPISODE.AIR_DATE, EPISODE.EPISODE_);
        for (EpisodeDto episode : episodes) {
            multipleInsert = multipleInsert.values(
                    episode.getId(),
                    episode.getName(),
                    episode.getAirDate(),
                    episode.getEpisode());
        }
        multipleInsert.execute();
    }
}