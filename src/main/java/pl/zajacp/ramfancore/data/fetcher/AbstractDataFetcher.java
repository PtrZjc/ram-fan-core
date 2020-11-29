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
import org.jooq.Record1;
import org.jooq.SelectJoinStep;
import org.springframework.web.client.RestTemplate;
import pl.zajacp.ramfancore.data.fetcher.model.RamDto;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Slf4j
@AllArgsConstructor
public abstract class AbstractDataFetcher<Dto extends RamDto> {

    protected final DSLContext jooq;
    protected final RestTemplate restTemplate;

    protected final String resource;
    protected final int PAGE_SIZE = 20;

    protected abstract SelectJoinStep<Record1<Long>> selectIdColumnFromRamDataTable();

    protected abstract Dto prepareRamDto(Long i, String response);

    protected abstract void insertDtoIntoRamTable(List<Dto> characters);

    void fetchDataAndSaveInDb() {
        Set<Long> existingIds = getResourceUniqueIDs();
        Long objectCount = getTotalResourceObjectCount();
        Map<Long, List<Long>> missingRecordsByPageNumber = getMissingRecordsByPageNumber(existingIds, objectCount);

        missingRecordsByPageNumber.forEach(this::fetchPageAndSaveInDb);
    }

    protected Set<Long> getResourceUniqueIDs() {
        return selectIdColumnFromRamDataTable()
                .fetch().stream()
                .map(record -> (Long) record.get(0))
                .collect(toSet());
    }

    protected Long getTotalResourceObjectCount() {
        return Long.valueOf(
                JsonPath.parse(restTemplate.getForObject("/" + resource, String.class)).read("$.info.count").toString());
    }

    protected Map<Long, List<Long>> getMissingRecordsByPageNumber(Set<Long> existingIds, Long totalObjectCount) {
        return LongStream.rangeClosed(1, totalObjectCount).boxed()
                .filter(id -> !existingIds.contains(id))
                .collect(groupingBy(id -> (id - 1) / PAGE_SIZE + 1, mapping(id -> id % PAGE_SIZE - 1, toList())));
    }

    private void fetchPageAndSaveInDb(Long pageNumber, List<Long> recordIndex) {
        String responsePage = restTemplate.getForObject("/" + resource + "/?page=" +
                pageNumber.toString(), String.class);

        List<Dto> ramDtos = new ArrayList<>();
        recordIndex.forEach(i -> ramDtos.add(prepareRamDto(i, responsePage)));
        insertDtoIntoRamTable(ramDtos);
    }

    protected Object getParamObjectFromResultJson(String json, Long index, String paramName) {
        return JsonPath.parse(json).read(new StringBuilder()
                .append("$.results.[")
                .append(index.toString())
                .append("].")
                .append(paramName)
                .toString());
    }

    protected Optional<Long> getIdFromUrl(String url) {
        if (!"".equals(url)) {
            String[] segments = url.split("/");
            return Optional.of(Long.valueOf(segments[segments.length - 1]));
        }
        return Optional.empty();
    }
}
