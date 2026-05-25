package mig.project.rest;

import lombok.RequiredArgsConstructor;
import mig.project.exceptions.WedstrijdNietGevondenException;
import mig.project.rest.dto.WedstrijdRestDto;
import mig.project.service.WedstrijdService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/wedstrijden")
@RequiredArgsConstructor
public class WedstrijdRestController {

    private final WedstrijdService wedstrijdService;

    @GetMapping("/datum/{datum}")
    public ResponseEntity<List<WedstrijdRestDto>> getWedstrijdenOpDatum(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datum) {

        List<WedstrijdRestDto> wedstrijden = wedstrijdService.getWedstrijdenOpDatum(datum).stream()
                .map(WedstrijdRestDto::fromEntity)
                .toList();

        return ResponseEntity.ok(wedstrijden);
    }

    @GetMapping("/capaciteit/{stadiumcode}")
    public ResponseEntity<Integer> getStadionCapaciteit(@PathVariable Integer stadiumcode) {
        // dit is puur om de foutafhandeling te testen
        if (stadiumcode < 1000 || stadiumcode > 9999) {
            throw new IllegalArgumentException("Ongeldige stadiumcode voor REST API");
        }

        return ResponseEntity.ok(stadiumcode * 10);
    }
}