package br.com.busapi.contract.v1.lines.controller;

import br.com.busapi.contract.v1.lines.controller.facade.LinesControllerFacade;
import br.com.busapi.contract.v1.lines.models.request.LineRequest;
import br.com.busapi.contract.v1.lines.models.response.BusLineResponse;
import br.com.busapi.contract.v1.lines.models.response.BusLineItinerary;
import br.com.busapi.contract.v1.lines.models.response.ListBusLineResponse;
import br.com.busapi.impl.exception.errors.ResponseError;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@Api(tags = {"Lines", "v1"}, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@RequestMapping("line-service/v1/lines")
@AllArgsConstructor
public class LinesController {

    private final LinesControllerFacade controllerFacade;

    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = Page.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ResponseError.class),
            @ApiResponse(code = 404, message = "Not found", response = ResponseError.class),
            @ApiResponse(code = 405, message = "Method not Allowed", response = ResponseError.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ResponseError.class)
    })
    @ApiOperation(value = "Get all buses", notes = "Find all bus lines")
    @GetMapping
    public Page<BusLineResponse> findAll(@RequestParam(required = false) Integer page,
                                         @RequestParam(required = false) Integer size) {
        Pageable pageable;
        if (page == null || size == null) {
            pageable = Pageable.unpaged();
        } else {
            pageable = PageRequest.of(page, size);
        }
        return controllerFacade.findAll(pageable);
    }

    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = BusLineItinerary.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ResponseError.class),
            @ApiResponse(code = 404, message = "Not found", response = ResponseError.class),
            @ApiResponse(code = 405, message = "Method not Allowed", response = ResponseError.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ResponseError.class)
    })
    @ApiOperation(value = "Get line by id", notes = "Find bus line by id")
    @GetMapping("/{id}")
    public BusLineItinerary findById(@PathVariable
                                    @ApiParam(example = "5566") Integer id) {
        return controllerFacade.findById(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses({
            @ApiResponse(code = 204, message = "Delete successful - No content"),
            @ApiResponse(code = 400, message = "Bad Request", response = ResponseError.class),
            @ApiResponse(code = 405, message = "Method not Allowed", response = ResponseError.class),
            @ApiResponse(code = 405, message = "Method not Allowed", response = ResponseError.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ResponseError.class)
    })
    @ApiOperation(value = "Delete bus line", notes = "Delete bus line by id")
    @DeleteMapping("/{id}")
    public void deleteBusLine(@PathVariable
                              @ApiParam(example = "5566") Integer id) {

        controllerFacade.deleteLine(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = ListBusLineResponse.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ResponseError.class),
            @ApiResponse(code = 404, message = "Not found", response = ResponseError.class),
            @ApiResponse(code = 405, message = "Method not Allowed", response = ResponseError.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ResponseError.class)
    })
    @ApiOperation(value = "Get all buses near", notes = "Find all bus lines within the informed distance (in kilometers)" +
            " from informed point(lat and lng)")
    @GetMapping("/find_near")
    public ListBusLineResponse findNear(@RequestParam
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
            @ApiResponse(code = 200, message = "OK", response = ListBusLineResponse.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ResponseError.class),
            @ApiResponse(code = 404, message = "Not found", response = ResponseError.class),
            @ApiResponse(code = 405, message = "Method not Allowed", response = ResponseError.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ResponseError.class)
    })
    @ApiOperation(value = "Get all by name", notes = "Find all bus lines that contains the given string")
    @GetMapping("/name/{name}")
    public ListBusLineResponse findBusByName(@PathVariable String name) {
        return controllerFacade.findByName(name);
    }

    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK", response = BusLineItinerary.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ResponseError.class),
            @ApiResponse(code = 404, message = "Not found", response = ResponseError.class),
            @ApiResponse(code = 405, message = "Method not Allowed", response = ResponseError.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ResponseError.class)
    })
    @ApiOperation(value = "Find bus by code")
    @GetMapping("/code/{code}")
    public BusLineItinerary findByCode(@PathVariable
                                      @ApiParam(example = "264-1") String code) {
        return controllerFacade.findByCode(code);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses({
            @ApiResponse(code = 201, message = "Created", response = BusLineResponse.class),
            @ApiResponse(code = 400, message = "Bad Request", response = ResponseError.class),
            @ApiResponse(code = 405, message = "Method not allowed", response = ResponseError.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ResponseError.class)
    })
    @ApiOperation(value = "Save bus line", notes = "Save new bus line")
    @PostMapping
    public BusLineResponse saveBusLine(@RequestBody LineRequest lineRequest) {
        return controllerFacade.saveOne(lineRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Updated", response = BusLineResponse.class),
            @ApiResponse(code = 204, message = "Delete successful - No content"),
            @ApiResponse(code = 400, message = "Bad Request", response = ResponseError.class),
            @ApiResponse(code = 405, message = "Method not Allowed", response = ResponseError.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ResponseError.class)
    })
    @ApiOperation(value = "Update bus line", notes = "Update a bus line")
    @PutMapping
    public BusLineResponse updateBusLine(@RequestBody LineRequest lineRequest) {
        return controllerFacade.updateBusLine(lineRequest);
    }

}
