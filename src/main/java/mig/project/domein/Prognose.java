package mig.project.domein;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "prognoses", uniqueConstraints = @UniqueConstraint(columnNames = {"gebruiker_id", "wedstrijd_id"}))
@Getter
@Setter
@NoArgsConstructor
public class Prognose {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "{validation.doelpunten.verplicht}")
    @Min(value = 0, message = "{validation.doelpunten.min}")
    private Integer voorspeldeDoelpuntenA;

    @NotNull(message = "{validation.doelpunten.verplicht}")
    @Min(value = 0, message = "{validation.doelpunten.min}")
    private Integer voorspeldeDoelpuntenB;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gebruiker_id", nullable = false)
    private Gebruiker gebruiker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wedstrijd_id", nullable = false)
    private Wedstrijd wedstrijd;

    private Integer behaaldePunten;
}
