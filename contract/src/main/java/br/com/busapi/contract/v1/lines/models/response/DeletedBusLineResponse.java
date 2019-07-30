package br.com.busapi.contract.v1.lines.models.response;

import br.com.busapi.impl.lines.models.Line;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeletedBusLineResponse {

    @ApiModelProperty(example = "200")
    private int status;
    @ApiModelProperty(example = "Line with id 5566 deleted with success.")
    private String message;
    private Line deletedLine;
}
