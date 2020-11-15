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
    private Integer id;
    private String name;
    private String status;
    private String species;
    private String type;
    private String gender;
    private Optional<Integer> originId;
    private Optional<Integer> locationId;
    private List<Integer> episodeIds;
    private byte[] imageBytes;
}
