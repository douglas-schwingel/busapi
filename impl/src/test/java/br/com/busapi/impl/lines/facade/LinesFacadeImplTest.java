package br.com.busapi.impl.lines.facade;

import br.com.busapi.impl.exception.ApiException;
import br.com.busapi.impl.lines.integration.LinesOperations;
import br.com.busapi.impl.lines.models.Line;
import br.com.busapi.impl.lines.service.LinesService;
import br.com.busapi.impl.lines.utils.LinesRandomizer;
import br.com.busapi.impl.lines.validation.LineValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LinesFacadeImplTest {

    private LinesService service;
    private LinesOperations operations;
    private LineValidation validation;
    private LinesFacadeImpl facade;
    private LinesRandomizer utils;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        service = mock(LinesService.class);
        operations = mock(LinesOperations.class);
        validation = mock(LineValidation.class);
        facade = new LinesFacadeImpl(service, operations, validation, new ObjectMapper(), new RestTemplate());
        utils = new LinesRandomizer();
    }

    @Test
    public void mustNotCallSaveAllWhenTesting() {
        when(operations.listBusLines(any(), any())).thenReturn(utils.getAllLines());

        ReflectionTestUtils.setField(facade, "isRealApplication", false);

        facade.saveAll();
        verify(service, times(0)).saveAll(any(), any(), any(),any(), any());
    }

    @Test
    public void mustCallSaveAllWhenNotTesting() {
        when(operations.listBusLines(any(), any())).thenReturn(utils.getAllLines());

        ReflectionTestUtils.setField(facade, "isRealApplication", true);

        facade.saveAll();
        verify(service, times(1)).saveAll(any(), any(), any(), any(), any());
    }

    @Test
    public void mustReturnAllBusesNear() {
        when(service.findNear(any(), any())).thenReturn(utils.getAllLines());

        List<Line> lines = facade.findNear(new Point(-30, -51), new Distance(0.005));

        assertEquals(utils.getAllLines().size(), lines.size());
    }

    @Test
    public void mustReturnTheLineWithTheGivenName() {
        String name = "VIAMAO";
        when(service.findByNameContains(name)).thenReturn(utils.getByNameContains(name));
        when(validation.nameIsValid(name)).thenReturn(true);

        List<Line> byName = facade.findByName(name);
        assertEquals(2, byName.size());
    }

    @Test
    public void mustThrowApiExceptionForInvalidName() {
        String name = "VIAMAO";
        when(service.findByNameContains(name)).thenReturn(utils.getByNameContains(name));
        when(validation.nameIsValid(name)).thenReturn(false);
        exception.expect(ApiException.class);
        exception.expectMessage("Invalid name: " + name);

        facade.findByName(name);
    }

    @Test
    public void mustReturnPageWith3Lines() {
        when(service.findAll(any())). thenReturn(new PageImpl<>(utils.getAllLines()));

        Page<Line> page = facade.findAll(PageRequest.of(0, 15));
        assertFalse(page.isEmpty());
        assertEquals(3, page.getTotalElements());
    }

    @Test
    public void mustReturnTheRighLineAfterSaving() {
        Line random = utils.getRandom();
        when(service.findById(random.getId())).thenReturn(null);
        when(service.saveOne(random)).thenReturn(random);
        when(validation.isValidToSave(random)).thenReturn(true);

        Line saved = facade.saveOne(random);
        assertEquals(random, saved);
    }

    @Test
    public void shouldThrowExceptionForSavingExistingLine() {
        Line random = utils.getRandom();
        when(service.findById(random.getId())).thenReturn(random);
        when(validation.isValidToSave(random)).thenReturn(true);
        exception.expect(ApiException.class);
        exception.expectMessage("Uptades should be done with PUT and not POST");
        facade.saveOne(random);
    }

    @Test
    public void shouldThrowExceptionForInvalidLineValuesOnSave() {
        Line random = utils.getRandom();
        when(validation.isValidToSave(random)).thenReturn(false);
        exception.expect(ApiException.class);
        exception.expectMessage("Invalid line values");
        facade.saveOne(random);
    }

    @Test
    public void mustReturnTheRightLineFindingById() {
        Line random = utils.getRandom();
        when(service.findById(random.getId())).thenReturn(random);

        Line found = facade.findById(random.getId());

        assertEquals(random, found);
    }

    @Test
    public void shouldThrowApiExceptionForLineNotFoundWithId() {
        Line random = utils.getRandom();
        when(service.findById(random.getId())).thenReturn(null);
        exception.expect(ApiException.class);
        exception.expectMessage("No registered line with id: " + random.getId());

        facade.findById(random.getId());
    }

    @Test
    public void shouldDoNothingAfterDeletingLine() {
        Line random = utils.getRandom();
        when(service.findById(random.getId())).thenReturn(random);
        when(service.deleteLine(random)).thenReturn(true);

        facade.deleteLine(random.getId());
        verify(service, times(1)).deleteLine(random);
    }

    @Test
    public void shouldThrowApiExceptionWhenTryingToDeleteNotExistingLine() {
        Line random = utils.getRandom();
        when(service.findById(random.getId())).thenReturn(random);
        when(service.deleteLine(random)).thenReturn(false);
        exception.expect(ApiException.class);
        exception.expectMessage("No line to be deleted with the id " + random.getId());

        facade.deleteLine(random.getId());
    }

    @Test
    public void mustReturnTheRighLineAfterUpdating() {
        Line random = utils.getRandom();
        when(service.findById(random.getId())).thenReturn(random);
        when(service.saveOne(random)).thenReturn(random);
        when(validation.isValidToSave(random)).thenReturn(true);
        when(validation.validateFieldsToUpdate(any(), any())).thenReturn(random);

        Line updated = facade.updateLine(random);
        assertEquals(random, updated);
    }

    @Test
    public void mustReturnTheRightLineFindingByCode() {
        Line random = utils.getRandom();
        when(service.findByCode(random.getCode())).thenReturn(random);
        when(validation.codeIsValid(random.getCode())).thenReturn(true);
        Line found = facade.findByCode(random.getCode());

        assertEquals(random, found);
    }

    @Test
    public void shouldThrowApiExceptionForLineNotFoundWithCode() {
        Line random = utils.getRandom();
        when(service.findByCode(random.getCode())).thenReturn(null);
        exception.expect(ApiException.class);
        exception.expectMessage("Invalid code " + random.getCode());

        facade.findByCode(random.getCode());
    }


}