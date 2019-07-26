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

    private static final String URI = "http://www.poatransporte.com.br/php/facades/process.php";

    public List<Line> listBusLines(RestTemplate template) {
        String linesString = template.getForObject(URI + "?a=nc&p=%&t=o", String.class);
        try {
            return Arrays.asList(new ObjectMapper().readValue(linesString, Line[].class));
        } catch (IOException e) {
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
            List<Double[]> coordinates = new ArrayList<>();
            lineCoordinates.forEach((i, c) -> coordinates.add(new Double[]{c.getLat(), c.getLng()}));
            l.setCoordinates(coordinates);
            log.info(l.toString());
            repository.save(l);
        }, "PopulatingCoordinatesFor - " + l.getId()).start();
    }

    private Map<Integer, Coordinate> listBusLineCoordinates(Integer id, RestTemplate template) {
        int count = 0;
        int maxRetry = 9;
        while (true) {
            try {
                String parsedString = parseToStringWithoutIdAndCode(Objects.requireNonNull(
                        template.getForObject(URI + "?a=il&p=" + id, String.class)));
                return new ObjectMapper()
                        .readValue(parsedString, new TypeReference<Map<Integer, Coordinate>>() {
                });
            } catch (RestClientException e) {
                count++;
                if (count == maxRetry) throw new RuntimeException(id.toString());
            } catch (IOException e) {
                log.error("Error trying to read value from {}", id);
            }
        }
    }

    private String parseToStringWithoutIdAndCode(String lineString) {
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
