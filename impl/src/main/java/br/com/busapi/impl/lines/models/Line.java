package br.com.busapi.impl.lines.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class Line {

    private Integer id;
    @JsonProperty("codigo")
    private String code;
    @JsonProperty("nome")
    private String name;
    @Singular
    @GeoSpatialIndexed
    private List<Double[]> coordinates;

}
