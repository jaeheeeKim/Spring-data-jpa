package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository; // 이렇게 인터페이스만 만들어주면 구현체를 Spring Data JPA가 알아서 다 만들어 injection 해줌!⭐
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        // 있을수도 있고 없을수도 있어서 Optional로 가져옴
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        /* 단건 조회 검증 */
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

//        findMember1.setUsername("member!!!"); // update 쿼리 나감! 변경감지⭐더티체킹

        /* 리스트 조회 검증 */
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        /* 카운트 검증 */
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        /* 삭제 검증 */
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }


    /*@Test
    public void findByUsernameAndAgeGreaterThen() { // 사실 이런거 귀찮으니 스프링데이터JPA가 알아서 해줌...
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            assertThat(s).isEqualTo(m1.getUsername());
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

//        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
//        for (Member member : result) {
//            System.out.println("member = " + member);
//        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // List는 없으면 null이 아니라 entity collection을 반환해줌~! 따라서 if(aaa != null) 이런거 안해도 됨!!!
        List<Member> aaa = memberRepository.findListByUsername("AAA");
        // 자바 8 이후에 Optional로 당연히 데이터가 있거나 없거나 알아서 해줌 
        Member findMember = memberRepository.findMemberByUsername("AAA");
        Optional<Member> aaa1 = memberRepository.findOptionalByUsername("AAA");
    }

    @Test
    public void paging() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
//        Page<Member> page = memberRepository.findByAge(age, pageRequest); // totalCount 쿼리까지 날려줌!(반환 타입에 따라 다르긴함)
        Slice<Member> page = memberRepository.findByAge(age, pageRequest);
//        List<Member> pageList = memberRepository.findByAge(age, pageRequest);
        Page<Member> pageTop = memberRepository.findByTopAge(age, pageRequest);

        // controller에서 엔티티를 바로 노출 하지 말고 Dto로 반환하길 권장함⭐⭐⭐
        Slice<MemberDto> dtoMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        //then
        List<Member> content = page.getContent();

//        assertThat(page.getTotalElements()).isEqualTo(5);// 총 컨텐츠 개수
        assertThat(page.getNumber()).isEqualTo(0);      // 페이지 번호
//        assertThat(page.getTotalPages()).isEqualTo(2);  // 총 페이지 개수(0페이지 3개, 1페이지 2개)
        assertThat(page.isFirst()).isTrue();                    // 첫번째 페이지인지?
        assertThat(page.hasNext()).isTrue();                    // 다음 페이지가 있는지?
    }

    @Test
    public void bulkUpdate() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);
//        em.clear(); // bulk 연산 하고나면 영속성컨텍스트 날려 주기 위해서 위에서 entityManager 추가하고

        // bulk 연산하고 바로 em.clear() 안해주면 영속성컨텍스트에는 40, DB에만 41로 저장이 되버림!
        // 🚫따라서 반드시!!! bulk 연산을 하고나면 영속성컨텍스트를 날려버림🚫
        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);

        // then
        assertThat(resultCount).isEqualTo(3);
    }

    *//** @EntityGraph 설명전후 **//*
    @Test
    public void findMemberLazy() {
        // given
        // member1 -> teamA
        // member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        Member member3 = new Member("member1", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        em.flush();
        em.clear();

        // when (N + 1 문제)
        // select Member(N=2) 1
        // 이 쿼리를 날리면 member만 가져옴 그래서 team에(LAZY로 설정이 되어있다면) 가짜 객체를 넣어둠 = 프록시 초기화
//        List<Member> members = memberRepository.findAll();
        List<Member> members = memberRepository.findMemberFetchJoin(); // ⭐패치 조인을 해주면 Member 조회할 때 한방 쿼리로 Team 까지 함께 조회!

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            // findAll()은 team의 proxy 가짜 객체만 가져옴, findMemberFetchJoin()은 진짜 entity가 나옴
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }

        // @EntityGraph는 패치조인을 편리하게 할 수 있다! 정도만 알고 있기!
        List<Member> member = memberRepository.findEntityGraphByUsername("member1");
    }

    @Test
    public void queryHint() {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
//        Member findMember = memberRepository.findById(member1.getId()).get(); // 실무에서 이렇게 get()으로 꺼내면 안됨~!
//        findMember.setUsername("member2");
//
//        em.flush(); // flush를 통해 Dirty Checking 하여 변경 감지하고! update 쿼리 날림!
//        // ▶️ 변경 감지 체크하는 과정에서 데이터도 체크 해야하기때문에 최적화가 많이 되어있더라도 비용이 더 들게 된다.
//        // 따라서 단순히 조회만 할거라면 최적화하는 방법을 Hibernate가 제공하고, JPA 표준은 제공 안함!

        Member findMember = memberRepository.findReadOnlyByUsername("member1"); // read only이기 때문에 변경이 안된다고 가정하고 밑에 있는 set update 쿼리 무시함
        findMember.setUsername("member2");

        em.flush();
    }*/

    @Test
    public void lock() {
        // given
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();

        // when
        List<Member> result = memberRepository.findLockByUsername("member1");
    }

    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void projections() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();
        // 구현체가 아닌 인터페이스인 UsernameOnly인데 스프링데이터JPA가 구현체를 만들어줌
        // Entity 가져와서 Dto로 바꾸고 이런 과정 없이 간단하게 사용 가능하고 최적화 가능!⭐
        List<UsernameOnlyDto> result = memberRepository.findProjectionsByUsername("m1", UsernameOnlyDto.class);
        for (UsernameOnlyDto usernameOnly : result) {
            System.out.println("usernameOnly = " + usernameOnly);
        }

        List<NestedClosedProjections> result2 = memberRepository.findProjectionsByUsername("m1", NestedClosedProjections.class);
        for (NestedClosedProjections nestedClosedProjections : result2) {
            String username = nestedClosedProjections.getUsername();
            System.out.println("username = " + username);
            String teamName = nestedClosedProjections.getTeam().getName();
            System.out.println("teamName = " + teamName);

        }
    }

    @Test
    public void nativeQuery() { // 제약이 많기 때문에 잘 안씀
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        Member result = memberRepository.findByNativeQuery("m1");
        System.out.println("result = " + result);

        Page<MemberProjection> resultProjections = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        List<MemberProjection> content = resultProjections.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection.getUsername() = " + memberProjection.getUsername());
            System.out.println("memberProjection.getTeamName() = " + memberProjection.getTeamName());
        }
    }
}

