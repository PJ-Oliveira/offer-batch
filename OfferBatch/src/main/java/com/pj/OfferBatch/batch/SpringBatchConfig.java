package com.pj.OfferBatch.batch;


import com.pj.OfferBatch.batch.processor.OfferItemProcessor;
import com.pj.OfferBatch.domain.model.Offer;
import com.pj.OfferBatch.util.JobCompletionNotificationListener;
import com.pj.OfferBatch.util.OfferReaderToExport;
import com.pj.OfferBatch.util.OfferWriterToExport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.io.ClassPathResource;


import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@Slf4j
public class SpringBatchConfig {

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

    @Bean
    public FlatFileItemReader<Offer> itemReader() {
        FlatFileItemReader<Offer> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new ClassPathResource("imput.csv"));
        flatFileItemReader.setLineMapper(new DefaultLineMapper<Offer>() {
            {
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {setNames(new String[] {"descricao", "desconto", "active"});}
                });
                setFieldSetMapper(new BeanWrapperFieldSetMapper<Offer>() {
                    {setTargetType(Offer.class);}
                });
            }
        });
        log.info("Reading {}", flatFileItemReader);
        return flatFileItemReader;
    }

    @Bean
    public OfferItemProcessor offerItemProcessor(){
        return new OfferItemProcessor();

    }

    @Bean
    public JdbcBatchItemWriter<Offer> writer(){
        JdbcBatchItemWriter<Offer> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO offer (descricao, desconto, active) VALUES (:descricao, :desconto, :active)");
        writer.setDataSource(this.dataSource);
        log.info("Writing {}", writer);
        return writer;
    }

    @Bean
    public Step importStep() {
        return stepBuilderFactory.get("Step: Import of imput.csv  Offers to DB")
                .<Offer, Offer>chunk(100)
                .reader(itemReader())
                .processor(offerItemProcessor())
                .writer(writer())
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
    public Job importOffersToDB(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get("New Import of imput.csv  Offers to DB")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(importStep())
                .end()
                .build();
    }



}

