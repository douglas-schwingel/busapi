package br.com.busapi.impl.exception.issues;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@ApiModel
public class Issue implements Serializable {

    @ApiModelProperty(example = "org.springframework.http.HttpStatus")
    private String id;
    @ApiModelProperty(example = "Not Found")
    private String message;

    public Issue(Exception e) {
        this.id = e.toString();
        this.message = e.getMessage();
    }
}
