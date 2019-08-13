package br.com.busapi.contract.v1.lines.controller.facade;

import br.com.busapi.contract.v1.lines.mapper.LinesMapper;
import br.com.busapi.contract.v1.lines.models.request.LineRequest;
import br.com.busapi.contract.v1.lines.models.response.BusLineResponse;
import br.com.busapi.contract.v1.lines.models.response.BusLineItinerary;
import br.com.busapi.contract.v1.lines.models.response.ListBusLineResponse;
import br.com.busapi.impl.lines.facade.LinesFacadeImpl;
import br.com.busapi.impl.lines.models.Line;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LinesControllerFacade {

    private final LinesFacadeImpl facadeImpl;

    public ListBusLineResponse findNear(Point point, Distance dist) {
        List<Line> lines = facadeImpl.findNear(point, dist);
        return LinesMapper.mapToListBusLinesResponse(lines);
    }

    public ListBusLineResponse findByName(String name) {
        return LinesMapper.mapToListBusLinesResponse(facadeImpl.findByName(name));
    }

    public Page<BusLineResponse> findAll(Pageable pageable) {
        return facadeImpl.findAll(pageable).map(LinesMapper::mapToBusLineResponse);
    }

    public BusLineResponse saveOne(LineRequest lineRequest) {
        Line line = LinesMapper.mapToLine(lineRequest);
        return LinesMapper.mapToBusLineResponse(facadeImpl.saveOne(line));
    }

    public BusLineItinerary findById(Integer id) {
        return LinesMapper.mapToBusLineItinerary(facadeImpl.findById(id));
    }

    public void deleteLine(Integer id) {
        facadeImpl.deleteLine(id);
    }

    public BusLineResponse updateBusLine(LineRequest lineRequest) {
        Line line = LinesMapper.mapToLine(lineRequest);
        return LinesMapper.mapToBusLineResponse(facadeImpl.updateLine(line));
    }

    public BusLineItinerary findByCode(String code) {
        return LinesMapper.mapToBusLineItinerary(facadeImpl.findByCode(code));
    }
}
