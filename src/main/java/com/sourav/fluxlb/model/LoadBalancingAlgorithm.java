package com.sourav.fluxlb.model;

public enum LoadBalancingAlgorithm {

    ROUND_ROBIN,
    WEIGHTED_ROUND_ROBIN,
    LEAST_CONNECTIONS,
    RANDOM,
    IP_HASH
}