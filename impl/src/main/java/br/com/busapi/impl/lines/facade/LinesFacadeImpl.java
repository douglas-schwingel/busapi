package br.com.busapi.impl.lines.facade;

import br.com.busapi.impl.exception.ApiException;
import br.com.busapi.impl.exception.errors.StandartError;
import br.com.busapi.impl.exception.issues.Issue;
import br.com.busapi.impl.lines.integration.LinesOperations;
import br.com.busapi.impl.lines.models.Line;
import br.com.busapi.impl.lines.service.LinesService;
import br.com.busapi.impl.lines.validation.LineValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class LinesFacadeImpl {

    private final LinesService service;
    private final LinesOperations operations;
    private LineValidation validation;

    public LinesFacadeImpl(LinesService service, LinesOperations operations, LineValidation validation) {
        this.service = service;
        this.operations = operations;
        this.validation = validation;
    }

    public List<Line> saveAll() {
        List<Line> allLines = operations.listBusLines(new RestTemplate(), new ObjectMapper());
        return service.saveAll(allLines, operations);
    }

    public List<Line> findNear(Point point, Distance dist) {
        return service.findNear(point, dist);
    }

    public List<Line> findByName(String name) {
        if (validation.nameIsValid(name)) return service.findByNameContains(name);
        throw apiException("Invalid name", "Name contains invalid data", "Verify form data",
                "Verify the passed name and try again");
    }

    public Page<Line> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    public Line saveOne(Line line) {
        if (validation.isValid(line)) {
            if (service.findById(line.getId()) == null) {
                return service.saveOne(line);
            } else {
                return service.update(line);
            }
        }
        throw apiException("Invalid line values", "Line contains invalid data",
                "User passed invalid data. Try verifying your form to make sure everything " +
                        "is all right.", "Verify the passed information and try again");
    }

    private ApiException apiException(String exceptionMessage, String issueMessage, String appAction, String userAction) {
        return new ApiException(StandartError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(exceptionMessage)
                .name(HttpStatus.BAD_REQUEST.name())
                .issue(new Issue(new IllegalArgumentException(issueMessage)))
                .suggestedApplicationAction(appAction)
                .suggestedUserAction(userAction)
                .build());
    }

    public Line findById(Integer id) {
        return service.findById(id);
    }
}
