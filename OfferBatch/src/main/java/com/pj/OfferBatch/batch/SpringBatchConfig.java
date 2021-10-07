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
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        flatFileItemReader.setResource(new ClassPathResource("input.csv"));
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
        return flatFileItemReader;
    }

    @Bean
    public OfferItemProcessor processor(){
        return new OfferItemProcessor();

    }

    @Bean
    public JdbcBatchItemWriter<Offer> writer(){
        JdbcBatchItemWriter<Offer> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO offer (descricao, desconto, active) VALUES (:descricao, :desconto, :active)");
        writer.setDataSource(this.dataSource);
        return writer;
    }

    @Bean
    public Step step() {
        return stepBuilderFactory.get("step")
                .<Offer, Offer>chunk(10)
                .reader(itemReader())
                .processor(processor())
                .writer(writer())
                .build();
    }
    @Bean
    public Step createStep() {
        return stepBuilderFactory.get("New Step Offer")
                .<Offer, Offer> chunk(1)
                .reader(offerReaderToExport)
                .writer(offerWriterToExport)
                .build();
    }

    //Export
    @Bean
    public Job exportJob() {
        return jobBuilderFactory.get("New Export of Offer")
                .incrementer(new RunIdIncrementer())
                .flow(createStep())
                .end()
                .build();
    }

    //Inport
    @Bean
    public Job importOfferJob(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get("importOfferJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step())
                .end()
                .build();
    }

//


}

