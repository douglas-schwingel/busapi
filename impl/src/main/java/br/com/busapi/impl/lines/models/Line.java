package br.com.busapi.impl.lines.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Line {

    private Integer id;
    @JsonProperty("codigo")
    private String code;
    @JsonProperty("nome")
    private String name;
    @GeoSpatialIndexed
    private List<Double[]> coordinates;

}
