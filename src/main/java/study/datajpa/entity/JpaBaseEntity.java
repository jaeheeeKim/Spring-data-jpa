package study.datajpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 진짜 상속 관계는 아니고 속성들 내려서 테이블에서 같이 쓸 수 있게 데이터만 공유하는?
public class JpaBaseEntity { // 이렇게 따로 엔티티를 만들면 아무리 엔티티가 많아도 등록일/수정일에 대해서 자동화 할 수 있음⭐

    @Column(updatable = false) // 변경 불가능
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() { // 저장 전 이벤트 발생
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now; // 실제 쿼리 업데이트할 때 nulll이 있으면 쿼리가 지저분 해지기 때문에 값을 채워두는거임!
    }

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
