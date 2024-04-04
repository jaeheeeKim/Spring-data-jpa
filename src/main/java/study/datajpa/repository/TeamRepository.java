package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Team;

//@Repository 스프링 컴포넌트 스캔을 통해 알아서 인식 하기 때문에 생략 가능함!
public interface TeamRepository extends JpaRepository<Team, Long> {
}
