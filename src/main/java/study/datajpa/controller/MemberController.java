package study.datajpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    /** ▶️ 도메인 클래스 컨버터 - 권장하지는 않음 **/
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) { // 스프링 데이터 JPA가 기본으로 해주는 기능임!
        return member.getUsername();
    }

    /** 페이징과 정렬 **/
    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5, sort = "username") Pageable pageable) { // Page는 결과 정보, Pageable은 파라미터 정보
        return memberRepository.findAll(pageable)
                .map(MemberDto::new);
//                .map(member -> new MemberDto(member));

//        Page<Member> page = memberRepository.findAll(pageable); // ⭐ 20개씩 페이징 ⭐
//        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
//        return map; // ctrl+alt+n 인라인
    }

    @PostConstruct // 스프링 애플리케이션 올라올 때 한 번 실행되는거임!
    public void init() {
//        memberRepository.save(new Member("userA"));
        for(int i = 0; i<100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }
}







