package com.loopers.scheduler;

import com.loopers.config.RankingProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@EnableScheduling
@Component
@RequiredArgsConstructor
public class BatchScheduler {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private static final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final JobLauncher jobLauncher;

    private final List<Job> jobs;

    private final RankingProperties rankingProperties;


    @Scheduled(cron = "0 5 0 * * MON", zone = "Asia/Seoul")
    public void runWeeklyAggregation() throws Exception {
        final String jobName = "weeklyAggregationJob";

        LocalDate today = LocalDate.now(ZONE);
        LocalDate weekStart = today.minusWeeks(1).with(DayOfWeek.MONDAY);
        LocalDate weekEnd   = weekStart.plusDays(6);

        int weekId = toWeekId(weekEnd);

        launchJob(jobName, b -> {
            addWeights(b);
            b.addString("startDate", YMD.format(weekStart));
            b.addString("endDate",   YMD.format(weekEnd));
            b.addLong("weekId", (long) weekId);
        });
    }

    @Scheduled(cron = "0 5 0 * * MON", zone = "Asia/Seoul")
    public void runWeeklyAggregations() throws Exception {
        final String jobName = "weeklyAggregationJob";

        LocalDate today = LocalDate.now(ZONE);
        LocalDate weekStart = today.minusWeeks(1).with(DayOfWeek.MONDAY);
        LocalDate weekEnd   = weekStart.plusDays(6);

        int weekId = toWeekId(weekEnd);

        launchJob(jobName, b -> {
            addWeights(b);
            b.addString("startDate", YMD.format(weekStart));
            b.addString("endDate",   YMD.format(weekEnd));
            b.addLong("weekId", (long) weekId);
        });
    }

    private void launchJob(String jobName, Consumer<JobParametersBuilder> parameterCustomizer) throws Exception {
        Job job = jobsByName().get(jobName);
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addLong("triggeredAt", Instant.now().toEpochMilli());
        builder.addString("tz", ZONE.getId());
        if (parameterCustomizer != null) {
            parameterCustomizer.accept(builder);
        }
        JobParameters params = builder.toJobParameters();
        JobExecution exec = jobLauncher.run(job, params);
    }

    private void addWeights(JobParametersBuilder b) {
        b.addDouble("wLike",  rankingProperties.getWeight().getLike());
        b.addDouble("wSales", rankingProperties.getWeight().getSales());
        b.addDouble("wPv",    rankingProperties.getWeight().getPv());
    }

    private Map<String, Job> jobsByName() {
        return jobs.stream().collect(Collectors.toMap(Job::getName, j -> j));
    }

    private int toWeekId(LocalDate dateInWeek) {
        int y = dateInWeek.get(IsoFields.WEEK_BASED_YEAR);
        int w = dateInWeek.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        return y * 100 + w;
    }
}
