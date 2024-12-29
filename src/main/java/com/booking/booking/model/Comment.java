package com.booking.booking.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;




    /*
        @ManyToOne(fetch = FetchType.LAZY)
        - 다수의 댓글이 하나의 게시글에 속함
        - LAZY: 댓글 조회시 게시글 정보는 실제 필요할 때만 조회

        @JoinColumn(name = "board_id")
        - 외래키 컬럼명을 board_id로 지정
        - Board 테이블의 PK와 연결
        - 이 댓글이 어느 게시글에 속하는지 나타냄
     */

    // 댓글이 속한 게시글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;



    /*
        @ManyToOne(fetch = FetchType.LAZY)
        - 다수의 댓글이 하나의 사용자에 속함
        - LAZY: 댓글 조회시 작성자 정보는 실제 필요할 때만 조회

        @JoinColumn(name = "user_id")
        - 외래키 컬럼명의 user_id로 지정
        - User 테이블의 PK와 연결
        - 이 댓글을 누가 작성했는지 나타냄
     */

    // 댓글 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
