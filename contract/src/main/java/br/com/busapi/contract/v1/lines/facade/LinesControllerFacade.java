package br.com.busapi.contract.v1.lines.facade;

import br.com.busapi.contract.v1.lines.mapper.LinesMapper;
import br.com.busapi.contract.v1.lines.models.response.BusLineResponse;
import br.com.busapi.contract.v1.lines.models.response.BusLinetinerary;
import br.com.busapi.contract.v1.lines.models.response.ListBusLineResponse;
import br.com.busapi.impl.lines.facade.LinesFacadeImpl;
import br.com.busapi.impl.lines.models.Line;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public ListBusLineResponse saveAll() {
        List<Line> lines = facadeImpl.saveAll();
        return mapper.mapToListBusLinesResponse(lines);
    }

    public ListBusLineResponse findNear(Point point, Distance dist) {
        List<Line> lines = facadeImpl.findNear(point, dist);
        return mapper.mapToListBusLinesResponse(lines);
    }

    public ListBusLineResponse findByName(String name) {
        return mapper.mapToListBusLinesResponse(facadeImpl.findByName(name));
    }

    public Page<BusLineResponse> findAll(Pageable pageable) {
        return facadeImpl.findAll(pageable).map(mapper::mapToBusLineResponse);
    }

    public BusLineResponse saveOne(Line line) {
        return mapper.mapToBusLineResponse(facadeImpl.saveOne(line));
    }

    public BusLinetinerary findById(Integer id) {
        return mapper.mapToBusLineItinerary(facadeImpl.findById(id));
    }

    public void deleteLine(Integer id) {
        facadeImpl.deleteLine(id);
    }

    public BusLineResponse updateBusLine(Line line) {
        return mapper.mapToBusLineResponse(facadeImpl.updateLine(line));
    }
}
