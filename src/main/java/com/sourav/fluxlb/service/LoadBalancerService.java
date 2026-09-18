package com.sourav.fluxlb.service;

import com.sourav.fluxlb.model.BackendServer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import com.sourav.fluxlb.model.LoadBalancingAlgorithm;
import java.util.concurrent.ThreadLocalRandom;
import com.sourav.fluxlb.model.LoadBalancerMetrics;

@Service
public class LoadBalancerService {

    private final List<BackendServer> servers = new ArrayList<>();

    private final AtomicInteger currentIndex = new AtomicInteger(0);

    private final AlgorithmService algorithmService;



    private final LoadBalancerMetrics metrics =
            new LoadBalancerMetrics();

    public LoadBalancerService(AlgorithmService algorithmService) {

        this.algorithmService = algorithmService;

        servers.add(new BackendServer("localhost", 9001, 3));
        servers.add(new BackendServer("localhost", 9002, 2));
        servers.add(new BackendServer("localhost", 9003, 1));
    }

    // Get next server using Round Robin
    public synchronized BackendServer getNextServer() {

        List<BackendServer> healthyServers = servers.stream()
                .filter(BackendServer::isHealthy)
                .toList();

        if (healthyServers.isEmpty()) {
            throw new RuntimeException("No healthy backend servers available");
        }

        int totalWeight = 0;
        BackendServer selectedServer = null;

        // Increase current weight for every healthy server
        for (BackendServer server : healthyServers) {

            server.setCurrentWeight(
                    server.getCurrentWeight() + server.getWeight()
            );

            totalWeight += server.getWeight();

            if (selectedServer == null ||
                    server.getCurrentWeight() > selectedServer.getCurrentWeight()) {

                selectedServer = server;
            }
        }

        // Reduce selected server's current weight
        selectedServer.setCurrentWeight(
                selectedServer.getCurrentWeight() - totalWeight
        );

        return selectedServer;
    }

    // Get all backend servers
    public List<BackendServer> getServers() {
        return new ArrayList<>(servers);
    }

    // Add a new backend server
    public synchronized void addServer(String host, int port) {
        servers.add(new BackendServer(host, port));
    }

    // Remove a backend server
    public synchronized void removeServer(int port) {
        servers.removeIf(server -> server.getPort() == port);

        if (!servers.isEmpty()) {
            currentIndex.set(currentIndex.get() % servers.size());
        } else {
            currentIndex.set(0);
        }
    }

    public synchronized void updateServerWeight(int port, int weight) {

        if (weight <= 0) {
            throw new IllegalArgumentException(
                    "Weight must be greater than 0"
            );
        }

        for (BackendServer server : servers) {

            if (server.getPort() == port) {
                server.setWeight(weight);
                return;
            }
        }

        throw new RuntimeException(
                "Backend server not found: " + port
        );
    }

    public synchronized BackendServer getLeastConnectionsServer() {

        List<BackendServer> healthyServers = servers.stream()
                .filter(BackendServer::isHealthy)
                .toList();

        if (healthyServers.isEmpty()) {
            throw new RuntimeException(
                    "No healthy backend servers available"
            );
        }

        BackendServer selectedServer = healthyServers.get(0);

        for (BackendServer server : healthyServers) {

            if (server.getActiveConnections()
                    < selectedServer.getActiveConnections()) {

                selectedServer = server;
            }
        }



        return selectedServer;
    }

    public synchronized BackendServer selectServer(String clientIp) {

        LoadBalancingAlgorithm algorithm =
                algorithmService.getCurrentAlgorithm();

        BackendServer selectedServer = switch (algorithm) {

            case ROUND_ROBIN ->
                    getRoundRobinServer();

            case WEIGHTED_ROUND_ROBIN ->
                    getWeightedRoundRobinServer();

            case LEAST_CONNECTIONS ->
                    getLeastConnectionsServer();

            case RANDOM ->
                    getRandomServer();

            case IP_HASH ->
                    getIpHashServer(clientIp);
        };

        selectedServer.incrementConnections();

        selectedServer.incrementRequests();

        metrics.incrementRequests();

        return selectedServer;
    }

    public LoadBalancerMetrics getMetrics() {
        return metrics;
    }

    private BackendServer getRoundRobinServer() {

        List<BackendServer> healthyServers = servers.stream()
                .filter(BackendServer::isHealthy)
                .toList();

        if (healthyServers.isEmpty()) {
            throw new RuntimeException(
                    "No healthy backend servers available"
            );
        }

        int index = currentIndex.getAndUpdate(
                value -> (value + 1) % healthyServers.size()
        );

        return healthyServers.get(index);
    }

    private BackendServer getWeightedRoundRobinServer() {

        List<BackendServer> healthyServers = servers.stream()
                .filter(BackendServer::isHealthy)
                .toList();

        if (healthyServers.isEmpty()) {
            throw new RuntimeException(
                    "No healthy backend servers available"
            );
        }

        int totalWeight = 0;
        BackendServer selectedServer = null;

        for (BackendServer server : healthyServers) {

            server.setCurrentWeight(
                    server.getCurrentWeight() + server.getWeight()
            );

            totalWeight += server.getWeight();

            if (selectedServer == null ||
                    server.getCurrentWeight() >
                            selectedServer.getCurrentWeight()) {

                selectedServer = server;
            }
        }

        selectedServer.setCurrentWeight(
                selectedServer.getCurrentWeight() - totalWeight
        );

        return selectedServer;
    }

    private BackendServer getRandomServer() {

        List<BackendServer> healthyServers = servers.stream()
                .filter(BackendServer::isHealthy)
                .toList();

        if (healthyServers.isEmpty()) {
            throw new RuntimeException(
                    "No healthy backend servers available"
            );
        }

        int index = ThreadLocalRandom.current()
                .nextInt(healthyServers.size());

        return healthyServers.get(index);
    }

    private BackendServer getIpHashServer(String clientIp) {

        List<BackendServer> healthyServers = servers.stream()
                .filter(BackendServer::isHealthy)
                .toList();

        if (healthyServers.isEmpty()) {
            throw new RuntimeException(
                    "No healthy backend servers available"
            );
        }

        int hash = Math.abs(clientIp.hashCode());

        int index = hash % healthyServers.size();

        return healthyServers.get(index);
    }
}