package br.com.busapi.contract.v1.lines.controller;

import br.com.busapi.contract.v1.lines.facade.LinesControllerFacade;
import br.com.busapi.contract.v1.lines.models.response.ListBusItineraryResponse;
import br.com.busapi.contract.v1.lines.models.response.ListBusLinesResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.web.bind.annotation.*;


@RestController("/v1/lines")
public class LinesController {

    private final LinesControllerFacade controllerFacade;

    public LinesController(LinesControllerFacade controllerFacade) {
        this.controllerFacade = controllerFacade;
    }

    @ApiOperation(value = "List all bus lines")
    @PostMapping("/saveAll")
    public ListBusLinesResponse listAllBusLinesWithing() {
        return controllerFacade.saveAll();
    }

    @ApiOperation(value = "List single bus itinerary")
    @GetMapping("/")
    public ListBusLinesResponse listBusItinerary(@RequestParam Double lat,
                                                     @RequestParam Double lng,
                                                     @RequestParam Double distance
                                                     ) {
        Point point = new Point(lat, lng);
        Distance dist = new Distance(distance, Metrics.KILOMETERS);
        return controllerFacade.findNear(point, dist);
    }
}
