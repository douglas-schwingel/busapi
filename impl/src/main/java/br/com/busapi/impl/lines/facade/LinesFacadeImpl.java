package br.com.busapi.impl.lines.facade;

import br.com.busapi.impl.lines.models.Line;
import br.com.busapi.impl.lines.service.LinesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class LinesFacadeImpl {

    private final LinesService service;

    public LinesFacadeImpl(LinesService service) {
        this.service = service;
    }

    public List<Line> listAllBusLines() {
        log.info("Entered list all buses");
        String linesString = new RestTemplate()
                .getForObject("http://www.poatransporte.com.br/php/facades/process.php?a=nc&p=%&t=o", String.class);
        try {
            Line[] lines = new ObjectMapper().readValue(linesString, Line[].class);
            List<Line> lines1 = Arrays.asList(lines);

            return lines1;
        } catch (IOException e) {
            log.error("Exception {}", e.getMessage());
            log.trace("{0}", e);
        }
        return Collections.emptyList();
    }
}
