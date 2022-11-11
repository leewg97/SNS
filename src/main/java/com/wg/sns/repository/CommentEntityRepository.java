package com.wg.sns.repository;

import com.wg.sns.model.entity.CommentEntity;
import com.wg.sns.model.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CommentEntityRepository extends JpaRepository<CommentEntity, Long> {
    Page<CommentEntity> findAllByPostEntity(PostEntity postEntity, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update CommentEntity entity set deleted_at = NOW() where entity.postEntity = :post")
    void deleteAllByPost(@Param("post") PostEntity postEntity);

}
