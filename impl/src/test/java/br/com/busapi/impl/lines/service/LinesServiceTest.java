package br.com.busapi.impl.lines.service;

import br.com.busapi.impl.lines.repository.LinesRepository;
import org.junit.Before;

import static org.mockito.Mockito.mock;

public class LinesServiceTest {

    private LinesRepository repository;

    private LinesService service;

    @Before
    public void setUp() {
        repository = mock(LinesRepository.class);
        service = new LinesService(repository);
    }




}