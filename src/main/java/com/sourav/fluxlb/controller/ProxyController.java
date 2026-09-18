package com.sourav.fluxlb.controller;

import com.sourav.fluxlb.model.BackendServer;
import com.sourav.fluxlb.service.LoadBalancerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ProxyController {

    private final LoadBalancerService loadBalancerService;
    private final RestTemplate restTemplate;

    public ProxyController(
            LoadBalancerService loadBalancerService,
            RestTemplate restTemplate) {

        this.loadBalancerService = loadBalancerService;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/api/hello")
    public ResponseEntity<String> forwardRequest(
            jakarta.servlet.http.HttpServletRequest request) {

        String clientIp = request.getRemoteAddr();

        BackendServer server =
                loadBalancerService.selectServer(clientIp);

        try {
            String url = server.getUrl() + "/api/hello";

            String response = restTemplate.getForObject(
                    url,
                    String.class
            );

            return ResponseEntity.ok(response);

        } finally {
            server.decrementConnections();
        }
    }

    @GetMapping("/api/slow")
    public ResponseEntity<String> forwardSlowRequest(
            jakarta.servlet.http.HttpServletRequest request) {

        String clientIp = request.getRemoteAddr();

        BackendServer server =
                loadBalancerService.selectServer(clientIp);

        try {
            String url = server.getUrl() + "/api/slow";

            String response = restTemplate.getForObject(
                    url,
                    String.class
            );

            return ResponseEntity.ok(response);

        } finally {
            server.decrementConnections();
        }
    }
}