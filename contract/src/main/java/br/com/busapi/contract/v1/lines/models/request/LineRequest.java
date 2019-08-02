package br.com.busapi.contract.v1.lines.models.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class LineRequest {

    @ApiModelProperty(example = "5566")
    private Integer id;

    @ApiModelProperty(example = "344-2")
    private String codigo;

    @ApiModelProperty(example = "SANTA_MARIA")
    private String nome;

    @Singular
    private List<Double[]> coordenadas;
}
