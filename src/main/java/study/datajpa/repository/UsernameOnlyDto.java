package study.datajpa.repository;

public class UsernameOnlyDto {

    private final String username;

    public UsernameOnlyDto(String username) { // 이 생성자의 파라미터명을 분석해서 씀
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
