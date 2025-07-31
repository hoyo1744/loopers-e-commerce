package com.loopers.domain.like;

import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LikeServiceIntegrationTest {

    @Autowired
    private LikeService likeService;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("상품 좋아요 조회 통합 테스트")
    @Nested
    public class Get {

        /**
         * - [O] 특정 유저가 특정 상품에 좋아요를 누른 경우, isLiked는 true를 반환한다
         * - [O] 특정 유저가 특정 상품에 좋아요를 누르지 않은 경우, isLiked는 false를 반환한다
         * - [0] 특정 상품에 대한 총 좋아요 개수를 반환한다
         * - [0] 좋아요가 없는 상품의 경우 countLikes는 0을 반환한다
         */

        @Test
        @DisplayName("특정 유저가 특정 상품에 좋아요를 누른 경우, isLiked는 true를 반환한다")
        void isLiked_returnsTrue_whenLikedExists() {
            // given
            String userId = "user123";
            Long productId = 1L;

            Like like = Like.create(userId, productId);
            likeRepository.save(like);

            LikeCommand.Check command = LikeCommand.Check.of(userId, productId);

            // when
            boolean result = likeService.isLiked(command);

            // then
            assertTrue(result);
        }

        @Test
        @DisplayName("특정 유저가 특정 상품에 좋아요를 누르지 않은 경우, isLiked는 false를 반환한다")
        void isLiked_returnsFalse_whenNoLikeExists() {
            // given
            String userId = "user123";
            Long productId = 1L;

            LikeCommand.Check command = LikeCommand.Check.of(userId, productId);

            // when
            boolean result = likeService.isLiked(command);

            // then
            assertFalse(result);
        }

        @Test
        @DisplayName("특정 상품에 대한 총 좋아요 개수를 반환한다")
        void countLikes_returnsCorrectCount() {
            // given
            Long productId = 100L;

            likeRepository.save(Like.create("user1", productId));
            likeRepository.save(Like.create("user2", productId));
            likeRepository.save(Like.create("user3", productId));

            // when
            Long likeCount = likeService.countLikes(productId);

            // then
            assertEquals(3L, likeCount);
        }

        @Test
        @DisplayName("좋아요가 없는 상품의 경우 countLikes는 0을 반환한다")
        void countLikes_returnsZero_whenNoLikes() {
            // given
            Long productId = 200L;

            // when
            Long likeCount = likeService.countLikes(productId);

            // then
            assertEquals(0L, likeCount);
        }
    }
}
