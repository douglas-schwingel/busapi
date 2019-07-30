package br.com.busapi.impl.lines.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(example = "5566")
    private Integer id;
    @ApiModelProperty(example = "344-2")
    @JsonProperty("codigo")
    private String code;
    @ApiModelProperty(example = "SANTA_MARIA")
    @JsonProperty("nome")
    private String name;
    @JsonProperty("coordenadas")
    @GeoSpatialIndexed
    private List<Double[]> coordinates;

}
