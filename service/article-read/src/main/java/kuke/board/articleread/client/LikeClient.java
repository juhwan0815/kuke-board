package kuke.board.articleread.client;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeClient {

    private RestClient restClient;

    @Value("${endpoints.kuke-board-like-service.url}")
    private String likeServiceUrl;

    @PostConstruct
    public void initRestClient() {
        restClient = RestClient.create(likeServiceUrl);
    }

    public long count(Long articleId) {
        try {
            return restClient.get()
                    .uri("/v1/article-likes/articles/{articleId}/count", articleId)
                    .retrieve()
                    .body(Long.class);
        } catch (Exception e) {
            log.error("[LikeClient.read] article = {}", articleId, e);
            return 0;
        }
    }

    @Getter
    public static class ArticleResponse {
        private Long articleId;
        private String title;
        private String content;
        private Long boardId;
        private Long writerId;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
    }

}
