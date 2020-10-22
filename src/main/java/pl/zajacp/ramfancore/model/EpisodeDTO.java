package pl.zajacp.ramfancore.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeDTO {

    private Integer id;
    private String name;
    private LocalDate airDate;
    private String episode;
    private List<String> characters;
    private URL url;
    private LocalDateTime created;
}
