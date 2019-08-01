package br.com.busapi.impl.lines.utils;

import br.com.busapi.impl.lines.models.Line;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utilitary class. Randomly generates coordinates for BusLines.
 * @implNote Test usage only
 *
 *
 */
public class LinesRandomizer {

    private List<Line> lines = new ArrayList<>();

    public LinesRandomizer() {
        lines.add(Line.builder()
                .id(1093)
                .name("VIAMAO")
                .code("109-3")
                .coordinates(randomNumberOfCoordinates())
                .build());

        lines.add(Line.builder()
                .id(1095)
                .name("TAQUARA")
                .code("109-5")
                .coordinates(randomNumberOfCoordinates())
                .build());

        lines.add(Line.builder()
                .id(1099)
                .name("VIAMAO-KRAHE")
                .code("109-9")
                .coordinates(randomNumberOfCoordinates())
                .build());
    }

    private List<Double[]> randomNumberOfCoordinates() {
        int max = new SecureRandom().nextInt(10);
        List<Double[]> list = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            list.add(rngCoordinate());
        }
        return list;
    }

    private Double[] rngCoordinate() {
        return new Double[]{rngLat(), rngLgn()};
    }

    private Double rngLat() {
        Double min = -30.261371;
        Double max = -29.954468;
        return min + (max - min) * new SecureRandom().nextDouble();
    }

    private Double rngLgn() {
        Double min = -51.266828;
        Double max = -51.087028;
        return min + (max - min) * new SecureRandom().nextDouble();
    }

    public List<Line> getByNameContains(String pattern) {
        return lines.stream().filter(line -> line.getName().contains(pattern)).collect(Collectors.toList());
    }

    public List<Line> getAllLines() {
        return lines;
    }

    public Line getRandom() {
        return lines.get(new SecureRandom().nextInt(lines.size()));
    }

    public Line getById(Integer recievedId) {
        return lines.stream().filter(line -> line.getId().equals(recievedId)).findFirst().orElse(getRandom());
    }

    public Line getByCode(String recievedCode) {
        return lines.stream().filter(line -> line.getCode().equals(recievedCode)).findFirst().orElse(getRandom());
    }

}
