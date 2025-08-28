package com.loopers.domain.external;

import org.springframework.stereotype.Service;

@Service

public class ExternalDataService {

    public void send(String label, ExternalPayload payload) {
        try {
            Thread.sleep(1000);
            System.out.println(String.format("label: %s, payload: %s", label, payload));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
