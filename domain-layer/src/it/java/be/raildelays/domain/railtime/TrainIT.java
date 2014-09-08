package be.raildelays.domain.railtime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test validation of Train bean.
 *
 * @author Almex
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class TrainIT {

    private Validator validator;

    @Before
    public void setUp() throws Exception {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testEmptyId() {
        Set<ConstraintViolation<Train>> constraintViolations = validator.validate(new Train(""));

        assertFalse("A train with an empty id should not be valid", constraintViolations.size() == 0);
    }

    @Test
    public void testValidId() {
        Set<ConstraintViolation<Train>> constraintViolations = validator.validate(new Train("IC466"));

        assertTrue("This train id should be valid", constraintViolations.size() == 0);
    }


    @Test
    public void testInvalidId() {
        Set<ConstraintViolation<Train>> constraintViolations = validator.validate(new Train("IC46600"));

        assertTrue("This train id should be invalid", constraintViolations.size() == 0);
    }

}
