package br.com.busapi.impl.lines.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Line {

    private Integer id;
    @JsonProperty("codigo")
    private String code;
    @JsonProperty("nome")
    private String name;
    private Map<Integer, Coordinate> itinerary;

}
