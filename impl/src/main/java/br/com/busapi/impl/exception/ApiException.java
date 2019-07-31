package br.com.busapi.impl.exception;

import br.com.busapi.impl.exception.errors.StandardError;
import lombok.Getter;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.List;

@ApiIgnore
public class ApiException extends RuntimeException {

    @Getter
    private final List<StandardError> errors;

    public ApiException(StandardError... errors) {
        super(errors[0].getMessage());
        this.errors = Arrays.asList(errors);
    }


}
