package br.com.busapi.impl.lines.facade;

import br.com.busapi.impl.exception.ApiException;
import br.com.busapi.impl.exception.errors.StandardError;
import br.com.busapi.impl.exception.issues.Issue;
import br.com.busapi.impl.lines.integration.LinesOperations;
import br.com.busapi.impl.lines.models.Line;
import br.com.busapi.impl.lines.service.LinesService;
import br.com.busapi.impl.lines.service.SaveThread;
import br.com.busapi.impl.lines.validation.LineValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.Semaphore;

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
        return service.saveAll(allLines, operations, validation, new SaveThread(), new Semaphore(2));
    }

    public List<Line> findNear(Point point, Distance dist) {
        return service.findNear(point, dist);
    }

    public List<Line> findByName(String name) {
        if (validation.nameIsValid(name)) return service.findByNameContains(name);
        throw invalidDataApiException("Invalid name: " + name, "Name contains invalid data", "Verify form data",
                "Verify the passed name and try again");
    }

    public Page<Line> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    public Line saveOne(Line line) {
        if (validation.isValidToSave(line)) {
            if (service.findById(line.getId()) == null) {
                return service.saveOne(line);
            } else {
                throw new ApiException(StandardError.builder()
                        .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                        .name(HttpStatus.METHOD_NOT_ALLOWED.name())
                        .message("Uptades should be done with PUT and not POST")
                        .issue(new Issue(new HttpRequestMethodNotSupportedException("Method PUT should" +
                                " be used instead of POST")))
                        .suggestedApplicationAction("Redirect to the PUT endpoint")
                        .suggestedUserAction("Contact the developer")
                        .build());
            }
        }
        throw invalidDataApiException("Invalid line values", "Line contains invalid data",
                "User passed invalid data. Try verifying your form to make sure everything " +
                        "is all right.", "Verify the passed information and try again");
    }

    public Line findById(Integer id) {
        Line line = service.findById(id);
        if (line == null) {
            throw invalidDataApiException("No registered line with id: " + id, "Unregistered id",
                    "Create verification before sending the request", "Verify the informed id and try again");
        }
        return line;
    }

    public void deleteLine(Integer id) {
        Line lineToBeDeleted = service.findById(id);
        if (!service.deleteLine(lineToBeDeleted)) {
            throw invalidDataApiException("No line with the id " + id, "Invalid line id",
                    "Contact us for more informations.", "Verify the id and try again.");
        }
    }

    private ApiException invalidDataApiException(String exceptionMessage, String issueMessage, String appAction, String userAction) {
        return new ApiException(StandardError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(exceptionMessage)
                .name(HttpStatus.BAD_REQUEST.name())
                .issue(new Issue(new IllegalArgumentException(issueMessage)))
                .suggestedApplicationAction(appAction)
                .suggestedUserAction(userAction)
                .build());
    }

    public Line updateLine(Line line) {
        if (validation.isValidToSave(line)) {
            Line validatedLine = validation.validateFieldsToUpdate(line, findById(line.getId()));
            return service.saveOne(validatedLine);
        }
        throw invalidDataApiException("No line with the id " + line.getId(), "Invalid line id",
                "Contact us for more informations.", "Verify the id and try again.");
    }

    public Line findByCode(String code) {
        if (validation.codeIsValid(code)) return service.findByCode(code);
        throw invalidDataApiException("Invalid code " + code, "The code " + code + " is not valid",
                "Contect us for more informations.", "Verify the code and try again");
    }
}
