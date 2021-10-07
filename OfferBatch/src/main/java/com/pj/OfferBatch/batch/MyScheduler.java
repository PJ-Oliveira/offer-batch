package com.pj.OfferBatch.batch;

import com.pj.OfferBatch.batch.configbatch.BatchStepsJobs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@Slf4j
@Configuration
@EnableScheduling
public class MyScheduler {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private BatchStepsJobs batchStepsJobs;

    SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd HH:mm:ss.S");

    @Scheduled(fixedDelay = 10, initialDelay = 10)
    public void scheduleByFixerRate() throws Exception{
        log.info("Batch Job Starting");
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("time", format.format(Calendar.getInstance().getTime())).toJobParameters();
        jobLauncher.run(batchStepsJobs.exportOffersToCsv(), jobParameters);
        jobLauncher.run(batchStepsJobs.importOffersToDB(), jobParameters);
        log.info("Batch job Executed Success");

    }

}
