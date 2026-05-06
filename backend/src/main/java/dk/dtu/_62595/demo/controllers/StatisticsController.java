package dk.dtu._62595.demo.controllers;

import dk.dtu._62595.demo.dto.StatisticsSummary;
import dk.dtu._62595.demo.services.StatisticsService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/statistics", produces="application/json")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/{groupId}")
    public StatisticsSummary getStatistics(
            @PathVariable UUID groupId,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end
    ) {
        return statisticsService.getGroupStatistics(groupId, start, end);
    }
}