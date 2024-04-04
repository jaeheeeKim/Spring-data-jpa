package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    @PersistenceContext // Spring Boot가 Spring Container(JPA에 있는 영속성 컨텍스트) entityManager를 가져와줌
    private EntityManager em; // 엔티티를 넣으면 JPA가 알아서 insert랑 select쿼리를 DB에 날려줌

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    // ⭐update 라는 메소드는 필요 없이 변경감지로 DB에 업데이트 쿼리를 날림!

    public void delete(Member member) {
        em.remove(member);
    }

    public List<Member> findAll(){
        // JPQL은 SQL과 거의 같은데 문법에 약간 차이가 있음 , jpql 쿼리가 실행이 되고 sql로 번역이 됨
        // 그래서 이 entity 대신에 테이블을 조회 해야하니까 sql로 번역된 후 실제 DB에서 데이터를 가져와서 반환을 해줌
        return em.createQuery("select m from Member m", Member.class) // Member Entity
                .getResultList();
    }
    
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }
    
    public long count() { // count가 long 타입임
        return em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult(); // 결과 하나만
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age) {
        return em.createQuery("select m from Member m where m.username = :username and m.age > :age")
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    public List<Member> findByUsername(String username) {
        return em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", username)
                .getResultList();
    }

    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m from Member m where m.age = :age order by m.username desc")
                .setParameter("age", age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

    public int bulkAgePlus(int age) {
        return em.createQuery("update Member m set m.age = m.age +1 where m.age >= :age")
                .setParameter("age", age)
                .executeUpdate();
    }
}