package com.loopers.domain.trace;

public interface TraceEvent {
    String getEvent();

    String getUserId();

    Object getPayload();

}
