package study.datajpa.repository;

public interface NestedClosedProjections { // 중첩 구조 처리

    String getUsername(); // 이 root 엔티티만 최적화되는
    TeamInfo getTeam();   // 두번째부터 조인 들어가면서 최적화가 안되는

    interface TeamInfo {
        String getName();
    }
}
