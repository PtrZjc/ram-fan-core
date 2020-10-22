package pl.zajacp.ramfancore.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CharacterDTO {

    private Integer id;
    private String name;
    private String status;
    private String species;
    private String type;
    private String gender;
    private LocationDTO origin;
    private LocationDTO locationDTO;
    private URL image;
    private List<String> episode;
    private URL url;
    private LocalDateTime created;

    private class Origin {
        private String name;
        private URL url;
    }
}
