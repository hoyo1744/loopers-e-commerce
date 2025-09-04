package com.loopers.infrastructure.eventlog;

import com.loopers.domain.eventlog.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventLogJpaRepository extends JpaRepository<EventLog, Long> {


}
