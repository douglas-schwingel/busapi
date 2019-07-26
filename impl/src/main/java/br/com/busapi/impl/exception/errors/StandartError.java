package br.com.busapi.impl.exception.errors;

import br.com.busapi.impl.exception.issues.Issue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.io.Serializable;
import java.util.List;

@Builder
@Data
@ApiModel
public class StandartError implements Serializable {

    @ApiModelProperty(example = "BAD_REQUEST")
    private String name;
    @ApiModelProperty(example = "Invalid request")
    private String message;
    @ApiModelProperty(example = "400")
    private Integer status;
    @Singular
    private List<Issue> issues;
    @ApiModelProperty(example = "Send us an email to get help")
    private String suggestedApplicationAction;
    @ApiModelProperty(example = "Verify the information and try again")
    private String suggestedUserAction;

}
