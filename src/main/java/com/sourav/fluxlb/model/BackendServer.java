package com.sourav.fluxlb.model;

import java.util.concurrent.atomic.AtomicInteger;

public class BackendServer {

    private String host;
    private int port;
    private boolean healthy;
    private int weight;

    private int currentWeight;

    private final AtomicInteger activeConnections =
            new AtomicInteger(0);

    private final AtomicInteger totalRequests =
            new AtomicInteger(0);

    public BackendServer(String host, int port) {
        this(host, port, 1);
    }

    public BackendServer(String host, int port, int weight) {
        this.host = host;
        this.port = port;
        this.weight = weight;
        this.currentWeight = 0;
        this.healthy = true;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(int currentWeight) {
        this.currentWeight = currentWeight;
    }

    public int getActiveConnections() {
        return activeConnections.get();
    }

    public int getTotalRequests() {
        return totalRequests.get();
    }

    public void incrementRequests() {
        totalRequests.incrementAndGet();
    }

    public void incrementConnections() {
        activeConnections.incrementAndGet();
    }

    public void decrementConnections() {
        activeConnections.updateAndGet(
                value -> Math.max(0, value - 1)
        );
    }

    public String getUrl() {
        return "http://" + host + ":" + port;
    }
}