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
public class LocationDTO {

    private Integer id;
    private String name;
    private String type;
    private String dimension;
    private List<String> residents;
    private URL url;
    private LocalDateTime created;
}
