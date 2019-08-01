package br.com.busapi.impl.lines.service;

import br.com.busapi.impl.lines.integration.LinesOperations;
import br.com.busapi.impl.lines.repository.LinesRepository;
import br.com.busapi.impl.lines.utils.LinesRandomizer;
import br.com.busapi.impl.lines.validation.LineValidation;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Semaphore;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SaveThreadTest {

    private SaveThread saveThread;
    private LinesRandomizer utils;

    @Before
    public void setUp() {
        saveThread = new SaveThread();
        utils = new LinesRandomizer();
    }

    @Test
    public void mustNotReleaseThreads() {
        LinesOperations operations = mock(LinesOperations.class);
        LineValidation validation = mock(LineValidation.class);
        LinesRepository repository = mock(LinesRepository.class);
        Semaphore semaphore = spy(new Semaphore(2));

        doAnswer(invocation -> { throw new InterruptedException(); }).when(operations).populateLineWithCoordinates(any(), any());

        saveThread.execSaveInNewThread(operations, repository, validation, semaphore, utils.getById(1093));

        verify(semaphore, times(0)).release();
    }

}