package be.raildelays.domain.entities;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Test validation of TrainLine bean.
 *
 * @author Almex
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class TrainLineIT {

    private Validator validator;

    @Before
    public void setUp() throws Exception {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testEmptyId() {
        Set<ConstraintViolation<TrainLine>> constraintViolations = validator.validate(
                new TrainLine.Builder((Long) null).build(false)
        );

        assertEquals("A trainLine with an empty id should not be valid", 1, constraintViolations.size());
    }

    @Test
    public void testValidId() {
        Set<ConstraintViolation<TrainLine>> constraintViolations = validator.validate(
                new TrainLine.Builder(466L).build(false)
        );

        assertEquals("This trainLine id should be valid", 0, constraintViolations.size());
    }

}
