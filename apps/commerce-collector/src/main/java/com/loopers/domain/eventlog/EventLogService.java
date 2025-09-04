package com.loopers.domain.eventlog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventLogService {

    private final EventLogRepository eventLogRepository;

    @Transactional
    public EventLog createEventLog(EventLogCommand.Event log) {
        EventLog eventLog = EventLog.create(log.getUserId(), log.getEventType(), log.getLog());
        return eventLogRepository.save(eventLog);
    }
}
