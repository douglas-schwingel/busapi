package br.com.busapi.contract.v1.lines.controller;

import br.com.busapi.contract.v1.lines.facade.LinesControllerFacade;
import br.com.busapi.contract.v1.lines.models.response.BusLineResponse;
import br.com.busapi.contract.v1.lines.models.response.ListBusLinesResponse;
import br.com.busapi.impl.exception.ApiException;
import br.com.busapi.impl.exception.errors.ResponseError;
import io.swagger.annotations.*;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"Lines", "v1"}, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@RequestMapping("/v1/lines")
public class LinesController {

    private final LinesControllerFacade controllerFacade;

    public LinesController(LinesControllerFacade controllerFacade) {
        this.controllerFacade = controllerFacade;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses({
            @ApiResponse(code = 201, message = "Created", response = ListBusLinesResponse.class),
            @ApiResponse(code = 403, message = "Method not Allowed", response = ResponseError.class),
            @ApiResponse(code = 404, message = "Not found", response = ResponseError.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ResponseError.class)
    })
    @ApiOperation(value = "Save all buses from DataPOA")
    @PostMapping("/saveAll")
    public ListBusLinesResponse saveAll() {
        return controllerFacade.saveAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = ListBusLinesResponse.class),
            @ApiResponse(code = 403, message = "Method not Allowed", response = ResponseError.class),
            @ApiResponse(code = 404, message = "Not found", response = ResponseError.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ResponseError.class)
    })
    @ApiOperation(value = "Get all buses near", notes = "Find all bus lines within the informed distance (in kilometers)" +
            " from informed point(lat and lng)")
    @GetMapping("/find_near")
    public ListBusLinesResponse findNear(@RequestParam
                                                 @ApiParam(value = "Latitude", example = "-30.056697") Double lat,
                                         @RequestParam
                                                 @ApiParam(value = "Longitude", example = "-51.156185") Double lng,
                                         @RequestParam
                                                 @ApiParam(value = "Distance(Km)", example = "0.05") Double distance
    ) {
        Point point = new Point(lat, lng);
        Distance dist = new Distance(distance, Metrics.KILOMETERS);
        return controllerFacade.findNear(point, dist);
    }

    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
            @ApiResponse(code = 201, message = "Created", response = BusLineResponse.class),
            @ApiResponse(code = 204, message = "No content", response = ResponseError.class),
            @ApiResponse(code = 403, message = "Method not Allowed", response = ResponseError.class),
            @ApiResponse(code = 404, message = "Not found", response = ResponseError.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ResponseError.class)
    })
    @ApiOperation(value = "Get all buses near", notes = "Find all bus lines within the informed distance (in kilometers)" +
            " from informed point(lat and lng)")
    @GetMapping("/{name}")
    public BusLineResponse findBusByName(@PathVariable String name) {
        return controllerFacade.findByName(name);
    }

}
