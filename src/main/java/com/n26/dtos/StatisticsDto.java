package com.n26.dtos;

import com.n26.entities.Statistics;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsDto {
    private String sum;
    private String avg;
    private String max;
    private String min;
    private long count;

    public static StatisticsDto getStatisticsDto(@NonNull Statistics statistics) {
        return StatisticsDto.builder()
                .sum(statistics.getSum().toString())
                .count(statistics.getCount())
                .max(statistics.getMax().toString())
                .min(statistics.getMin().toString())
                .avg(statistics.getAvg().toString())
                .build();
    }
}
