package com.imperiumforum.repository;

import com.imperiumforum.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByTopicIdAndIsActiveTrueOrderByCreatedAtAsc(Long topicId, Pageable pageable);

    List<Post> findByTopicIdAndIsActiveTrueOrderByCreatedAtAsc(Long topicId);

    long countByTopicIdAndIsActiveTrue(Long topicId);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.topic.id = ?1 AND p.isActive = true")
    int countActivePostsByTopic(Long topicId);

    Post findFirstByTopicIdAndIsActiveTrueOrderByCreatedAtDesc(Long topicId);

    List<Post> findByAuthorIdAndIsActiveTrueOrderByCreatedAtDesc(Long userId, Pageable pageable);

    long countByAuthorIdAndIsActiveTrue(Long userId);

    long countByTopicCategoryIdAndIsActiveTrue(Long categoryId);
}