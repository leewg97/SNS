package com.wg.sns.repository;

import com.wg.sns.model.entity.LikeEntity;
import com.wg.sns.model.entity.PostEntity;
import com.wg.sns.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeEntityRepository extends JpaRepository<LikeEntity, Long> {
    Optional<LikeEntity> findByUserEntityAndPostEntity(UserEntity userEntity, PostEntity postEntity);

    @Query(value = "SELECT COUNT(*) FROM LikeEntity entity WHERE entity.postEntity =:post")
    Long countByPostEntity(@Param("post") PostEntity postEntity);
}
