package mig.project.domein;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mig.project.validator.GeldigeStadiumcode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wedstrijden")
@GeldigeStadiumcode
@Getter
@Setter
@NoArgsConstructor
public class Wedstrijd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{validation.land.nietleeg}")
    @Column(nullable = false)
    private String landA;

    @NotBlank(message = "{validation.land.nietleeg}")
    @Column(nullable = false)
    private String landB;

    @NotNull(message = "{validation.datum.verplicht}")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(nullable = false)
    private LocalDateTime datumTijd;

    @NotBlank(message = "{validation.locatie.nietleeg}")
    @Column(nullable = false)
    private String locatie;

    @NotNull(message = "{validation.stadiumcode.verplicht}")
    @Column(nullable = false)
    private Integer stadiumcode;

    @NotNull(message = "{validation.checksum.verplicht}")
    @Column(nullable = false)
    private Integer checksum;

    private Integer doelpuntenA;
    private Integer doelpuntenB;

    @OneToMany(mappedBy = "wedstrijd", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prognose> prognoses = new ArrayList<>();
}
