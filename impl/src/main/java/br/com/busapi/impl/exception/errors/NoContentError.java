package br.com.busapi.impl.exception.errors;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoContentError implements StandartError {

    @ApiModelProperty(example = "BAD_REQUEST")
    private String name;
    @ApiModelProperty(example = "Invalid request")
    private String message;
    @ApiModelProperty(example = "400")
    private Integer status;


}
