package br.com.busapi.impl.exception.handlers;

import br.com.busapi.impl.exception.ApiException;
import br.com.busapi.impl.exception.errors.ResponseError;
import br.com.busapi.impl.exception.errors.StandardError;
import br.com.busapi.impl.exception.issues.Issue;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class MyExceptionHandlerTest {

    private HttpServletRequest request;
    private MyExceptionHandler handler;

    @Before
    public void setUp() {
        request = mock(HttpServletRequest.class);
        handler = spy(new MyExceptionHandler());
        when(request.getRequestURI()).thenReturn("/test/teapot");
    }

    @Test
    public void mustReturnAResponseEntityWithTheRightInformations() {
       ApiException apiException = new ApiException(StandardError.builder()
                .name(HttpStatus.I_AM_A_TEAPOT.name())
                .status(HttpStatus.I_AM_A_TEAPOT.value())
               .message("I am a teapot")
               .issue(new Issue("TeaPot", "Testing teapot"))
               .suggestedApplicationAction("Just relax!")
               .suggestedUserAction("42")
                .build());
        ResponseEntity<ResponseError> responseEntity = handler.apiException(apiException, request);

        assertEquals(418, responseEntity.getStatusCodeValue());
    }

    @Test
    public void mustReturnAValidResponseEntityForMethodNotSupported() {
        var exception = new HttpRequestMethodNotSupportedException("This method is not supported here");

        ResponseEntity<ResponseError> responseEntity =
                handler.httpRequestMethodNotSupportedException(exception, request);

        assertEquals(405, responseEntity.getStatusCodeValue());
    }


    @Test
    public void mustReturnResponseEntityWithHttpStatus400() {
        var exception = mock(MethodArgumentTypeMismatchException.class);

        ResponseEntity<ResponseError> responseEntity = handler.methodArgumentException(exception, request);
        assertEquals(400, responseEntity.getStatusCodeValue());
    }

    @Test
    public void mustReturnResponseEntityWithStatus500ForGenericException() {
        RuntimeException exception = new RuntimeException("Just testing");

        ResponseEntity<ResponseError> responseEntity = handler.exception(exception, request);
        assertEquals(500, responseEntity.getStatusCodeValue());
    }

}