package com.prograngers.backend.repository;

import com.prograngers.backend.entity.Comment;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Slf4j
class CommentRepositoryTest {

    @Autowired
    private  CommentRepository commentRepository;

    @Test
    void 저장_테스트(){
        // given
        Comment comment = Comment.builder()
                .content("댓글 내용")
                .build();

        // when
        Comment saved = commentRepository.save(comment);

        // then
        Assertions.assertThat(saved).isEqualTo(comment);
    }

    @Test
    void 수정_테스트(){
        // given
        Comment comment = Comment.builder()
                .content("댓글 내용")
                .build();
        commentRepository.save(comment);

        // when
        comment.updateContent("수정 내용");
        Comment updated = commentRepository.save(comment);

        // then
        Assertions.assertThat(updated).isEqualTo(comment);
    }



}