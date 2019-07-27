package br.com.busapi.impl.lines.facade;

import br.com.busapi.impl.lines.integration.LinesOperations;
import br.com.busapi.impl.lines.models.Line;
import br.com.busapi.impl.lines.service.LinesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class LinesFacadeImpl {

    private final LinesService service;
    private final LinesOperations operations;

    public LinesFacadeImpl(LinesService service, LinesOperations operations) {
        this.service = service;
        this.operations = operations;
    }

    public List<Line> saveAll() {
        List<Line> allLines = operations.listBusLines(new RestTemplate(), new ObjectMapper());
        return service.saveAll(allLines, operations);
    }

    public List<Line> findNear(Point point, Distance dist) {
        return service.findNear(point, dist);
    }

    public Line findByName(String name) {
        return service.findByName(name);
    }
}
