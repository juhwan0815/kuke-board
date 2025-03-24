package kuke.board.article.repository;

import kuke.board.article.entity.BoardArticleCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardArticleCountRepository extends JpaRepository<BoardArticleCount, Long> {

    @Query(nativeQuery = true,
            value = "update board_article_count set article_count = article_count + 1 where board_id = :boardId")
    @Modifying
    int increase(@Param("boardId") Long boardId);

    @Query(nativeQuery = true,
            value = "update board_article_count set article_count = article_count - 1 where board_id = :boardId")
    @Modifying
    int decrease(@Param("boardId") Long boardId);
}
