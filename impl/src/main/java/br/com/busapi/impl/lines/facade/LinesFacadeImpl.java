package br.com.busapi.impl.lines.facade;

import br.com.busapi.impl.lines.integration.LinesOperations;
import br.com.busapi.impl.lines.models.Line;
import br.com.busapi.impl.lines.service.LinesService;
import lombok.extern.slf4j.Slf4j;
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

    public List<Line> listAllBusLines() {
        List<Line> allLines = operations.listBusLines(new RestTemplate());
        return service.saveAll(allLines, operations);
    }
}
