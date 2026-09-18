package com.sourav.fluxlb.controller;

import com.sourav.fluxlb.model.LoadBalancingAlgorithm;
import com.sourav.fluxlb.service.AlgorithmService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/config")
@CrossOrigin(origins = "http://localhost:5173")
public class AlgorithmController {

    private final AlgorithmService algorithmService;

    public AlgorithmController(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    @GetMapping("/algorithm")
    public String getAlgorithm() {
        return algorithmService
                .getCurrentAlgorithm()
                .name();
    }

    @PostMapping("/algorithm")
    public String setAlgorithm(
            @RequestParam LoadBalancingAlgorithm algorithm) {

        algorithmService.setAlgorithm(algorithm);

        return "Load balancing algorithm changed to: "
                + algorithm.name();
    }
}