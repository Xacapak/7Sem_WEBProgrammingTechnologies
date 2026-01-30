package com.imperiumforum.repository;

import com.imperiumforum.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    Page<Topic> findByCategoryIdAndIsActiveTrueOrderByIsPinnedDescCreatedAtDesc(Long categoryId, Pageable pageable);

    List<Topic> findByCategoryIdAndIsPinnedTrueAndIsActiveTrueOrderByCreatedAtDesc(Long categoryId);

    List<Topic> findByCategoryIdAndIsActiveTrueOrderByCreatedAtDesc(Long categoryId, Pageable pageable);

    long countByCategoryIdAndIsActiveTrue(Long categoryId);

    @Query("SELECT COUNT(t) FROM Topic t WHERE t.category.id = ?1 AND t.isActive = true")
    int countActiveTopicsByCategory(Long categoryId);

    Topic findFirstByCategoryIdAndIsActiveTrueOrderByCreatedAtDesc(Long categoryId);

    List<Topic> findByAuthorIdAndIsActiveTrueOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<Topic> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);
}