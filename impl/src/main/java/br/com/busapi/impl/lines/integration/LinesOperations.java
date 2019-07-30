package br.com.busapi.impl.lines.integration;

import br.com.busapi.impl.exception.ApiException;
import br.com.busapi.impl.exception.errors.StandartErrorImpl;
import br.com.busapi.impl.exception.issues.Issue;
import br.com.busapi.impl.lines.models.Coordinate;
import br.com.busapi.impl.lines.models.Line;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class LinesOperations {

    private static final String URI = "http://www.poatransporte.com.br/php/facades/process.php";

    public List<Line> listBusLines(RestTemplate template, ObjectMapper mapper) {
        String linesString = template.getForObject(URI + "?a=nc&p=%&t=o", String.class);
        try {
            return Arrays.asList(mapper.readValue(linesString, Line[].class));
        } catch (IOException e) {
            log.trace("{0}", e);
        }
        return Collections.emptyList();
    }


    public void populateLineWithCoordinates(RestTemplate template, Line line){
        Map<Integer, Coordinate> lineCoordinates = listBusLineCoordinates(line.getId(), template);
        List<Double[]> coordinates = new ArrayList<>();
        lineCoordinates.forEach((i, c) -> coordinates.add(new Double[]{c.getLat(), c.getLng()}));
        line.setCoordinates(coordinates);
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
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                if (count == maxRetry) throw new ApiException(StandartErrorImpl.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("Internal error during request for DataPOA")
                        .name(HttpStatus.INTERNAL_SERVER_ERROR.name())
                        .issue(new Issue(e))
                        .suggestedApplicationAction("Contact us: not_existing@goodluck.com")
                        .suggestedUserAction("Contact the developers")
                        .build());
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
