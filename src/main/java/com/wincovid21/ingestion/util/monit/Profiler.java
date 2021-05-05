package com.wincovid21.ingestion.util.monit;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class Profiler {

    private final MeterRegistry meterRegistry;

    @Autowired
    public Profiler(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public static class PromTimer {
        private final Timer timer;
        private long start;

        PromTimer(Timer timer) {
            this.timer = timer;
        }

        private void startTimer() {
            start = Clock.SYSTEM.wallTime();
        }

        public void stopTimer() {
            timer.record(Clock.SYSTEM.wallTime() - start, TimeUnit.MILLISECONDS);
        }
    }

    public void increment(String metrixName, int incrementBy) {
        meterRegistry.counter(metrixName).increment(incrementBy);
    }


    public void incrementCount(String profile, final ProfileResponseType profileResponseType) {
        if (StringUtils.isNotEmpty(profile) && profileResponseType != null) {
            String metricName = profile + "." + profileResponseType.getValue();
            incrementCount(metricName);
        } else {
            log.error("Either profile # {} is empty or profileResponseType is null.", profile);
        }
    }

    public void incrementCount(String matrixName) {
        meterRegistry.counter(matrixName).increment();
    }

    public PromTimer startTimer(String metrixName) {
        final PromTimer timer = new PromTimer(meterRegistry.timer(metrixName));
        timer.startTimer();
        return timer;
    }
}

