package com.booking.booking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    /*
        Collection<Role>
            Role 객체들을 담을 수 있는 컬렉션 타입

        HashSet<>()
            중복 허용 X (한 사용자가 같은 권한 여러 개 가질 수 X, 다른 권한은 여러 개 가능)
            순서 보장 X

        --> Role을 저장할 때 Collection의 기능을 사용할 건데, 구체적으로는 HashSet의 특성(중복 제거, 빠른 검색)을 가진 저장소를 쓰겠다
    */

    /*
        EAGER: User 엔티티 로드할 때 Role도 즉시 함께 로드
        cascade: User 엔티티 변경 시 Role에도 적용할 작업들
            CascadeType.PERSIST: User 저장 시 Role도 저장 --> User 저장시 새 Role도 자동 저장
            CascadeType.MERGE: User 수정 시 Role도 수정 --> User 수정시 변경된 Role도 자동 수정
            CascadeType.DETACH: User 분리 시 Role도 분리 --> User 엔티티 데이터 변경 감시 중단 시 Role도 감시 중단
    */

    /*
        user_roles: 다대다 관계를 위한 연결 테이블 이름
        user_id: 연결 테이블의 User FK 컬럼명
        id: User 테이블의 PK 컬럼명
        role_id: 연결 테이블의 Role FK 컬럼명
        id: Role 테이블의 PK 컬럼명
    */
    @ManyToMany(fetch = FetchType.EAGER,
                cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH })
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles = new HashSet<>();
}
