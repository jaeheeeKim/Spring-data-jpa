package study.datajpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
@NamedQuery(
        name = "Member.findByUsername",   // 큰 장점 : 애플리케이션 로딩 시점에 쿼리를 다 파싱해서 오류 잘 찾아냄
        query = "select m from Member m where m.username = :username"
)
@NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team")) // JPA 표준 스펙
public class Member extends BaseTimeEntity{

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계이니 연관관계의 주인은 member , 지연로딩을 위해 LAZY로⭐
    @JoinColumn(name = "team_id") // foreign key
    private Team team;

//    protected Member() { } // entity는 default 생성자로 파라미터 없이 있어야함 , 프록싱 할 때를 위해 열어 둬야함

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if(team != null) {
            changeTeam(team);
        }
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
