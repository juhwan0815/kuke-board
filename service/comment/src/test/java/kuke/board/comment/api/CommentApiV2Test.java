package kuke.board.comment.api;

import kuke.board.comment.service.response.CommentPageResponse;
import kuke.board.comment.service.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class CommentApiV2Test {

    RestClient restClient = RestClient.create("http://localhost:9001");

    @Test
    void create() {
        CommentResponse response1 = create(new CommentCreateRequestV2(1L, "my comment1", null, 1L));
        CommentResponse response2 = create(new CommentCreateRequestV2(1L, "my comment2", response1.getPath(), 1L));
        CommentResponse response3 = create(new CommentCreateRequestV2(1L, "my comment3", response2.getPath(), 1L));

        System.out.println("response1.getPath() = " + response1.getPath());
        System.out.println("\tresponse2.getPath() = " + response2.getPath());
        System.out.println("\t\tresponse3.getPath() = " + response3.getPath());

        System.out.println("response1.getCommentId() = " + response1.getCommentId());
        System.out.println("\tresponse2.getCommentId() = " + response2.getCommentId());
        System.out.println("\t\tresponse3.getCommentId() = " + response3.getCommentId());


        /**
         * response1.getPath() = 00002
         * 	response2.getPath() = 0000200000
         * 		response3.getPath() = 000020000000000
         * response1.getCommentId() = 158471180741742592
         * 	response2.getCommentId() = 158471181152784384
         * 		response3.getCommentId() = 158471181224087552
         */
    }

    @Test
    void read() {
        restClient.get()
                .uri("/v2/comments/{commentId}", 158471181224087552L)
                .retrieve()
                .body(CommentResponse.class);
    }

    @Test
    void delete() {
        restClient.delete()
                .uri("/v2/comments/{commentId}", 158471181224087552L)
                .retrieve();
    }

    @Test
    void readAll() {
        CommentPageResponse response = restClient.get()
                .uri("/v2/comments?articleId=1&pageSize=10&page=1")
                .retrieve()
                .body(CommentPageResponse.class);

        System.out.println("response.getCommentCount() = " + response.getCommentCount());
        for (CommentResponse comment : response.getComments()) {
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }
        /**
         * comment.getCommentId() = 158469796021637120
         * comment.getCommentId() = 158469797674192896
         * comment.getCommentId() = 158469797766467584
         * comment.getCommentId() = 158470874037456896
         * comment.getCommentId() = 158470874473664512
         * comment.getCommentId() = 158470874553356288
         * comment.getCommentId() = 158471180741742592
         * comment.getCommentId() = 158471181152784384
         * comment.getCommentId() = 158473227052081152
         * comment.getCommentId() = 158473227224047619
         */
    }

    @Test
    void readAllInfiniteScroll() {
        List<CommentResponse> responses1 = restClient.get()
                .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });

        System.out.println("firstPage");
        for (CommentResponse response : responses1) {
            System.out.println("response.getCommentId() = " + response.getCommentId());
        }

        String lastPath = responses1.getLast().getPath();
        List<CommentResponse> responses2 = restClient.get()
                .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5&lastPath=%s".formatted(lastPath))
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });

        System.out.println("secondPage");
        for (CommentResponse response : responses2) {
            System.out.println("response.getCommentId() = " + response.getCommentId());
        }

        /**
         * firstPage
         * response.getCommentId() = 158469796021637120
         * response.getCommentId() = 158469797674192896
         * response.getCommentId() = 158469797766467584
         * response.getCommentId() = 158470874037456896
         * response.getCommentId() = 158470874473664512
         * secondPage
         * response.getCommentId() = 158470874553356288
         * response.getCommentId() = 158471180741742592
         * response.getCommentId() = 158471181152784384
         * response.getCommentId() = 158473227052081152
         * response.getCommentId() = 158473227224047619
         */
    }

    @Test
    void countTest() {
        CommentResponse commentResponse = create(new CommentCreateRequestV2(2L, "my comment1", null, 1L));

        Long count1 = restClient.get()
                .uri("/v2/comments/articles/{articleId}/count", 2L)
                .retrieve()
                .body(Long.class);

        System.out.println("count1 = " + count1);

        restClient.delete()
                .uri("/v2/comments/{commentId}", commentResponse.getCommentId())
                .retrieve();


        Long count2 = restClient.get()
                .uri("/v2/comments/articles/{articleId}/count", 2L)
                .retrieve()
                .body(Long.class);

        System.out.println("count1 = " + count2);
    }

    CommentResponse create(CommentCreateRequestV2 request) {
        return restClient.post()
                .uri("/v2/comments")
                .body(request)
                .retrieve()
                .body(CommentResponse.class);
    }

    @Getter
    @AllArgsConstructor
    static class CommentCreateRequestV2 {

        private Long articleId;
        private String content;
        private String parentPath;
        private Long writerId;

    }
}
