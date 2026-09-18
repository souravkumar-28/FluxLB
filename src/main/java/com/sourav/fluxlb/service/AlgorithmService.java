package com.sourav.fluxlb.service;

import com.sourav.fluxlb.model.LoadBalancingAlgorithm;
import org.springframework.stereotype.Service;

@Service
public class AlgorithmService {

    private LoadBalancingAlgorithm currentAlgorithm =
            LoadBalancingAlgorithm.ROUND_ROBIN;

    public LoadBalancingAlgorithm getCurrentAlgorithm() {
        return currentAlgorithm;
    }

    public void setAlgorithm(LoadBalancingAlgorithm algorithm) {
        this.currentAlgorithm = algorithm;
    }
}