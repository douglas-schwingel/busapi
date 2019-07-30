package br.com.busapi.impl.lines.service;

import br.com.busapi.impl.exception.ApiException;
import br.com.busapi.impl.exception.errors.StandartErrorImpl;
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
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.Semaphore;

@Slf4j
@Service
public class LinesService {

    private final LinesRepository repository;

    public LinesService(LinesRepository repository) {
        this.repository = repository;
    }

    public List<Line> saveAll(List<Line> allLines, LinesOperations operations, LineValidation validation) {
        Semaphore semaphore = new Semaphore(5);
        allLines.forEach(l -> {
            try {
                semaphore.acquire();
                Thread.sleep(400);
                new Thread(() -> {
                    operations.populateLineWithCoordinates(new RestTemplate(), l);
                    l.setName(validation.formatName(l.getName()));
                    repository.save(l);
                }).start();
            } catch (InterruptedException e) {
                log.error("Error during save operation: {}", e.getMessage(), e);
            }
            log.info("Saved line {}", l.getId());
            semaphore.release();
        });
        return allLines;
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
            throw new ApiException(StandartErrorImpl.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .name(HttpStatus.BAD_REQUEST.name())
                    .message(String.format("Requested page(%d) is above max pages (%d)",
                            pageable.getPageNumber(), page.getTotalPages()))
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
}
