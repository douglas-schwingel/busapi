package br.com.busapi.contract.v1.lines.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class BusLinetinerary {

    @ApiModelProperty(example = "5566")
    private Integer id;
    @ApiModelProperty(example = "344-2")
    @JsonProperty("codigo")
    private String code;
    @ApiModelProperty(example = "SANTA_MARIA")
    @JsonProperty("nome")
    private String name;
    private List<Double[]> coordinates;
}
