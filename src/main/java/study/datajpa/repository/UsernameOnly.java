package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

// 엔티티 대신에 DTO를 편리하게 조회할 때 사용
public interface UsernameOnly {

    @Value("#{'target.username'+' '+'target.age'}") // Open Projections
    String getUsername();
}
