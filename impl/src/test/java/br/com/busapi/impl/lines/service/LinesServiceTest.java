package br.com.busapi.impl.lines.service;

import br.com.busapi.impl.lines.repository.LinesRepository;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LinesServiceTest {

    @Mock
    private LinesRepository repository;

    private LinesService service;

    @Before
    public void setUp() {
        service = new LinesService(repository);
    }




}