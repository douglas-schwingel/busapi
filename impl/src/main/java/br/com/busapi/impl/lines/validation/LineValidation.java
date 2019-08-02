package br.com.busapi.impl.lines.validation;

import br.com.busapi.impl.exception.ApiException;
import br.com.busapi.impl.exception.errors.StandardError;
import br.com.busapi.impl.exception.issues.Issue;
import br.com.busapi.impl.lines.models.Line;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LineValidation {

    public boolean isValidToSave(Line line) {
        Map<String, String> invalidFileds = new HashMap<>();
        if (!idIsValid(line.getId())) invalidFileds.put("id", String.valueOf(line.getId()));
        if (!nameIsValid(line.getName())) invalidFileds.put("nome", line.getName());
        if (!codeIsValid(line.getCode()))invalidFileds.put("codigo", line.getCode());
        if (!coordinatesAreValid(line.getCoordinates())) invalidFileds.put("coordenadas", line.getCoordinates().toString());

        if (invalidFileds.size() == 0) {
            return true;
        } else {
            StringBuilder message = new StringBuilder();
            message.append("Invalid fields:");
            invalidFileds.forEach((k, v) -> message.append(String.format(" | Field: %s with value %.30s", k, v)));
            throw new ApiException(StandardError.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .name(HttpStatus.BAD_REQUEST.name())
                    .message(message.toString())
                    .issue(new Issue(new IllegalArgumentException("Number of invalid fields: " + invalidFileds.size())))
                    .suggestedApplicationAction("Verify form fields before making the request")
                    .suggestedUserAction("Verify invalid fields and try again")
                    .build());
        }
    }

    public boolean coordinatesAreValid(List<Double[]> coordinates) {
        if (coordinates != null) {
            if (coordinates.isEmpty()) return false;
            coordinates.forEach(c -> {
                if (c.length != 2){
                    throw apiExceptionForInvalidCoordinates("Coordinate must have both lat and lng!");
                }
                if (c[0] > -29.954468
                        || c[0] < -30.261371
                        || c[1] > -51.087028
                        || c[1] < -51.266828) {
                    throw apiExceptionForInvalidCoordinates(String.format("Invalid coordinates - [%.6f : %.6f]"
                            , c[0], c[1]));
                }
            });
            return true;
        } else {
            return false;
        }
    }

    private ApiException apiExceptionForInvalidCoordinates(String message) {
        return new ApiException(StandardError.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .name(HttpStatus.BAD_REQUEST.name())
                .message("Invalid coordinate: Coordinates must be within Porto Alegre's territory")
                .issue(new Issue(
                        new IllegalArgumentException(message)))
                .suggestedApplicationAction("Make verification before sending parameters")
                .suggestedUserAction("Verify the limits of Porto Alegre and try adding new coordinates")
                .build());
    }

    public boolean codeIsValid(String code) {
        if (code != null) {
            return code.matches("[A-Z0-9-]{2,7}")
                    && !code.isEmpty()
                    && !code.isBlank();
        } else {
            return false;
        }
    }

    public boolean idIsValid(Integer id) {
        if (id != null) {
            return id > 0;
        } else {
            return false;
        }
    }

    public boolean nameIsValid(String name) {
        if (name != null) {
            return name.matches("[A-Za-z0-9-_]{1,25}")
                    && !name.isBlank()
                    && !name.isEmpty();
        } else {
            return false;
        }
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
