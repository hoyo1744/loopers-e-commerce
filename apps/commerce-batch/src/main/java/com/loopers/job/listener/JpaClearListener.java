package com.loopers.job.listener;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaClearListener implements ChunkListener {
    private final EntityManager em;

    @Override
    public void afterChunk(ChunkContext context) {
        em.clear();
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        em.clear();
    }
}
