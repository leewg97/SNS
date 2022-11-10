package com.wg.sns.repository;

import com.wg.sns.model.entity.CommentEntity;
import com.wg.sns.model.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentEntityRepository extends JpaRepository<CommentEntity, Long> {
    Page<CommentEntity> findAllByPostEntity(PostEntity postEntity, Pageable pageable);
}
