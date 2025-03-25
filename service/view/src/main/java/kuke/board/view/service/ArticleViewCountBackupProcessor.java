package kuke.board.view.service;

import kuke.board.common.event.EventType;
import kuke.board.common.event.payload.ArticleViewEventPayload;
import kuke.board.common.outboxmessagerelay.OutBoxEventPublisher;
import kuke.board.view.entity.ArticleViewCount;
import kuke.board.view.repository.ArticleViewCountBackupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ArticleViewCountBackupProcessor {

    private final OutBoxEventPublisher outBoxEventPublisher;
    private final ArticleViewCountBackupRepository articleViewCountBackupRepository;

    @Transactional
    public void backUp(Long articleId, Long viewCount) {
        int result = articleViewCountBackupRepository.updateViewCount(articleId, viewCount);
        if(result == 0) {
            articleViewCountBackupRepository.findById(articleId)
                    .ifPresentOrElse(ignored -> {}, () -> {
                        articleViewCountBackupRepository.save(ArticleViewCount.init(articleId, viewCount));
                    });
        }

        outBoxEventPublisher.publish(
                EventType.ARTICLE_VIEWER,
                ArticleViewEventPayload.builder()
                        .articleId(articleId)
                        .articleViewCount(viewCount)
                        .build(),
                articleId
        );
    }
}
