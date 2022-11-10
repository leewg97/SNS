package com.wg.sns.repository;

import com.wg.sns.model.entity.PostEntity;
import com.wg.sns.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostEntityRepository extends JpaRepository<PostEntity, Long> {
    Page<PostEntity> findAllByUserEntity(UserEntity userEntity, Pageable pageable);
}
