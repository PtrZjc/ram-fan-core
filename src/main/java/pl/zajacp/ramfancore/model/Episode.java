package pl.zajacp.ramfancore.model;

import java.time.LocalDate;
import java.util.List;

public class Episode {

    private Integer id;
    private String name;    
    private LocalDate showDate;
    private String number;
    private String url;    
    private List<String> charactersUrl;
    private List<Character> characters;
}
