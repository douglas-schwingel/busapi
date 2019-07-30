package br.com.busapi.contract.v1.lines.mapper;

import br.com.busapi.contract.v1.lines.models.response.BusLineResponse;
import br.com.busapi.contract.v1.lines.models.response.BusLinetinerary;
import br.com.busapi.contract.v1.lines.models.response.DeletedBusLineResponse;
import br.com.busapi.contract.v1.lines.models.response.ListBusLineResponse;
import br.com.busapi.impl.lines.models.Line;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LinesMapper {

    public BusLineResponse mapToBusLineResponse(Line line) {
        return BusLineResponse.builder()
                .id(line.getId())
                .code(line.getCode())
                .name(line.getName()).build();
    }


    public ListBusLineResponse mapToListBusLinesResponse(List<Line> lines) {
        var builder = ListBusLineResponse.builder();
        lines.forEach(l -> builder.line(mapToBusLineResponse(l)));
        return builder.build();
    }

    public BusLinetinerary mapToBusLineItinerary(Line line) {
        return BusLinetinerary.builder()
                .name(line.getName())
                .id(line.getId())
                .code(line.getCode())
                .coordinates(line.getCoordinates())
                .build();
    }

    public DeletedBusLineResponse mapToDeletedBusLineResponse(Line deleteLine) {
        return DeletedBusLineResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Line with id " + deleteLine.getId() + " ]deleted with success.")
                .deletedLine(deleteLine)
                .build();
    }
}
