package br.com.busapi.impl.lines.validation;

import br.com.busapi.impl.exception.ApiException;
import br.com.busapi.impl.lines.models.Line;
import br.com.busapi.impl.lines.test.utils.LinesTestsUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;

import static org.junit.Assert.*;

public class LineValidationTest {

    private LineValidation validation = new LineValidation();
    private LinesTestsUtils utils = new LinesTestsUtils();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldReturnTrueForValidLine() {
        boolean validToSave = validation.isValidToSave(utils.getRandom());
        assertTrue(validToSave);
    }

    @Test
    public void shouldReturnThrowApiExceptionForInvalidLine() {
        Line random = utils.getRandom();
        random.setName("1ª tentativa");
        exception.expect(ApiException.class);
        exception.expectMessage("Invalid fields:" +
                " | Field: nome with value " + random.getName());

        validation.isValidToSave(random);
    }

    @Test
    public void shouldReturnFalseForInvalidName() {
        Line random = utils.getRandom();

        assertTrue(validation.nameIsValid(random.getName()));

        random.setName(null);
        assertFalse(validation.nameIsValid(random.getName()));

        random.setName("");
        assertFalse(validation.nameIsValid(random.getName()));

        random.setName("Nót Valid");
        assertFalse(validation.nameIsValid(random.getName()));

        random.setName("   ");
        assertFalse(validation.nameIsValid(random.getName()));
    }

    @Test
    public void mustFormatAnInvalidNameToAValidOne() {
        String invalid = "VÁLID / NAME";
        assertFalse(validation.nameIsValid(invalid));

        String valid = validation.formatName(invalid);
        assertTrue(validation.nameIsValid(valid));
    }

    @Test
    public void mustReturnFalseForInvalidCoordinates() {
        Line random = utils.getRandom();

        random.setCoordinates(null);
        assertFalse(validation.coordinatesAreValid(random.getCoordinates()));
    }

    @Test
    public void shouldThrowApiExceptionForInavlidCoordinates() {
        Line random = utils.getRandom();
        random.setCoordinates(Collections.singletonList(new Double[]{30.0, -51.0}));
        exception.expect(ApiException.class);
        exception.expectMessage("Invalid coordinate: Coordinates must be within Porto Alegre's territory");

        validation.coordinatesAreValid(random.getCoordinates());

    }


    @Test
    public void mustReturnFalseForEachNullField() {

        assertFalse(validation.idIsValid(null));
        assertFalse(validation.nameIsValid(null));
        assertFalse(validation.codeIsValid(null));
    }

    @Test
    public void mustReturnLineWithNewCoordinates() {
        Line line = utils.getById(1095);
        Line saved = Line.builder()
                .id(1099)
                .name("VIAMAO-KRAHE")
                .code("109-9")
                .coordinates(Collections.singletonList(new Double[]{30.0, -51.0}))
                .build();
        validation.validateFieldsToUpdate(line, saved);

        assertEquals(line.getName(), saved.getName());
        assertEquals(line.getCode(), saved.getCode());
        assertEquals(line.getCoordinates(), saved.getCoordinates());
    }

}