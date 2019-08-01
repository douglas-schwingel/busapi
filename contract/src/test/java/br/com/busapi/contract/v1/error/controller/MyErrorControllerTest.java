package br.com.busapi.contract.v1.error.controller;

import br.com.busapi.impl.exception.ApiException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MyErrorControllerTest {

    @Mock
    private HttpServletRequest request;

    private MyErrorController controller = new MyErrorController();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void mustCreateValidApiExceptionForNotFound() {
        exception.expect(ApiException.class);
        exception.expectMessage("Not Found");

        when(request.getAttribute(anyString())).thenReturn(404);
        controller.error(request);
    }

    @Test
    public void mustCreateValidApiExceptionForNullStatus() {
        exception.expect(ApiException.class);
        exception.expectMessage("Internal Server Error");

        when(request.getAttribute(anyString())).thenReturn(null);
        controller.error(request);
    }

    @Test
    public void mustCreateValidApiExceptionForInvalidStatus() {
        exception.expect(ApiException.class);
        exception.expectMessage("Internal Server Error");

        when(request.getAttribute(anyString())).thenReturn(8000);
        controller.error(request);
    }

    @Test
    public void mustReturnTheRightString() {
        String errorPath = controller.getErrorPath();
        assertEquals("/error", errorPath);
    }

}