package kz.zzhalelov.bailanysta.repository;

import kz.zzhalelov.bailanysta.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByContentContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrderByCreatedAtDesc(String content, String author);

    List<Post> findAllByOrderByCreatedAtDesc();

    List<Post> findByAuthorOrderByCreatedAtDesc(String author);

    List<Post> findByAuthorInOrderByCreatedAtDesc(List<String> authors);
}