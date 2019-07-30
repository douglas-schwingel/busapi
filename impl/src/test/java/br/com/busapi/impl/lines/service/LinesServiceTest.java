package br.com.busapi.impl.lines.service;

import br.com.busapi.impl.exception.ApiException;
import br.com.busapi.impl.lines.models.Line;
import br.com.busapi.impl.lines.repository.LinesRepository;
import br.com.busapi.impl.lines.test.utils.LinesTestsUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LinesServiceTest {

    private LinesRepository repository;

    private LinesService service;

    private LinesTestsUtils utils = new LinesTestsUtils();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        repository = mock(LinesRepository.class);
        service = new LinesService(repository);
    }

    @Test
    public void shouldReturnAllLinesWhichNameContainsGivenString() {
        when(repository.findAllByNameContains("VIAMAO")).thenReturn(utils.getByNameContains("VIAMAO"));

        List<Line> viamao = service.findByNameContains("viamao");

        assertEquals(2, viamao.size(), 0.001);
    }

    @Test
    public void mustReturnAValidPageForUnpaged() {
        when(repository.findAll(Pageable.unpaged())).thenReturn(new PageImpl<>(utils.getAllLines()));

        Page<Line> all = service.findAll(Pageable.unpaged());

        assertEquals(3, all.getTotalElements());
    }

    @Test
    public void mustThrowApiExceptionForRequestingPageAboveMaxPages() {
        Pageable pageable = PageRequest.of(90, 15);
        Page<Line> pageImpl = mock(Page.class);
        when(repository.findAll(pageable)).thenReturn(pageImpl);
        when(pageImpl.getTotalPages()).thenReturn(1);
        when(pageImpl.getNumber()).thenReturn(90);


        exception.expect(ApiException.class);
        exception.expectMessage("Requested page(90) is above max pages (0)");

        service.findAll(pageable);
    }

    @Test
    public void mustReturnTheSavedLine() {
        Line random = utils.getRandom();
        when(repository.save(random)).thenReturn(random);

        Line saved = service.saveOne(random);

        assertEquals(random, saved);
    }

    @Test
    public void mustReturnLineWithRequestedId() {
        int id = 1095;
        Line byId = utils.getById(id);
        when(repository.findById(id)).thenReturn(byId);

        Line line = service.findById(id);

        assertEquals(byId, line);
    }

    @Test
    public void mustReturnTrueForRemovingValidLine() {
        Line random = utils.getRandom();

        boolean isSuccessful = service.deleteLine(random);

        assertTrue(isSuccessful);
    }

    @Test
    public void mustThrowTheExceptionForDeleteInvalidLine() {
        Line random = utils.getRandom();
        doThrow(new IllegalArgumentException()).when(repository).delete(random);

        boolean isSuccessful = service.deleteLine(random);

        assertFalse(isSuccessful);
    }

    @Test
    public void mustReturnTheRightLineForRecievedCode() {
        String code = "109-5";
        Line byCode = utils.getByCode(code);
        when(repository.findByCode(code)).thenReturn(byCode);

        Line line = service.findByCode(code);

        assertEquals(byCode, line);
    }
}