package com.loopers.infrastructure.eventhandled;

import com.loopers.domain.eventhandled.EventHandled;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface EventHandledJpaRepository extends JpaRepository<EventHandled, String> {
    @Modifying
    @Transactional
    @Query(value = """
    INSERT IGNORE INTO event_handled (event_id, topic, partition_no, offset_val, processed_at)
    VALUES (:eventId, :topic, :partitionNo, :offsetVal, NOW())
    """, nativeQuery = true)
    int markHandled(@Param("eventId") String eventId,
                    @Param("topic") String topic,
                    @Param("partitionNo") int partitionNo,
                    @Param("offsetVal") long offsetVal);
}
