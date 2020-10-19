package pl.zajacp.ramfancore.model;

import com.rickandmortyapi.Episode;
import lombok.Getter;

import java.net.URL;
import java.util.List;

@Getter
public class Character {

    private Integer id;
    private String name;
    private Status status;
    private String species;
    private String type;
    private Gender gender;
    private Location originLocation;
    private Location lastKnownLocation;
    private URL image;
    private List<String> episodesUrl;
    private List<Episode> episodes;

    public enum Gender {
        UNKNOWN,
        FEMALE,
        MALE,
        GENDERLESS
    }

    public enum Status {
        UNKNOWN,
        ALIVE,
        DEAD
    }
}
