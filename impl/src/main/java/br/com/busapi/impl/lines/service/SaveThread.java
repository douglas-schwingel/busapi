package br.com.busapi.impl.lines.service;

import br.com.busapi.impl.lines.integration.LinesOperations;
import br.com.busapi.impl.lines.models.Line;
import br.com.busapi.impl.lines.repository.LinesRepository;
import br.com.busapi.impl.lines.validation.LineValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Semaphore;

@Slf4j
public class SaveThread {


    public void execSaveInNewThread(LinesOperations operations, LinesRepository repository,
                                    LineValidation validation,
                                    Semaphore semaphore, Line l) {
        new Thread(() -> {
            try {
                doSave(operations, repository, validation, l);
                Thread.sleep(400);
            } catch (InterruptedException e) {
                log.error("Exception {}", e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
            semaphore.release();
        }, "Save " + l.getId()).start();
    }

    private void doSave(LinesOperations operations, LinesRepository repository, LineValidation validation, Line l) {
            operations.populateLineWithCoordinates(new RestTemplate(), l);
        l.setName(validation.formatName(l.getName()));
        repository.save(l);
        log.info("Saved line {}", l.getId());
    }
}
