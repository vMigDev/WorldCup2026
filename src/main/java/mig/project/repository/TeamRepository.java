package mig.project.repository;

import mig.project.domein.Team;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByTeamNaam(String teamNaam);

    Optional<Team> findByUitnodigingscode(String uitnodigingscode);

    @Query("""
            select distinct t
            from Team t
            left join fetch t.leden
            """)
    List<Team> findAllMetLeden();

    @Query("""
            select t
            from Team t
            order by t.teamNaam asc
            """)
    List<Team> findTop10ByOrderByTeamNaamAsc(Pageable pageable);
}
