package br.com.busapi.contract.v1.lines.facade;

import br.com.busapi.contract.v1.lines.mapper.LinesMapper;
import br.com.busapi.contract.v1.lines.models.response.ListBusLinesResponse;
import br.com.busapi.impl.lines.facade.LinesFacadeImpl;
import br.com.busapi.impl.lines.models.Line;
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

    public ListBusLinesResponse listAllBusLines() {
        List<Line> lines = facadeImpl.listAllBusLines();
        return mapper.mapToListBusLinesResponse(lines);
    }
}
