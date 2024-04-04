package study.datajpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

//@Repository 스프링 컴포넌트 스캔을 통해 알아서 인식 하기 때문에 생략 가능함!
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom { // 엄청 많은 기능을 제공함

    /** 1. 메소드 이름으로 쿼리 생성 *
    // ✅ 쿼리 단순하면
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    *//** 2. JPA NamedQuery **//*
    // 실무에서 안씀...
//    @Query(name = "Member.findByUsernames") // 없어도 NamedQuery를 찾아서 동작함!
    List<Member> findByUsername(@Param("username") String username);

    *//** 3. @Query, 리포지토리 메소드에 쿼리 정의하기 **//*
    // ✅ 쿼리 복잡하면
    // 엄청난 장점 : 애플리케이션 로딩 시점에 다 파싱을 하고 버그(오류)를 찾아서 알려줌!⭐
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    *//** 4. @Query, 값 조회하기 **//*
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    *//** 4. @Query, DTO 조회하기 **//*
    // Dto를 쓸때는 new operation 꼭 써줘야 함!!!
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    *//** 5. 파라미터 바인딩 **//*
    @Query("select m from Member m where m.username in :username")
    List<Member> findByNames(@Param("names") List<String> names);

    *//** 6. 반환 타입 **//*
    List<Member> findListByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); // 단건 Optional

    *//** 8. 스프링 데이터 JPA 페이징과 정렬 **//*
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m") // count 쿼리 분리해주기 (복잡하게 join 하지않도록 따로 작성해주는 성능 최적화)
    Slice<Member> findByAge(int age, Pageable pageable);

    Page<Member> findByTopAge(int age, Pageable pageable);

    *//** 9. 벌크성 수정 쿼리 **//*
    @Modifying(clearAutomatically = true) // executeUpdate랑 같은 기능이며, 안넣어주면 getResultList 같은건줄 앎
    @Query("update Member m set m.age = :age where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    *//** 10. EntityGraph 설명 전 Fetch 조인 **//*
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin(); // 매우 중요한 Fetch조인⭐⭐⭐ 기본적으로 left outer join 나감!*/




    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // 회원 데이터를 쓸 때 팀 데이터를 쓸 일이 너무 많기 때문에 EntityGraph로 뽑는다
    @EntityGraph(attributePaths = ("team"))
//    @EntityGraph("Member.all")
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    /** 11. JPA Hint & Lock **/
    // 주로 트래픽이 많은 API 몇개에 넣어서 최적화 하는 편! 처음부터 막 튜닝을 깔아서 개발 한다는 건 좋지 않음! 그런거 없이도 성능 잘 나옴!
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true")) // 내부적으로 최적화를 해서 스냅샷을 안 만든다. 변경 감지 체크를 안함!⭐
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    /** 간단하게 조회 가능한 Projections **/
//    List<UsernameOnlyDto> findProjectionsByUsername(@Param("username") String username);

    /** 동적 프로젝션 **/
    // 제너릭해주고 Class<T>해서 type을 넘기면 됨(쿼리가 똑같은데 넘기고싶은 타입만 다를 경우!)
    <T> List<T> findProjectionsByUsername(@Param("username") String username, Class<T> type);

    /** 네이티브 쿼리 **/
    @Query(value = "select * from Member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    /** 네이티브 쿼리 - Projections 활용 (정적 쿼리를 네이티브 쿼리로 짜야할때는 Projections를 활용할 수 있다.) **/
    @Query(value = "select m.member_id as id, m.username, t.name as teamname" +
            "from member m left join team t",
            countQuery = "select count(*) from member", // countQuery 꼭 넣어줘야함
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
    // 페이징 되는게 장점! 최근에 생긴 기능이라 실무에서 써 볼 기회가 없었음 이제 써볼까하심!
}
