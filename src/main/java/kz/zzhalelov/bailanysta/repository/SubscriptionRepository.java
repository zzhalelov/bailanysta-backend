package kz.zzhalelov.bailanysta.repository;

import kz.zzhalelov.bailanysta.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    boolean existsByFollowerAndFollowing(String follower, String following);

    Optional<Subscription> findByFollowerAndFollowing(String follower, String following);

    List<Subscription> findByFollower(String follower);
}