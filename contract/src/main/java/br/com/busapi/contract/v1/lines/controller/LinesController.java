package br.com.busapi.contract.v1.lines.controller;

import br.com.busapi.contract.v1.lines.facade.LinesControllerFacade;
import br.com.busapi.contract.v1.lines.models.response.ListBusItineraryResponse;
import br.com.busapi.contract.v1.lines.models.response.ListBusLinesResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/v1/lines")
public class LinesController {

    private final LinesControllerFacade controllerFacade;

    public LinesController(LinesControllerFacade controllerFacade) {
        this.controllerFacade = controllerFacade;
    }

    @ApiOperation(value = "List all bus lines")
    @PostMapping("/saveAll")
    public ListBusLinesResponse listAllBusLines() {
        return controllerFacade.listAllBusLines();
    }

    @ApiOperation(value = "List single bus itinerary")
    public ListBusItineraryResponse listBusItinerary() {
        return null;
    }
}
