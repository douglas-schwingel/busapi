package br.com.busapi.impl.exception;

import br.com.busapi.impl.exception.errors.StandartError;
import lombok.Getter;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.List;

@ApiIgnore
public class ApiException extends RuntimeException {

    @Getter
    private final List<StandartError> errors;

    public ApiException(StandartError... errors) {
        super(errors[0].getMessage());
        this.errors = Arrays.asList(errors);
    }


}
