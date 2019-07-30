package br.com.busapi.impl.lines.validation;

import br.com.busapi.impl.lines.models.Line;
import br.com.busapi.impl.lines.test.utils.LinesTestsUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LineValidationTest {

    private LineValidation validation = new LineValidation();
    private LinesTestsUtils utils = new LinesTestsUtils();

    @Test
    public void shouldReturnTrueForValidLine() {
        boolean validToSave = validation.isValidToSave(utils.getRandom());
        assertTrue(validToSave);
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
//        TODO reavaliar esse teste pra fazer funcionar
        Line random = utils.getRandom();
        random.getCoordinates().forEach(d -> System.out.println(d[0] + " | " + d[1]));
        assertTrue(validation.coordinatesAreValid(random.getCoordinates()));

        random.setCoordinates(null);
        assertFalse(validation.coordinatesAreValid(random.getCoordinates()));

        random.setCoordinates(Collections.singletonList(new Double[]{30.0, -51.1}));
        assertFalse(validation.coordinatesAreValid(random.getCoordinates()));
    }

}