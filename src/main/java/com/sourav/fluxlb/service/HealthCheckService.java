package com.sourav.fluxlb.service;
import org.springframework.scheduling.annotation.Scheduled;
import com.sourav.fluxlb.model.BackendServer;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class HealthCheckService {

    private final RestTemplate restTemplate;
    private final LoadBalancerService loadBalancerService;

    public HealthCheckService(
            RestTemplate restTemplate,
            LoadBalancerService loadBalancerService) {

        this.restTemplate = restTemplate;
        this.loadBalancerService = loadBalancerService;
    }

    @Scheduled(fixedRate = 5000)
    public void checkServerHealth() {

        List<BackendServer> servers = loadBalancerService.getServers();

        for (BackendServer server : servers) {

            try {
                String url = server.getUrl() + "/api/hello";

                restTemplate.getForObject(url, String.class);

                server.setHealthy(true);

                System.out.println(
                        "Backend " + server.getPort() + " is UP"
                );

            } catch (Exception e) {

                server.setHealthy(false);

                System.out.println(
                        "Backend " + server.getPort() + " is DOWN"
                );
            }
        }
    }
}