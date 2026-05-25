package mig.project.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import mig.project.domein.Wedstrijd;

public class StadiumcodeValidator implements ConstraintValidator<GeldigeStadiumcode, Wedstrijd> {

    @Override
    public boolean isValid(Wedstrijd wedstrijd, ConstraintValidatorContext context) {
        if (wedstrijd == null || wedstrijd.getStadiumcode() == null || wedstrijd.getChecksum() == null) {
            return true;
        }

        int code = wedstrijd.getStadiumcode();
        int checksum = wedstrijd.getChecksum();
        boolean geldig = code >= 1000 && code <= 9999 && (code % 97) == checksum;

        if (!geldig) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{validation.stadiumcode.ongeldig}")
                    .addPropertyNode("stadiumcode")
                    .addConstraintViolation();
        }

        return geldig;
    }
}
