package com.sourav.fluxlb.model;

import java.util.concurrent.atomic.AtomicLong;

public class LoadBalancerMetrics {

    private final AtomicLong totalRequests =
            new AtomicLong(0);

    public long getTotalRequests() {
        return totalRequests.get();
    }

    public void incrementRequests() {
        totalRequests.incrementAndGet();
    }
}