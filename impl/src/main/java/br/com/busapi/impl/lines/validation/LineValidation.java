package br.com.busapi.impl.lines.validation;

import br.com.busapi.impl.lines.models.Line;
import org.springframework.stereotype.Service;

@Service
public class LineValidation {

    public boolean isValid(Line line) {
        return nameIsValid(line.getName())
                && idIsValid(line.getId())
                && codeIsValid(line.getCode());
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
}
