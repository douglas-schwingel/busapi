package br.com.busapi.impl.exception.errors;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
@Generated
public class ResponseError implements Serializable {

    @ApiModelProperty(example = "/v2/not_found")
    private String namespace;
    @ApiModelProperty(example = "en-US")
    private String language;
    @Singular
    private List<StandardError> errors;



}