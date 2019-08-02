package br.com.busapi.impl.lines.service;

import br.com.busapi.impl.exception.ApiException;
import br.com.busapi.impl.exception.errors.StandardError;
import br.com.busapi.impl.exception.issues.Issue;
import br.com.busapi.impl.lines.integration.LinesOperations;
import br.com.busapi.impl.lines.models.Line;
import br.com.busapi.impl.lines.repository.LinesRepository;
import br.com.busapi.impl.lines.validation.LineValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Semaphore;

@Slf4j
@Service
public class LinesService {

    private final LinesRepository repository;

    public LinesService(LinesRepository repository) {
        this.repository = repository;
    }

    public void saveAll(List<Line> allLines, LinesOperations operations,
                              LineValidation validation, SaveThread thread, Semaphore semaphore) {
        allLines.forEach(l -> {
            if(repository.findById(l.getId()) == null) {
                try {
                    semaphore.acquire();
                    thread.execSaveInNewThread(operations, repository, validation, semaphore, l);
                } catch (InterruptedException e) {
                    log.error("Error during save operation: {}", e.getMessage(), e);
                    Thread.currentThread().interrupt();
                }
            } else {
                log.info(String.format("Line with id %d already exists", l.getId()));
            }
        });
    }

    public List<Line> findNear(Point point, Distance dist) {
        return repository.findAllByCoordinatesNear(point, dist);
    }

    public List<Line> findByNameContains(String name) {
        return repository.findAllByNameContains(name.toUpperCase());
    }

    public Page<Line> findAll(Pageable pageable) {
        Page<Line> page = repository.findAll(pageable);
        if (page.getTotalPages() < page.getNumber() && pageable.isPaged()) {
            throw new ApiException(StandardError.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .name(HttpStatus.BAD_REQUEST.name())
                    .message(String.format("Requested page(%d) is above max pages (%d)",
                            pageable.getPageNumber(), page.getTotalPages() - 1))
                    .issue(new Issue(new IndexOutOfBoundsException("Request page above total pages")))
                    .suggestedApplicationAction("Create verification before sending the request")
                    .suggestedUserAction("Verify the page number and try again")
                    .build());
        }
        return page;
    }

    public Line saveOne(Line line) {
        return repository.save(line);
    }

    public Line findById(Integer id) {
        return repository.findById(id);
    }

    public boolean deleteLine(Line line) {
        try {
            repository.delete(line);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public Line findByCode(String code) {
        return repository.findByCode(code).orElseThrow(() -> new ApiException(StandardError.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .name(HttpStatus.NOT_FOUND.name())
                .message(String.format("No bus line with the code %s", code))
                .issue(new Issue(new IllegalArgumentException("No bus line found for code " + code)))
                .suggestedApplicationAction("Create verification before sending the request")
                .suggestedUserAction("Verify the page number and try again")
                .build()));
    }


}
