package br.com.busapi.impl.lines.service;

import br.com.busapi.impl.lines.integration.LinesOperations;
import br.com.busapi.impl.lines.models.Line;
import br.com.busapi.impl.lines.repository.LinesRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

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
        return operations.populateLinesWithCoordinates(new RestTemplate(), repository, allLines);
    }
}
