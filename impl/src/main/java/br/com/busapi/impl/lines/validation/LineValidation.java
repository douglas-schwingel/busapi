package br.com.busapi.impl.lines.validation;

import br.com.busapi.impl.exception.ApiException;
import br.com.busapi.impl.exception.errors.StandartErrorImpl;
import br.com.busapi.impl.exception.issues.Issue;
import br.com.busapi.impl.lines.models.Line;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LineValidation {

    public boolean isValidToSave(Line line) {
        return nameIsValid(line.getName())
                && idIsValid(line.getId())
                && coordinatesAreValid(line.getCoordinates())
                && codeIsValid(line.getCode());
    }

    public boolean coordinatesAreValid(List<Double[]> coordinates) {
        coordinates.forEach(c -> {
            if (c[1] > -51.087028
                    || c[1] < -51.266828
                    || c[0] < -30.261371
                    || c[0] > -29.954468) {
                throw new ApiException(StandartErrorImpl.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .name(HttpStatus.BAD_REQUEST.name())
                        .message("Invalid coordinate: Coordinates must be within Porto Alegre's territory")
                        .issue(new Issue(
                                new IllegalArgumentException(String.format("Invalid coordinates - [%.6f : %.6f]"
                                        , c[0], c[1]))))
                        .suggestedApplicationAction("Make verification before sending parameters")
                        .suggestedUserAction("Verify the limits of Porto Alegre and try adding new coordinates")
                        .build());
            }
        });
        return true;
    }

    public boolean codeIsValid(String code) {
        return code.matches("[0-9-]{4,6}");
    }

    public boolean idIsValid(Integer id) {
        return (id > 0);
    }

    public boolean nameIsValid(String name) {
        return name.matches("[A-Za-z0-9-_]{1,25}")
                && !name.isBlank()
                && !name.isEmpty();
    }

    public String formatName(String name) {
        return name
                .replace(" ", "_")
                .replace("/", "-")
                .replace("ª", "-a")
                .replace("º", "-o")
                .replace("Á", "A")
                .replace("\\", "")
                .replace("Ç", "C")
                .replace("Ã", "A")
                .replace("ã", "a")
                .replace("õ", "o")
                .replace("é", "e")
                .replace("Õ", "O")
                .replace("Ô", "O")
                .replace("Ó", "O")
                .replace("Í", "I")
                .replace("Ê", "E")
                .replace("Á", "A")
                .replace("É", "E").toUpperCase();
    }

    public Line validateFieldsToUpdate(Line line, Line saved) {
        if (line.getName() != null && nameIsValid(line.getName())) saved.setName(formatName(line.getName()));
        if (line.getCode() != null && codeIsValid(line.getCode())) saved.setCode(line.getCode());
        List<Double[]> lineCoordinates = line.getCoordinates();
        if (lineCoordinates != null && !lineCoordinates.isEmpty()) saved.setCoordinates(lineCoordinates);
        return saved;
    }
}
