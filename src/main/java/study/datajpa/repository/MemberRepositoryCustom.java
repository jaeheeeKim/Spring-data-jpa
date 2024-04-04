package study.datajpa.repository;

import study.datajpa.entity.Member;

import java.util.List;

                // 복잡한 코드를 만들 때 쿼리 dsl로 Custom 해서 많이 씀
public interface MemberRepositoryCustom {

    List<Member> findMemberCustom();
}
