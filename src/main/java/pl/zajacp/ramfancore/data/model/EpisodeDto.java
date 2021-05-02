package pl.zajacp.ramfancore.data.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeDto implements RamDto {
    private Long id;
    private String name;
    private String airDate;
    private String episode;
    private List<Long> characterIds;
}
