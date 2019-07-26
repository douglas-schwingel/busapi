package br.com.busapi.impl.lines.service;

import br.com.busapi.impl.lines.integration.LinesOperations;
import br.com.busapi.impl.lines.models.Line;
import br.com.busapi.impl.lines.repository.LinesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

@Slf4j
@Service
public class LinesService {

    private final LinesRepository repository;

    public LinesService(LinesRepository repository) {
        this.repository = repository;
    }

    public List<Line> listAll() {
        return new ArrayList<>(repository.findAll());
    }

    public List<Line> saveAll(List<Line> allLines, LinesOperations operations) {
        Semaphore semaphore = new Semaphore(5);
        allLines.forEach(l -> {
            try {
                semaphore.acquire();
                Thread.sleep(400);
                new Thread(() -> {
                    operations.populateLinesWithCoordinates(new RestTemplate(), l);
                    repository.save(l);
                }).start();
            } catch (InterruptedException e) {
                log.error("Error during save operation: {}", e.getMessage());
            }
            log.info("Saved line {}", l.getId());
            semaphore.release();
        });
        return allLines;
    }

    public List<Line> findNear(Point point, Distance dist) {
        return repository.findLinesByCoordinatesNear(point, dist);
    }
}
