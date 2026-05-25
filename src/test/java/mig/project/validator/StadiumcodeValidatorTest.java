package mig.project.validator;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import mig.project.domein.Wedstrijd;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class StadiumcodeValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void valideStadiumcodeGeeftGeenFouten() {
        Wedstrijd wedstrijd = geldigeWedstrijd();
        wedstrijd.setStadiumcode(1234);
        wedstrijd.setChecksum(1234 % 97);

        assertThat(validator.validate(wedstrijd)).isEmpty();
    }

    @Test
    void ongeldigeChecksumGeeftValidatiefout() {
        Wedstrijd wedstrijd = geldigeWedstrijd();
        wedstrijd.setStadiumcode(1234);
        wedstrijd.setChecksum(99);

        assertThat(validator.validate(wedstrijd))
                .extracting("message")
                .contains("{validation.stadiumcode.ongeldig}");
    }

    private Wedstrijd geldigeWedstrijd() {
        Wedstrijd wedstrijd = new Wedstrijd();
        wedstrijd.setLandA("Belgie");
        wedstrijd.setLandB("Mexico");
        wedstrijd.setDatumTijd(LocalDateTime.of(2026, 6, 12, 18, 0));
        wedstrijd.setLocatie("NY");
        wedstrijd.setStadiumcode(1000);
        wedstrijd.setChecksum(1000 % 97);
        return wedstrijd;
    }
}
