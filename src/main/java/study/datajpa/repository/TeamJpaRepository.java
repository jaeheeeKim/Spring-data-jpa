package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Team;

import java.util.List;
import java.util.Optional;

@Repository // Spring이 컴포넌트 스캔을 해야하기때문에
public class TeamJpaRepository {

    @PersistenceContext // entity manager를 injection 해주는 annotation
    private EntityManager em;

    public Team save(Team team) {
        em.persist(team);
        return team;
    }

    // ⭐update 라는 메소드는 필요 없이 변경감지로 DB에 업데이트 쿼리를 날림!

    public void delete(Team team) {
        em.remove(team);
    }

    public List<Team> findAll() {
        return em.createQuery("select t from Team t", Team.class)
                .getResultList();
    }

    public Optional<Team> findById(Long id) {
        Team team = em.find(Team.class, id);
        return Optional.ofNullable(team);
    }

    public long count() {
        return em.createQuery("select count(t) from Team t", Long.class)
                .getSingleResult();
    }
}
