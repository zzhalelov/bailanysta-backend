package kz.zzhalelov.bailanysta.config;

import kz.zzhalelov.bailanysta.model.Comment;
import kz.zzhalelov.bailanysta.model.Post;
import kz.zzhalelov.bailanysta.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PostRepository postRepository;

    @Override
    public void run(String... args) {
        if (postRepository.count() == 0) {
            Post post1 = Post.builder()
                    .author("Arman")
                    .content("Привет всем в Bailanysta! Тестирую новый интерфейс 🚀")
                    .likesCount(3)
                    .comments(List.of(
                            Comment.builder().author("Aruzhan").text("Отличный дизайн!").build(),
                            Comment.builder().author("Zhastilek").text("Добро пожаловать!").build()
                    ))
                    .build();

            Post post2 = Post.builder()
                    .author("Aruzhan")
                    .content("Кто-нибудь уже пробовал сгенерировать пост с помощью ИИ?")
                    .likesCount(5)
                    .comments(List.of(
                            Comment.builder().author("Nariman").text("Да, работает супер!").build()
                    ))
                    .build();

            postRepository.saveAll(List.of(post1, post2));
        }
    }
}