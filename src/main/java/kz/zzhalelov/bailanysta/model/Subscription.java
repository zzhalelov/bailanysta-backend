package kz.zzhalelov.bailanysta.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscriptions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"follower", "following"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String follower;  // Кто подписывается
    private String following; // На кого подписывается
}