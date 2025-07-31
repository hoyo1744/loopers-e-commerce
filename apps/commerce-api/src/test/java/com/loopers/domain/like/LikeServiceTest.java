package com.loopers.domain.like;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private LikeService likeService;
    

    @DisplayName("상품 좋아요 등록 유닛 테스트")
    @Nested
    class Like {
        /**
         * - [O]  상품 좋아요 등록시, 이미 좋아요가 등록되어 있다면, 200 OK 와 Product already liked. 메시지가 전달된다.
         * - [O]  상품 좋아요 등록시, 좋아요가 등록되지 않았다면, 200 OK 와 Product liked successfully.메시지가 전달된다.
         */
        
        @Test
        @DisplayName("상품 좋아요 등록시, 이미 좋아요가 등록되어 있다면, 200 OK 와 Product already liked. 메시지가 전달된다.")
        public void return_200OkWithAlreadyLikeMessage_whenLikeProductLike() throws Exception{
            //given
            String userId = "test";
            Long productId = 1L;
            LikeCommand.Like command = LikeCommand.Like.of(userId, productId);

            when(likeRepository.existsByUserIdAndProductId(userId, productId)).thenReturn(true);

            //when
            boolean result = likeService.likeProduct(command);

            //then
            assertFalse(result);
            verify(likeRepository, never()).save(any());
        }

        @Test
        @DisplayName("상품 좋아요 등록시, 좋아요가 등록되지 않았다면, 200 OK 와 Product liked successfully.메시지가 전달된다.")
        public void return_200OkWithSuccessMessage_whenLikeProduct() throws Exception{
            //given
            String userId = "test";
            Long productId = 1L;
            LikeCommand.Like command = LikeCommand.Like.of(userId, productId);

            when(likeRepository.existsByUserIdAndProductId(userId, productId)).thenReturn(false);

            //when
            boolean result = likeService.likeProduct(command);

            //then
            assertTrue(result);
            verify(likeRepository).save(any());
        }
    }

    @DisplayName("상품 좋아요 취소 유닛 테스트")
    @Nested
    class Unlike {
        /**
         * - [O]  상품 좋아요 취소시, 이미 좋아요가 취소되어 있다면, 200 OK 와 Product not liked yet. 메시지가 전달된다.
         * - [O]  상품 좋아요 취소시, 좋아요가 취소되지 않았다면, 200 OK 와 Product unliked successfully. 메시지가 전달된다,.
         */

        @Test
        @DisplayName("상품 좋아요 취소시, 이미 좋아요가 취소되어 있다면, 200 OK 와 Product not liked yet. 메시지가 전달된다.")
        public void return_200OOkWithNotLikedMessage_whenUnLikeProduct() throws Exception{
            //given
            String userId = "test";
            Long productId = 1L;
            LikeCommand.Unlike command = LikeCommand.Unlike.of(userId, productId);

            when(likeRepository.existsByUserIdAndProductId(userId, productId)).thenReturn(false);

            //when
            boolean result = likeService.unLikeProduct(command);

            //then
            assertFalse(result);
            verify(likeRepository, never()).delete(userId, productId);
        }

        @Test
        @DisplayName("상품 좋아요 취소시, 좋아요가 취소되지 않았다면, 200 OK 와 Product unliked successfully. 메시지가 전달된다,.")
        public void return_200OKWithSuccessMessage_whenUnlikeProduct() throws Exception{
            //given
            String userId = "test";
            Long productId = 1L;
            LikeCommand.Unlike command = LikeCommand.Unlike.of(userId, productId);

            when(likeRepository.existsByUserIdAndProductId(userId, productId)).thenReturn(true);

            //when
            boolean result = likeService.unLikeProduct(command);

            //then
            assertTrue(result);
            verify(likeRepository).delete(userId, productId);
        }


    }


}
