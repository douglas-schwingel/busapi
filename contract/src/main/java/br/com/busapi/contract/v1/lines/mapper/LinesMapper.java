package br.com.busapi.contract.v1.lines.mapper;

import br.com.busapi.contract.v1.lines.models.request.LineRequest;
import br.com.busapi.contract.v1.lines.models.response.BusLineResponse;
import br.com.busapi.contract.v1.lines.models.response.BusLinetinerary;
import br.com.busapi.contract.v1.lines.models.response.ListBusLineResponse;
import br.com.busapi.impl.lines.models.Line;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LinesMapper {

    private LinesMapper() {}

    public static BusLineResponse mapToBusLineResponse(Line line) {
        return BusLineResponse.builder()
                .id(line.getId())
                .code(line.getCode())
                .name(line.getName()).build();
    }


    public static ListBusLineResponse mapToListBusLinesResponse(List<Line> lines) {
        var builder = ListBusLineResponse.builder();
        lines.forEach(l -> builder.line(mapToBusLineResponse(l)));
        return builder.build();
    }

    public static BusLinetinerary mapToBusLineItinerary(Line line) {
        return BusLinetinerary.builder()
                .name(line.getName())
                .id(line.getId())
                .code(line.getCode())
                .coordinates(line.getCoordinates())
                .build();
    }

    public static Line mapToLine(LineRequest lineRequest) {
        return Line.builder()
                .id(lineRequest.getId())
                .name(lineRequest.getNome())
                .code(lineRequest.getCodigo())
                .coordinates(lineRequest.getCoordenadas())
                .build();
    }
}
