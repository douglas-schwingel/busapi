package br.com.busapi.impl.lines.integration;

import br.com.busapi.impl.lines.models.Coordinate;
import br.com.busapi.impl.lines.models.Line;
import br.com.busapi.impl.lines.repository.LinesRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Semaphore;

@Service
@Slf4j
public class LinesOperations {

    public List<Line> listBusLines(RestTemplate template) {
        log.info("Entered list all buses");
        String linesString = template
                .getForObject("http://www.poatransporte.com.br/php/facades/process.php?a=nc&p=%&t=o", String.class);
        try {
            Line[] linesArray = new ObjectMapper().readValue(linesString, Line[].class);
            return Arrays.asList(linesArray);
        } catch (IOException e) {
            log.error("Exception {}", e.getMessage());
            log.trace("{0}", e);
        }
        return Collections.emptyList();
    }


    public List<Line> populateLinesWithCoordinates(RestTemplate template, LinesRepository repository, List<Line> lines) {
        Semaphore semaphore = new Semaphore(5);
        lines.forEach(l -> {
            try {
                semaphore.acquire();
                populate(template, repository, l);
                semaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        return lines;
    }

    private void populate(RestTemplate template, LinesRepository repository, Line l) throws InterruptedException {
        Thread.sleep(400);
        new Thread(() -> {
            Map<Integer, Coordinate> lineCoordinates = listBusLineCoordinates(l.getId(), template);
            l.setItinerary(lineCoordinates);
            log.info(l.toString());
            repository.save(l);
        }, "PopulatingCoordinatesFor - " + l.getId()).start();
    }

    private Map<Integer, Coordinate> listBusLineCoordinates(Integer id, RestTemplate template) {
        int count = 0;
        int maxRetry = 9;
        while (true) {
            String lineString = null;
            try {
                lineString = template
                        .getForObject("http://www.poatransporte.com.br/php/facades/process.php?a=il&p=" + id, String.class);
                String s = parseToString(lineString);
                return new ObjectMapper().readValue(s, new TypeReference<Map<Integer, Coordinate>>() {
                });
            } catch (RestClientException e) {
                log.error(e.getMessage());
                count++;
                if (count == maxRetry) throw new RuntimeException(id.toString());
            } catch (IOException e) {
                log.error("Error trying to read value from {}", id);
            }
        }
    }

    private String parseToString(String lineString) {
        String[] split = lineString.split(",");
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (int i = 3; i <= split.length - 1; i++) {
            builder.append(split[i]);
            builder.append(",");
        }
        builder.deleteCharAt(builder.lastIndexOf(","));
        return builder.toString();
    }

}
