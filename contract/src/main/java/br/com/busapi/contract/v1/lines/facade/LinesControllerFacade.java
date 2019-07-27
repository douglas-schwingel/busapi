package br.com.busapi.contract.v1.lines.facade;

import br.com.busapi.contract.v1.lines.mapper.LinesMapper;
import br.com.busapi.contract.v1.lines.models.response.BusLineResponse;
import br.com.busapi.contract.v1.lines.models.response.ListBusLinesResponse;
import br.com.busapi.impl.lines.facade.LinesFacadeImpl;
import br.com.busapi.impl.lines.models.Line;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LinesControllerFacade {

    private final LinesMapper mapper;
    private final LinesFacadeImpl facadeImpl;

    public LinesControllerFacade(LinesMapper mapper, LinesFacadeImpl linesFacade) {
        this.facadeImpl = linesFacade;
        this.mapper = mapper;
    }

    public ListBusLinesResponse saveAll() {
        List<Line> lines = facadeImpl.saveAll();
        return mapper.mapToListBusLinesResponse(lines);
    }

    public ListBusLinesResponse findNear(Point point, Distance dist) {
        List<Line> lines = facadeImpl.findNear(point, dist);
        return mapper.mapToListBusLinesResponse(lines);
    }

    public BusLineResponse findByName(String name) {
        return mapper.mapToBusLineResponse(facadeImpl.findByName(name));
    }
}
