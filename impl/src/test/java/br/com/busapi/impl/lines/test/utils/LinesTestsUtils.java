package br.com.busapi.impl.lines.test.utils;

import br.com.busapi.impl.lines.models.Line;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class LinesTestsUtils {

    private static List<Line> lines;

    public LinesTestsUtils() {
        lines = new ArrayList<>();

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
        int max = new Random().nextInt(10);
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
        return min + (max - min) * new Random().nextDouble();
    }

    private Double rngLgn() {
        Double min = -51.266828;
        Double max = -51.087028;
        return min + (max - min) * new Random().nextDouble();
    }

    public List<Line> getByNameContains(String pattern) {
        return lines.stream().filter(line -> line.getName().contains(pattern)).collect(Collectors.toList());
    }

    public List<Line> getAllLines() {
        return lines;
    }

    public Line getRandom() {
        return lines.get(new Random().nextInt(lines.size()));
    }

    public Line getById(Integer recievedId) {
        return lines.stream().filter(line -> line.getId().equals(recievedId)).findFirst().get();
    }

    public Line getByCode(String recievedCode) {
        return lines.stream().filter(line -> line.getCode().equals(recievedCode)).findFirst().get();
    }

}
