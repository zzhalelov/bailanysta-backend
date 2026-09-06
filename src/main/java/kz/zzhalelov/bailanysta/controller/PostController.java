package kz.zzhalelov.bailanysta.controller;

import kz.zzhalelov.bailanysta.model.Comment;
import kz.zzhalelov.bailanysta.model.Post;
import kz.zzhalelov.bailanysta.model.Subscription;
import kz.zzhalelov.bailanysta.repository.PostRepository;
import kz.zzhalelov.bailanysta.repository.SubscriptionRepository;
import kz.zzhalelov.bailanysta.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = {"http://localhost:5173", "https://bailanysta.zzhalelov.dev"})
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;
    private final GeminiService geminiService;
    private final SubscriptionRepository subscriptionRepository;

    @GetMapping
    public List<Post> getAllPosts(@RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) {
            return postRepository.findByContentContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrderByCreatedAtDesc(search, search);
        }
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    @GetMapping("/user/{author}")
    public List<Post> getUserPosts(@PathVariable String author) {
        return postRepository.findByAuthorOrderByCreatedAtDesc(author);
    }

    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postRepository.save(post);
    }

    @PostMapping("/{id}/like")
    public Post likePost(@PathVariable Long id) {
        Post post = postRepository.findById(id).orElseThrow();
        post.setLikesCount(post.getLikesCount() + 1);
        return postRepository.save(post);
    }

    @PostMapping("/{id}/comments")
    public Post addComment(@PathVariable Long id, @RequestBody Comment comment) {
        Post post = postRepository.findById(id).orElseThrow();
        post.getComments().add(comment);
        return postRepository.save(post);
    }

    @PostMapping("/ai-generate")
    public Map<String, String> generateAiContent(@RequestBody Map<String, String> request) {
        String topic = request.getOrDefault("topic", "Программирование и IT");
        String generatedText = geminiService.generatePostContent(topic);
        return Map.of("text", generatedText);
    }

    // Получить ленту только тех, на кого подписан текущий пользователь
    @GetMapping("/feed/subscriptions")
    public List<Post> getSubscriptionsFeed(@RequestParam String follower) {
        List<String> followings = subscriptionRepository.findByFollower(follower)
                .stream()
                .map(Subscription::getFollowing)
                .toList();

        if (followings.isEmpty()) {
            return List.of();
        }
        return postRepository.findByAuthorInOrderByCreatedAtDesc(followings);
    }

    // Тоггл подписки (подписаться / отписаться)
    @PostMapping("/subscriptions/toggle")
    public Map<String, Object> toggleSubscription(@RequestBody Map<String, String> request) {
        String follower = request.get("follower");
        String following = request.get("following");

        if (follower.equalsIgnoreCase(following)) {
            return Map.of("subscribed", false, "message", "Нельзя подписаться на самого себя");
        }

        var existing = subscriptionRepository.findByFollowerAndFollowing(follower, following);
        boolean isSubscribed;

        if (existing.isPresent()) {
            subscriptionRepository.delete(existing.get());
            isSubscribed = false;
        } else {
            subscriptionRepository.save(Subscription.builder().follower(follower).following(following).build());
            isSubscribed = true;
        }

        return Map.of("subscribed", isSubscribed);
    }

    // Проверить статус подписки
    @GetMapping("/subscriptions/status")
    public Map<String, Boolean> getSubscriptionStatus(@RequestParam String follower, @RequestParam String following) {
        boolean isSubscribed = subscriptionRepository.existsByFollowerAndFollowing(follower, following);
        return Map.of("subscribed", isSubscribed);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, @RequestParam String author) {
        var postOptional = postRepository.findById(id);
        if (postOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Post post = postOptional.get();
        if (!post.getAuthor().equalsIgnoreCase(author)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        postRepository.delete(post);
        return ResponseEntity.noContent().build();
    }
}