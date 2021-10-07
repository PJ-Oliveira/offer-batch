package com.pj.OfferBatch.batch.configbatch;


import com.pj.OfferBatch.batch.processor.OfferItemProcessor;
import com.pj.OfferBatch.domain.model.Offer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@Slf4j
public class BatchStepsJobs {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    @Autowired
    public DataSource dataSource;
    @Autowired
    private OfferReaderToExport offerReaderToExport;
    @Autowired
    private OfferWriterToExport offerWriterToExport;

    @Autowired
    private OfferReaderToImport offerReaderToImport;
    @Autowired
    private OfferWriterToImport offerWriterToImportToDB;

    @Bean
    public OfferItemProcessor offerItemProcessor(){
        return new OfferItemProcessor();
    }
    @Bean
    public OfferReaderToImport offerReaderToImport(){
        return new OfferReaderToImport();
    }

    @Bean
    public Step importStep() {
        return stepBuilderFactory.get("Step: Import of imput.csv  Offers to DB")
                .<Offer, Offer>chunk(100)
                .reader(offerReaderToImport.itemOfferReaderToImport())
                .processor(offerItemProcessor())
                .writer(offerWriterToImportToDB.offerWriterToImportToDb())
                .build();
    }

    @Bean
    public Step exportStep() {
        return stepBuilderFactory.get("Step: Export of Offers to output.csv")
                .<Offer, Offer> chunk(100)
                .reader(offerReaderToExport)
                .writer(offerWriterToExport)
                .build();
    }

    @Bean
    public Job exportOffersToCsv() {
        return jobBuilderFactory.get("New Export of Offers to output.csv")
                .incrementer(new RunIdIncrementer())
                .flow(exportStep())
                .end()
                .build();
    }

    @Bean
    public Job importOffersToDB() {
        return jobBuilderFactory.get("New Import of imput.csv  Offers to DB")
                .incrementer(new RunIdIncrementer())
                .flow(importStep())
                .end()
                .build();
    }
}

