package com.booking.booking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    /*
        @JsonIgnore
        - Role -> User -> Role -> User.. 같은 순환 참조 방지

        @ManyToMany
        - Role과 User는 다대다 관계
        - 한 Role은 여러 User 가질 수 있고, 한 User도 여러 Role 가질 수 있다

        mappedBy = "roles"
        - 이 관계의 주인이 User 엔티티의 roles 필드임을 명시
        - Role 엔티티에서는 User과의 관계를 읽기만 가능 (수정X)
        - 양방향 관계에서 한쪽만 @JoinTable을 가져야 하므로, Role에서는 mappedBy로 처리
     */
    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Collection<User> users = new HashSet<>();

    public Role(String name) {
        this.name = name;
    }

    public void assignRoleToUser(User user) {
        user.getRoles().add(this);
        this.getUsers().add(user);
    }

    public void removeUserFromRole(User user) {
        user.getRoles().remove(this);
        this.getUsers().remove(user);
    }

    public void removeAllUsersFromRole() {
        if (this.getUsers() != null) {
            List<User> roleUsers = this.getUsers().parallelStream().toList();
            roleUsers.forEach(this :: removeUserFromRole);
        }
    }

    public String getName() {
        return name != null ? name : "";
    }
}
