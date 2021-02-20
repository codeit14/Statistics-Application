package com.n26.dao;

import com.n26.entities.Statistics;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gives Interface to create, update and access statistics data
 * Internally, it used ConcurrentHashMap since we needed thread safety and O(1) cost
 */
@Component
public class StatisticsDao {
    private final ConcurrentHashMap<Long, Statistics> secondToStatsMap;

    public StatisticsDao() {
        secondToStatsMap = new ConcurrentHashMap<>();
    }

    public void add(long second, Statistics stats) {
        secondToStatsMap.put(second, stats);
    }

    public Statistics get(long second) {
        return secondToStatsMap.get(second);
    }

    public List<Long> getAll() {
        return new ArrayList<>(secondToStatsMap.keySet());
    }

    public boolean contains(long second) {
        return secondToStatsMap.containsKey(second);
    }

    public void evictEntriesBasedOnThreshold(long evictionThresholdInSeconds) {
        secondToStatsMap.forEach((k, v) -> {
            if (k < evictionThresholdInSeconds)
                secondToStatsMap.remove(k);
        });
    }

    public void clear() {
        secondToStatsMap.clear();
    }
}
