package br.com.busapi.impl.lines.integration;

import br.com.busapi.impl.exception.ApiException;
import br.com.busapi.impl.lines.models.Line;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LinesOperationsTest {

    private LinesOperations operations;

    @Mock
    private RestTemplate template;

    @Rule
    public ExpectedException exception = ExpectedException.none();
    private Line lineTeste;

    @Before
    public void setUp() {
        operations = new LinesOperations();

        lineTeste = Line.builder()
                .id(5285)
                .code("T-Testado")
                .name("Teste testado").build();
    }


    @Test
    public void shouldReturn989ElementsFromDataPOA() {
        List<Line> lines = operations.listBusLines(new RestTemplate(), new ObjectMapper());

        assertEquals(989, lines.size());
    }

    @Test
    public void shouldReturnAnEmptyListAfterIOException() {
        doReturn("{\"id_false\":\"something\", \"does_not\":\"existis\"}")
                .when(template).getForObject(anyString(), any());
        List<Line> lines = operations.listBusLines(template, new ObjectMapper());

        assertTrue(lines.isEmpty());
    }

    @Test
    public void shouldPopulateTheLinesCoordinates() {
        assertTrue(lineTeste.getCoordinates().isEmpty());

        operations.populateLineWithCoordinates(new RestTemplate(), lineTeste);

        assertFalse(lineTeste.getCoordinates().isEmpty());
    }

    @Test
    public void shouldThrowApiExceptionWhenRestClientExceptionIsThrownMoreThen10Times() {
        exception.expect(ApiException.class);
        exception.expectMessage("Internal error during request for DataPOA");
        doThrow(RestClientException.class).when(template).getForObject(anyString(), any());

        operations.populateLineWithCoordinates(template, lineTeste);
    }

}