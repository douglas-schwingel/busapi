package br.com.busapi.contract.v1.lines.mapper;

import br.com.busapi.contract.v1.lines.models.response.BusLineResponse;
import br.com.busapi.contract.v1.lines.models.response.ListBusLinesResponse;
import br.com.busapi.impl.lines.models.Line;
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


    public ListBusLinesResponse mapToListBusLinesResponse(List<Line> lines) {
        var builder = ListBusLinesResponse.builder();
        lines.forEach(l -> builder.line(mapToBusLineResponse(l)));
        return builder.build();
    }
}