package br.com.busapi.contract.v1.lines.controller;

import br.com.busapi.contract.v1.lines.facade.LinesControllerFacade;
import br.com.busapi.contract.v1.lines.mapper.LinesMapper;
import br.com.busapi.contract.v1.lines.models.request.LineRequest;
import br.com.busapi.contract.v1.lines.models.response.BusLineResponse;
import br.com.busapi.contract.v1.lines.models.response.BusLinetinerary;
import br.com.busapi.contract.v1.lines.models.response.ListBusLineResponse;
import br.com.busapi.impl.lines.facade.LinesFacadeImpl;
import br.com.busapi.impl.lines.models.Line;
import br.com.busapi.impl.lines.utils.LinesRandomizer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LinesControllerTest {

    private LinesRandomizer utils = new LinesRandomizer();
    private LinesFacadeImpl facadeImpl;
    private LinesControllerFacade controllerFacade;
    private LinesController controller;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        facadeImpl = mock(LinesFacadeImpl.class);
        controllerFacade = spy(new LinesControllerFacade(new LinesMapper(), facadeImpl));
        controller = new LinesController(controllerFacade);

    }

    @Test
    public void mustReturnOnePageWith3Lines() {
        when(facadeImpl.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(utils.getAllLines()));

        Page<BusLineResponse> page = controller.findAll(0, 15);
        verify(controllerFacade, times(1)).findAll(any(Pageable.class));

        assertEquals(3, page.getTotalElements());
    }

    @Test
    public void mustReturnPageForUnpagedWhenAllPageParametersAreNull() {
        when(facadeImpl.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(utils.getAllLines()));

        Page<BusLineResponse> page = controller.findAll(null, null);
        verify(controllerFacade, times(1)).findAll(any(Pageable.class));

        assertFalse(page.getPageable().isPaged());
        assertEquals(3, page.getTotalElements());
    }

    @Test
    public void mustReturnPageForUnpagedWhenOneOfPageParametersAreNull() {
        when(facadeImpl.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(utils.getAllLines()));

        Page<BusLineResponse> page = controller.findAll(0, null);
        verify(controllerFacade, times(1)).findAll(any(Pageable.class));

        assertFalse(page.getPageable().isPaged());
        assertEquals(3, page.getTotalElements());
    }

    @Test
    public void shouldReturnAValidBusLineResponseForRandomBus() {
        Line random = utils.getRandom();
        when(facadeImpl.findById(random.getId())).thenReturn(random);

        BusLinetinerary response = controller.findById(random.getId());
        verify(controllerFacade, times(1)).findById(random.getId());

        assertEquals(random.getId(), response.getId());
        assertEquals(random.getCode(), response.getCode());
        assertEquals(random.getName(), response.getName());
    }

    @Test
    public void mustCallTheMethodDeleteLineCorrectly() {
        controller.deleteBusLine(1093);
        verify(controllerFacade, times(1)).deleteLine(1093);
        verify(facadeImpl, times(1)).deleteLine(1093);
    }

    @Test
    public void mustReturnLinesWhenCallingFindNear() {
        when(facadeImpl.findNear(any(Point.class), any(Distance.class))).thenReturn(utils.getAllLines());

        ListBusLineResponse near = controller.findNear(0.005, -30.1, -51.1);
        verify(controllerFacade, times(1)).findNear(any(Point.class), any(Distance.class));

        assertEquals(3, near.getLines().size());
    }

    @Test
    public void msutReturn2LinesForTryingFindByViamao() {
        String name = "VIAMAO";
        when(facadeImpl.findByName(name)).thenReturn(utils.getByNameContains(name));
        ListBusLineResponse viamao = controller.findBusByName(name);
        verify(controllerFacade, times(1)).findByName(name);

        assertEquals(2, viamao.getLines().size());
        viamao.getLines().forEach(l -> assertTrue(l.getName().contains(name)));
    }

    @Test
    public void mustReturnOneLineWhenTryingToFindByCode() {
        Line random = utils.getRandom();
        when(facadeImpl.findByCode(random.getCode())).thenReturn(random);

        BusLinetinerary response = controller.findByCode(random.getCode());
        verify(controllerFacade, times(1)).findByCode(random.getCode());

        assertEquals(random.getCode(), response.getCode());
        assertEquals(random.getCoordinates().size(), response.getCoordinates().size());
    }

    @Test
    public void mustReturnTheSameLineRecievedAfterSaving() {
        Line random = utils.getRandom();
        LineRequest randomRequest = getRandomLineRequest(random);

        when(facadeImpl.saveOne(random)).thenReturn(random);

        BusLineResponse response = controller.saveBusLine(randomRequest);
        verify(controllerFacade, times(1)).saveOne(randomRequest);

        assertEquals(randomRequest.getNome(), response.getName());
        assertEquals(randomRequest.getCodigo(), response.getCode());
        assertEquals(random.getId(), response.getId());
    }

    @Test
    public void mustReturnTheSameLineRecievedAfterUpdating() {
        Line random = utils.getRandom();
        LineRequest randomRequest = getRandomLineRequest(random);

        when(facadeImpl.updateLine(random)).thenReturn(random);

        BusLineResponse response = controller.updateBusLine(randomRequest);
        verify(controllerFacade, times(1)).updateBusLine(randomRequest);

        assertEquals(randomRequest.getNome(), response.getName());
        assertEquals(randomRequest.getCodigo(), response.getCode());
        assertEquals(random.getId(), response.getId());
    }

    @Test
    public void mustReturnListBusLineResponseForSavingAll() {
        when(facadeImpl.saveAll()).thenReturn(utils.getAllLines());

        ListBusLineResponse listBusLineResponse = controller.saveAll();
        verify(controllerFacade, times(1)).saveAll();

        assertEquals(3, listBusLineResponse.getLines().size());
    }

    private LineRequest getRandomLineRequest(Line random) {
        return LineRequest.builder()
                .id(random.getId())
                .nome(random.getName())
                .codigo(random.getCode())
                .coordenadas(random.getCoordinates())
                .build();
    }


}