package br.com.busapi.impl.lines.service;

import br.com.busapi.impl.lines.integration.LinesOperations;
import br.com.busapi.impl.lines.models.Line;
import br.com.busapi.impl.lines.repository.LinesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
                    l.setName(formatName(l.getName()));
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
        return repository.findAllByCoordinatesNear(point, dist);
    }

    public List<Line> findByNameContains(String name) {
        return repository.findAllByNameContains(name.toUpperCase());
    }

    public Page<Line> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Line saveOne(Line line) {
        return repository.save(line);
    }

    public Line findById(Integer id) {
        return repository.findById(id);
    }

    public Line update(Line line) {
        Line saved = repository.findById(line.getId());
        if (line.getName() != null) saved.setName(line.getName());
        if (line.getCode() != null) saved.setCode(line.getCode());
        if(line.getCoordinates() != null) saved.getCoordinates().addAll(line.getCoordinates());
        return repository.save(saved);
    }

    private String formatName(String name) {
        return name
                .replace(" ", "_")
                .replace("/", "-")
                .replace("ª", "-a")
                .replace("º", "-o")
                .replace("Á", "A")
                .replace("\\", "")
                .replace("Ç", "C")
                .replace("Ã", "A")
                .replace("ã", "a")
                .replace("õ", "o")
                .replace("é", "e")
                .replace("Õ", "O")
                .replace("Ô", "O")
                .replace("Ó", "O")
                .replace("Í", "I")
                .replace("Ê", "E")
                .replace("Á", "A")
                .replace("É", "E").toUpperCase();
    }
}
