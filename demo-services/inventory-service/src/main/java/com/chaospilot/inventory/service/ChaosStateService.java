package com.chaospilot.inventory.service;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ChaosStateService {

    private final AtomicBoolean timeoutEnabled = new AtomicBoolean(false);
    private final AtomicBoolean cpuSpikeEnabled = new AtomicBoolean(false);
    private final AtomicInteger cpuSpikeDurationSeconds = new AtomicInteger(0);

    public void enableTimeout(boolean enabled) {
        timeoutEnabled.set(enabled);
    }

    public void enableCpuSpike(boolean enabled, int durationSeconds) {
        cpuSpikeEnabled.set(enabled);
        cpuSpikeDurationSeconds.set(enabled ? durationSeconds : 0);
    }

    public void reset() {
        timeoutEnabled.set(false);
        cpuSpikeEnabled.set(false);
        cpuSpikeDurationSeconds.set(0);
    }

    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("timeoutEnabled", timeoutEnabled.get());
        status.put("cpuSpikeEnabled", cpuSpikeEnabled.get());
        status.put("cpuSpikeDurationSeconds", cpuSpikeDurationSeconds.get());
        return Collections.unmodifiableMap(status);
    }
}
