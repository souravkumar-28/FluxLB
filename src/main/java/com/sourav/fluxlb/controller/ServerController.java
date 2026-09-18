package com.sourav.fluxlb.controller;

import com.sourav.fluxlb.model.BackendServer;
import com.sourav.fluxlb.service.LoadBalancerService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.sourav.fluxlb.model.LoadBalancerMetrics;

import java.util.List;

@RestController
@RequestMapping("/api/servers")
@CrossOrigin(origins = "http://localhost:5173")
public class ServerController {

    private final LoadBalancerService loadBalancerService;

    public ServerController(LoadBalancerService loadBalancerService) {
        this.loadBalancerService = loadBalancerService;
    }

    // Get all backend servers
    @GetMapping
    public List<BackendServer> getServers() {
        return loadBalancerService.getServers();
    }

    @GetMapping("/metrics")
    public LoadBalancerMetrics getMetrics() {
        return loadBalancerService.getMetrics();
    }

    // Add a backend server
    @PostMapping
    public String addServer(
            @RequestParam String host,
            @RequestParam int port) {

        loadBalancerService.addServer(host, port);

        return "Backend server added: " + host + ":" + port;
    }

    // Remove a backend server
    @DeleteMapping("/{port}")
    public String removeServer(@PathVariable int port) {

        loadBalancerService.removeServer(port);

        return "Backend server removed: " + port;
    }

    @PutMapping("/{port}/weight")
    public String updateWeight(
            @PathVariable int port,
            @RequestParam int weight) {

        loadBalancerService.updateServerWeight(port, weight);

        return "Backend server weight updated: "
                + port + " -> " + weight;
    }
}