package br.com.busapi.impl.exception.handlers;

import br.com.busapi.impl.exception.ApiException;
import br.com.busapi.impl.exception.errors.ResponseError;
import br.com.busapi.impl.exception.errors.StandartError;
import br.com.busapi.impl.exception.issues.Issue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class MyExceptionHandler {

    private static final String PT_BR = "pt-BR";
    private static final String LOG_MESSAGE = "Exception: {}";

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ResponseError> apiException(ApiException exception, HttpServletRequest request) {
        log.trace(LOG_MESSAGE,exception.getMessage(), exception);
        return ResponseEntity.status(exception.getErrors().get(0).getStatus())
                .body(new ResponseError(request.getRequestURI(), PT_BR, exception.getErrors()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseError> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception,
                                                                                HttpServletRequest request) {
        StandartError error = StandartError.builder()
                .name(HttpStatus.METHOD_NOT_ALLOWED.name())
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .message("Method not allowed")
                .issue(new Issue(exception))
                .suggestedUserAction("Contact the developer")
                .suggestedApplicationAction("This method is not supported here. Contact us")
                .build();
        log.trace(LOG_MESSAGE,exception.getMessage(), exception);
        return new ResponseEntity<>(ResponseError.builder()
                .namespace(request.getRequestURI())
                .language(PT_BR)
                .error(error).build()
                , HttpStatus.METHOD_NOT_ALLOWED);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseError> exception(Exception exception, HttpServletRequest request) {
        ApiException apiException = new ApiException(StandartError.builder()
                .message("Unexcpected error")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .name(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .issue(new Issue(exception))
                .suggestedUserAction("Just Run!!")
                .suggestedApplicationAction("Don't do anything.. It's not your fault")
                .build()
        );
        log.trace(LOG_MESSAGE,exception.getMessage(), exception);
        return ResponseEntity.status(apiException.getErrors().get(0).getStatus())
                .body(new ResponseError(request.getRequestURI(), PT_BR, apiException.getErrors()));
    }

}
