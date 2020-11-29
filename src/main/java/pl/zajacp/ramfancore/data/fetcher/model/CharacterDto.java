package pl.zajacp.ramfancore.data.fetcher.model;

import lombok.*;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterDto {
    private Long id;
    private String name;
    private String status;
    private String species;
    private String type;
    private String gender;
    private Optional<Long> originId;
    private Optional<Long> locationId;
    private List<Long> episodeIds;
    private byte[] imageBytes;
}
