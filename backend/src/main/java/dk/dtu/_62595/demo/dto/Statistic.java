package dk.dtu._62595.demo.dto;

import java.util.Map;

public record Statistic(long totalMealsPlanned, Map<String, Long> recipeCounts) {}